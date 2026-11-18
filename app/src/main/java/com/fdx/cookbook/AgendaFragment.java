package com.fdx.cookbook;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Meal agenda: one line per day, today at the top on opening, endless scrolling towards the
 * past (up) and the future (down). Two tabs: family agenda and personal agenda.
 * Days are built locally; entries are read from the server by windows of days.
 */
public class AgendaFragment extends Fragment {

    /** Number of days added at once when the list reaches one of its ends */
    private static final int CHUNK_DAYS = 28;
    /** Extend the list when fewer than this number of days remain before an end */
    private static final int EDGE_DAYS = 7;
    private static final String PREFS = "agenda";
    private static final String PREF_HINT_COUNT = "hint_long_press";
    private static final int HINT_TIMES = 3;

    private static final DateTimeFormatter DATE_SHORT = DateTimeFormatter.ofPattern("d MMM", Locale.FRANCE);
    private static final DateTimeFormatter DATE_YEAR = DateTimeFormatter.ofPattern("d MMM yyyy", Locale.FRANCE);

    private AgendaApi mApi;
    private AgendaEntry.Scope mScope = AgendaEntry.Scope.FAMILY;
    private LocalDate mToday;
    private LocalDate mFirstDay;     // day shown at position 0
    private int mDayCount;           // number of days in the list
    private final Map<LocalDate, List<AgendaEntry>> mEntries = new HashMap<>();
    /** Incremented when the shown data is reset: answers of older requests are ignored */
    private int mGeneration;
    /** Generation for which a loading error was already shown */
    private int mWarnedGeneration = -1;
    /** Requests of the current generation still waiting for their answer */
    private int mPending;
    /** A request of the current generation failed */
    private boolean mFailed;
    /** No server: the local copy is shown and nothing can be changed */
    private boolean mReadOnly;
    /** A recipe outside the cookbook is being downloaded */
    private boolean mLoadingRecipe;
    private AgendaCache mCache;
    /** When the shown copy was last confirmed by the server (null: no copy) */
    private LocalDateTime mSavedAt;
    private static final DateTimeFormatter SAVED_AT = DateTimeFormatter.ofPattern("d MMM 'à' HH'h'mm", Locale.FRANCE);

    private RecyclerView mRecycler;
    private LinearLayoutManager mLayout;
    private DayAdapter mAdapter;
    private TextView mTabFamily;
    private TextView mTabPerso;
    private String[] mDayNames;

    public static AgendaFragment newInstance() {
        return new AgendaFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_agenda, container, false);
        requireActivity().setTitle(R.string.PAG_title);
        mApi = new AgendaApi(requireContext());
        mCache = new AgendaCache(requireContext(), SessionInfo.get(requireContext()).getUser().getId());
        mDayNames = getResources().getStringArray(R.array.PAG_days);

        mTabFamily = v.findViewById(R.id.agenda_tab_family);
        mTabPerso = v.findViewById(R.id.agenda_tab_perso);
        mTabFamily.setOnClickListener(t -> selectScope(AgendaEntry.Scope.FAMILY));
        mTabPerso.setOnClickListener(t -> selectScope(AgendaEntry.Scope.PERSO));
        showTabs();

        showLongPressHint();

        mToday = LocalDate.now();
        mFirstDay = mToday.minusDays(CHUNK_DAYS);
        mDayCount = 3 * CHUNK_DAYS;

        mRecycler = v.findViewById(R.id.agenda_recycler_view);
        mLayout = new LinearLayoutManager(getActivity());
        mRecycler.setLayoutManager(mLayout);
        mAdapter = new DayAdapter();
        mRecycler.setAdapter(mAdapter);
        mLayout.scrollToPositionWithOffset(CHUNK_DAYS, 0);   // today at the top
        mRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView rv, int dx, int dy) {
                // Changing the adapter is not allowed during a scroll callback
                rv.post(AgendaFragment.this::extendIfNeeded);
            }
        });
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Read again on each return: other members may have changed the family agenda
        reloadAll();
    }

    /** The long press on a photo cannot be guessed: tell it the first HINT_TIMES openings */
    private void showLongPressHint() {
        try {
            SharedPreferences prefs = requireContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
            int n = prefs.getInt(PREF_HINT_COUNT, 0);
            if (n >= HINT_TIMES) return;
            prefs.edit().putInt(PREF_HINT_COUNT, n + 1).apply();
            Toast.makeText(requireContext(), R.string.PAG_hint_long_press, Toast.LENGTH_LONG).show();
        } catch (Exception ignored) {
            // a hint is not worth any failure
        }
    }

    // ------------------------------------------------------------------ tabs and loading

    private void selectScope(AgendaEntry.Scope scope) {
        if (scope == mScope) return;
        mScope = scope;
        showTabs();
        mEntries.clear();   // the other agenda: start from its own local copy
        reloadAll();
    }

    private void showTabs() {
        int on = ContextCompat.getColor(requireContext(), R.color.colorPrimary);
        int off = ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark);
        mTabFamily.setBackgroundColor(mScope == AgendaEntry.Scope.FAMILY ? on : off);
        mTabPerso.setBackgroundColor(mScope == AgendaEntry.Scope.PERSO ? on : off);
    }

    /**
     * Reads again all the days of the list. What is shown stays on screen until the server
     * answers (on first display, or after a tab change, the local copy is shown at once).
     */
    private void reloadAll() {
        mGeneration++;
        mPending = 0;
        mFailed = false;
        if (mEntries.isEmpty()) showLocalCopy();
        fetch(mFirstDay, mFirstDay.plusDays(mDayCount - 1));
    }

    private void showLocalCopy() {
        AgendaCache.Snapshot snap = mCache.load(mScope);
        mEntries.clear();
        for (AgendaEntry e : snap.entries) dayList(e.getDate()).add(e);
        mSavedAt = snap.savedAt;
        mAdapter.notifyDataSetChanged();
    }

    /** Saves the days of the list as the local copy (called after a server success only) */
    private void saveLocalCopy() {
        LocalDate last = mFirstDay.plusDays(mDayCount - 1);
        List<AgendaEntry> all = new ArrayList<>();
        for (Map.Entry<LocalDate, List<AgendaEntry>> d : mEntries.entrySet()) {
            if (!d.getKey().isBefore(mFirstDay) && !d.getKey().isAfter(last)) all.addAll(d.getValue());
        }
        mCache.save(mScope, all);
        mSavedAt = LocalDateTime.now();
    }

    /** Reads the entries of the days from..to (split to respect the server limit) */
    private void fetch(LocalDate from, LocalDate to) {
        final int generation = mGeneration;
        final AgendaEntry.Scope scope = mScope;
        LocalDate start = from;
        while (!start.isAfter(to)) {
            LocalDate end = start.plusDays(AgendaApi.MAX_DAYS_PER_READ - 1);
            if (end.isAfter(to)) end = to;
            final LocalDate s = start, e = end;
            mPending++;
            mApi.getEntries(scope, s, e, (status, list) -> {
                if (generation != mGeneration || !isAdded()) return;   // outdated answer
                mPending--;
                if (status != AgendaApi.Status.OK) {
                    mFailed = true;
                    enterReadOnly(generation);
                    return;
                }
                // The server answer replaces what was shown for these days
                for (LocalDate d = s; !d.isAfter(e); d = d.plusDays(1)) mEntries.remove(d);
                for (AgendaEntry entry : list) dayList(entry.getDate()).add(entry);
                int p1 = position(s), p2 = position(e);
                if (p1 >= 0 && p2 >= 0) mAdapter.notifyItemRangeChanged(p1, p2 - p1 + 1);
                if (mPending == 0 && !mFailed) {
                    leaveReadOnly();
                    saveLocalCopy();
                }
            });
            start = end.plusDays(1);
        }
    }

    /** No answer from the server: show the local copy in read-only mode */
    private void enterReadOnly(int generation) {
        if (!mReadOnly) {
            mReadOnly = true;
            mAdapter.notifyDataSetChanged();   // hides the + buttons
        }
        setSubtitle(getString(R.string.PAG_readonly_subtitle));
        // One message per loading, even when it was split in several requests
        if (mWarnedGeneration != generation) {
            mWarnedGeneration = generation;
            String msg = (mSavedAt == null)
                    ? getString(R.string.PAG_readonly_nocopy)
                    : getString(R.string.PAG_readonly_since, mSavedAt.format(SAVED_AT));
            Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show();
        }
    }

    private void leaveReadOnly() {
        if (!mReadOnly) return;
        mReadOnly = false;
        setSubtitle(null);
        mAdapter.notifyDataSetChanged();
    }

    private void setSubtitle(String s) {
        if (getActivity() instanceof AppCompatActivity) {
            ActionBar bar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (bar != null) bar.setSubtitle(s);
        }
    }

    /** True (and a message is shown) when changes are not possible */
    private boolean refuseIfReadOnly() {
        if (mReadOnly) {
            Toast.makeText(requireContext(), R.string.PAG_readonly_action, Toast.LENGTH_SHORT).show();
        }
        return mReadOnly;
    }

    /** Adds days before or after the list when the user comes close to one of its ends */
    private void extendIfNeeded() {
        if (!isAdded()) return;
        int first = mLayout.findFirstVisibleItemPosition();
        int last = mLayout.findLastVisibleItemPosition();
        if (first == RecyclerView.NO_POSITION) return;
        if (first < EDGE_DAYS) {
            View top = mLayout.findViewByPosition(first);
            int offset = (top == null) ? 0 : top.getTop();
            LocalDate oldFirst = mFirstDay;
            mFirstDay = mFirstDay.minusDays(CHUNK_DAYS);
            mDayCount += CHUNK_DAYS;
            mAdapter.notifyItemRangeInserted(0, CHUNK_DAYS);
            mLayout.scrollToPositionWithOffset(first + CHUNK_DAYS, offset);   // keep what is on screen
            fetch(mFirstDay, oldFirst.minusDays(1));
        }
        if (last > mDayCount - EDGE_DAYS) {
            LocalDate from = mFirstDay.plusDays(mDayCount);
            int oldCount = mDayCount;
            mDayCount += CHUNK_DAYS;
            mAdapter.notifyItemRangeInserted(oldCount, CHUNK_DAYS);
            fetch(from, from.plusDays(CHUNK_DAYS - 1));
        }
    }

    private int position(LocalDate d) {
        long p = ChronoUnit.DAYS.between(mFirstDay, d);
        return (p < 0 || p >= mDayCount) ? -1 : (int) p;
    }

    private List<AgendaEntry> dayList(LocalDate d) {
        List<AgendaEntry> l = mEntries.get(d);
        if (l == null) { l = new ArrayList<>(); mEntries.put(d, l); }
        return l;
    }

    private void refreshDay(LocalDate d) {
        int p = position(d);
        if (p >= 0) mAdapter.notifyItemChanged(p);
    }

    // ------------------------------------------------------------------ actions on entries

    private void onEntryClick(AgendaEntry e) {
        if (!e.isRecipe()) {
            if (refuseIfReadOnly()) return;
            AgendaEditDialog.showEdit(requireContext(), e, mApi, saved -> replaceEntry(e, saved), () -> confirmDelete(e));
            return;
        }
        Recipe r = CookBook.get(requireContext()).getRecipe(e.getRecipeId());
        if (r != null && r.IsVisible()) {
            startActivity(RecipeDisplayActivity.newIntent(requireContext(), r.getId()));
            return;
        }
        // Not in the local cookbook: download it and show it read-only
        if (mReadOnly) {
            Toast.makeText(requireContext(), R.string.PAG_recipe_offline, Toast.LENGTH_LONG).show();
            return;
        }
        if (mLoadingRecipe) return;   // a download is already running (double tap)
        mLoadingRecipe = true;
        Toast.makeText(requireContext(), R.string.PAG_recipe_loading, Toast.LENGTH_SHORT).show();
        mApi.getRecipe(e.getRecipeId(), (status, recipe) -> {
            mLoadingRecipe = false;
            if (!isAdded()) return;
            if (status == AgendaApi.Status.OK) {
                startActivity(RecipeDisplayActivity.newGuestIntent(requireContext(), recipe));
            } else {
                Toast.makeText(requireContext(), status == AgendaApi.Status.OFFLINE
                        ? R.string.PAG_offline_short : R.string.PAG_recipe_unavailable, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void onEntryLongClick(AgendaEntry e) {
        if (refuseIfReadOnly()) return;
        String[] items = {getString(R.string.PAG_edit), getString(R.string.PAG_delete)};
        new AlertDialog.Builder(requireContext())
                .setTitle(e.getLabel())
                .setItems(items, (d, which) -> {
                    if (which == 0) {
                        AgendaEditDialog.showEdit(requireContext(), e, mApi, saved -> replaceEntry(e, saved), () -> confirmDelete(e));
                    } else {
                        confirmDelete(e);
                    }
                })
                .show();
    }

    private void onAddClick(LocalDate day, AgendaEntry.Meal meal) {
        AgendaEntry empty = new AgendaEntry(mScope, day, meal);
        AgendaEditDialog.show(requireContext(), empty, true, mApi, saved -> {
            if (!isAdded()) return;
            dayList(saved.getDate()).add(saved);
            refreshDay(saved.getDate());
            saveLocalCopy();
        });
    }

    /** Removes the entry having this id (the list may hold a newer object read from the server) */
    private int removeById(LocalDate day, AgendaEntry e) {
        List<AgendaEntry> l = dayList(day);
        for (int i = 0; i < l.size(); i++) {
            if (l.get(i).getId().equals(e.getId())) { l.remove(i); return i; }
        }
        return -1;
    }

    private void confirmDelete(AgendaEntry e) {
        String msg = getString(R.string.PAG_confirm_delete, e.getLabel(),
                AgendaEditDialog.mealName(requireContext(), e.getMeal()),
                e.getDate().format(AgendaEditDialog.DAY_LONG));
        new AlertDialog.Builder(requireContext())
                .setMessage(msg)
                .setPositiveButton(R.string.PAG_delete, (d, w) -> mApi.delete(e.getId(), (status, unused) -> {
                    if (!isAdded()) return;
                    if (status == AgendaApi.Status.OK) {
                        removeById(e.getDate(), e);
                        refreshDay(e.getDate());
                        saveLocalCopy();
                    } else {
                        Toast.makeText(requireContext(), AgendaEditDialog.statusMessage(status), Toast.LENGTH_LONG).show();
                    }
                }))
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void replaceEntry(AgendaEntry old, AgendaEntry saved) {
        if (!isAdded()) return;
        int i = removeById(old.getDate(), old);
        List<AgendaEntry> target = dayList(saved.getDate());
        if (old.getDate().equals(saved.getDate()) && i >= 0) {
            target.add(Math.min(i, target.size()), saved);   // same day: keep its place
        } else {
            target.add(saved);
        }
        refreshDay(old.getDate());
        refreshDay(saved.getDate());
        saveLocalCopy();
    }

    // ------------------------------------------------------------------ list

    private class DayHolder extends RecyclerView.ViewHolder {
        private final View mRoot;
        private final TextView mDayName;
        private final TextView mDate;
        private final LinearLayout mLunch;
        private final LinearLayout mDinner;

        DayHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_agenda_day, parent, false));
            mRoot = itemView.findViewById(R.id.agenda_day_root);
            mDayName = itemView.findViewById(R.id.agenda_day_name);
            mDate = itemView.findViewById(R.id.agenda_day_date);
            mLunch = itemView.findViewById(R.id.agenda_lunch);
            mDinner = itemView.findViewById(R.id.agenda_dinner);
        }

        void bind(LocalDate day) {
            mDayName.setText(mDayNames[day.getDayOfWeek().getValue() - 1]);
            mDate.setText(day.format(day.getYear() == mToday.getYear() ? DATE_SHORT : DATE_YEAR));
            if (day.equals(mToday)) {
                mRoot.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark));
            } else {
                mRoot.setBackgroundResource(R.drawable.list_item_bg);
            }
            List<AgendaEntry> entries = mEntries.get(day);
            fillMeal(mLunch, day, AgendaEntry.Meal.LUNCH, entries);
            fillMeal(mDinner, day, AgendaEntry.Meal.DINNER, entries);
        }

        private void fillMeal(LinearLayout box, LocalDate day, AgendaEntry.Meal meal, List<AgendaEntry> entries) {
            box.removeAllViews();
            int count = 0;
            if (entries != null) {
                for (AgendaEntry e : entries) {
                    if (e.getMeal() != meal) continue;
                    count++;
                    TextView tv = newLine();
                    if (e.isRecipe()) {
                        tv.setText(getString(R.string.PAG_recipe_mark, e.getLabel()));
                        tv.setTextColor(ContextCompat.getColor(requireContext(), R.color.fg_title));
                    } else {
                        tv.setText(e.getLabel());
                    }
                    tv.setOnClickListener(x -> onEntryClick(e));
                    tv.setOnLongClickListener(x -> { onEntryLongClick(e); return true; });
                    box.addView(tv);
                }
            }
            if (count < AgendaEntry.MAX_PER_MEAL && !mReadOnly) {
                TextView plus = newLine();
                plus.setText(R.string.PAG_add);
                plus.setTextColor(ContextCompat.getColor(requireContext(), R.color.fg_enter_label));
                plus.setOnClickListener(x -> onAddClick(day, meal));
                box.addView(plus);
            }
        }

        private TextView newLine() {
            TextView tv = new TextView(requireContext());
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            tv.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
            int pad = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6,
                    getResources().getDisplayMetrics());
            tv.setPadding(0, pad, 0, pad);
            return tv;
        }
    }

    private class DayAdapter extends RecyclerView.Adapter<DayHolder> {
        @NonNull
        @Override
        public DayHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new DayHolder(LayoutInflater.from(getActivity()), parent);
        }

        @Override
        public void onBindViewHolder(@NonNull DayHolder holder, int position) {
            holder.bind(mFirstDay.plusDays(position));
        }

        @Override
        public int getItemCount() {
            return mDayCount;
        }
    }
}
