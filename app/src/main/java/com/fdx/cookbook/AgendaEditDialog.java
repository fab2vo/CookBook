package com.fdx.cookbook;

import android.app.DatePickerDialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * Dialog to add a dish to a meal of the agenda, or to change an existing one
 * (recipe of the cookbook or free text; in change mode the day and the meal can be changed too).
 * The entry is saved on the server before the listener is called.
 */
public class AgendaEditDialog {

    public interface Listener {
        /** Called on the main thread once the server has stored the entry */
        void onSaved(AgendaEntry saved);
    }

    static final DateTimeFormatter DAY_LONG = DateTimeFormatter.ofPattern("EEEE d MMM", Locale.FRANCE);

    /** Item of the recipe list: shows the title */
    private static class RecipeChoice {
        final UUID id;
        final String title;
        final String key;   // normalized title, for the search
        RecipeChoice(Recipe r) { id = r.getId(); title = r.getTitle(); key = normalize(title); }
        @Override public String toString() { return title; }
    }

    /** Lower case, without accents, œ/æ spelled oe/ae: "Bœuf Crème" -> "boeuf creme" */
    static String normalize(String s) {
        String t = s.toLowerCase(Locale.FRANCE).replace("œ", "oe").replace("æ", "ae");
        return Normalizer.normalize(t, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
    }

    /** Suggestions: every recipe whose title CONTAINS the typed text ("tatin" -> "Pomme tatin") */
    private static class RecipeChoiceAdapter extends BaseAdapter implements Filterable {
        private final Context mCtx;
        private final List<RecipeChoice> mAll;
        private List<RecipeChoice> mShown;

        RecipeChoiceAdapter(Context c, List<RecipeChoice> all) {
            mCtx = c;
            mAll = all;
            mShown = all;
        }

        @Override public int getCount() { return mShown.size(); }
        @Override public RecipeChoice getItem(int position) { return mShown.get(position); }
        @Override public long getItemId(int position) { return position; }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView tv = (TextView) (convertView != null ? convertView
                    : LayoutInflater.from(mCtx).inflate(android.R.layout.simple_dropdown_item_1line, parent, false));
            tv.setText(mShown.get(position).title);
            return tv;
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence typed) {
                    List<RecipeChoice> found = new ArrayList<>();
                    String q = typed == null ? "" : normalize(typed.toString().trim());
                    for (RecipeChoice rc : mAll) if (rc.key.contains(q)) found.add(rc);
                    FilterResults res = new FilterResults();
                    res.values = found;
                    res.count = found.size();
                    return res;
                }

                @Override
                @SuppressWarnings("unchecked")
                protected void publishResults(CharSequence typed, FilterResults res) {
                    mShown = (List<RecipeChoice>) res.values;
                    if (res.count > 0) notifyDataSetChanged(); else notifyDataSetInvalidated();
                }

                @Override
                public CharSequence convertResultToString(Object item) {
                    return ((RecipeChoice) item).title;
                }
            };
        }
    }

    private final Context mContext;
    private final AgendaEntry mOriginal;
    private final boolean mIsNew;
    private final AgendaApi mApi;
    private final Listener mListener;
    private final Runnable mOnDelete;

    private LocalDate mDate;
    private UUID mRecipeId;        // chosen recipe, null while none is chosen
    private String mRecipeTitle;

    private AgendaEditDialog(Context c, AgendaEntry original, boolean isNew, AgendaApi api, Listener l,
                             Runnable onDelete) {
        mContext = c;
        mOriginal = original;
        mIsNew = isNew;
        mApi = api;
        mListener = l;
        mOnDelete = onDelete;
        mDate = original.getDate();
        if (original.isRecipe()) {
            mRecipeId = original.getRecipeId();
            mRecipeTitle = original.getLabel();
        }
    }

    /**
     * @param entry for a new dish: an empty entry giving agenda, day and meal; otherwise the entry to change
     *              (it is not modified: the listener receives a new object with the same id)
     */
    public static void show(Context c, AgendaEntry entry, boolean isNew, AgendaApi api, Listener l) {
        new AgendaEditDialog(c, entry, isNew, api, l, null).build();
    }

    /** Change mode with an "Effacer" button: onDelete is run after the dialog is closed */
    public static void showEdit(Context c, AgendaEntry entry, AgendaApi api, Listener l, Runnable onDelete) {
        new AgendaEditDialog(c, entry, false, api, l, onDelete).build();
    }

    static String mealName(Context c, AgendaEntry.Meal meal) {
        return c.getString(meal == AgendaEntry.Meal.LUNCH ? R.string.PAG_lunch_long : R.string.PAG_dinner_long);
    }

    private static String capitalize(String s) {
        return s.isEmpty() ? s : s.substring(0, 1).toUpperCase(Locale.FRANCE) + s.substring(1);
    }

    private void build() {
        View v = LayoutInflater.from(mContext).inflate(R.layout.dialog_agenda_entry, null);
        View moveRow = v.findViewById(R.id.agenda_move_row);
        Button dateButton = v.findViewById(R.id.agenda_date_button);
        RadioGroup mealGroup = v.findViewById(R.id.agenda_meal_group);
        RadioGroup kindGroup = v.findViewById(R.id.agenda_kind_group);
        AutoCompleteTextView recipeInput = v.findViewById(R.id.agenda_recipe_input);
        EditText textInput = v.findViewById(R.id.agenda_text_input);

        // ---- day and meal (change mode only)
        moveRow.setVisibility(mIsNew ? View.GONE : View.VISIBLE);
        dateButton.setText(mDate.format(DAY_LONG));
        dateButton.setOnClickListener(b -> new DatePickerDialog(mContext,
                (picker, y, m, d) -> {
                    mDate = LocalDate.of(y, m + 1, d);
                    dateButton.setText(mDate.format(DAY_LONG));
                },
                mDate.getYear(), mDate.getMonthValue() - 1, mDate.getDayOfMonth()).show());
        mealGroup.check(mOriginal.getMeal() == AgendaEntry.Meal.LUNCH
                ? R.id.agenda_meal_lunch : R.id.agenda_meal_dinner);

        // ---- recipe of the cookbook: autocompletion on the titles
        List<RecipeChoice> choices = new ArrayList<>();
        for (Recipe r : CookBook.get(mContext).getRecipesVisibles()) choices.add(new RecipeChoice(r));
        choices.sort((a, b) -> a.key.compareTo(b.key));
        RecipeChoiceAdapter adapter = new RecipeChoiceAdapter(mContext, choices);
        recipeInput.setAdapter(adapter);
        recipeInput.setOnItemClickListener((parent, view, position, id) -> {
            RecipeChoice rc = adapter.getItem(position);
            if (rc == null) return;
            mRecipeId = rc.id;
            mRecipeTitle = rc.title;
            recipeInput.setError(null);
        });
        recipeInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) { }
            @Override public void onTextChanged(CharSequence s, int a, int b, int c) { }
            @Override public void afterTextChanged(Editable s) {
                // Typing after a choice cancels it: a title must be picked from the list
                if (mRecipeTitle != null && !s.toString().equals(mRecipeTitle)) {
                    mRecipeId = null;
                    mRecipeTitle = null;
                }
            }
        });

        // ---- recipe or free text
        kindGroup.setOnCheckedChangeListener((g, checkedId) -> {
            boolean recipe = checkedId == R.id.agenda_kind_recipe;
            recipeInput.setVisibility(recipe ? View.VISIBLE : View.GONE);
            textInput.setVisibility(recipe ? View.GONE : View.VISIBLE);
            (recipe ? recipeInput : textInput).requestFocus();
        });
        if (!mIsNew && !mOriginal.isRecipe()) {
            textInput.setText(mOriginal.getLabel());
            kindGroup.check(R.id.agenda_kind_text);
        } else {
            if (mOriginal.isRecipe()) recipeInput.setText(mOriginal.getLabel(), false);
            kindGroup.check(R.id.agenda_kind_recipe);
        }

        String title = mContext.getString(R.string.PAG_dialog_title,
                capitalize(mealName(mContext, mOriginal.getMeal())), mOriginal.getDate().format(DAY_LONG));
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
                .setTitle(title)
                .setView(v)
                .setPositiveButton(android.R.string.ok, null)   // set below, to keep the dialog open on error
                .setNegativeButton(android.R.string.cancel, null);
        if (!mIsNew && mOnDelete != null) {
            builder.setNeutralButton(R.string.PAG_delete, (d, w) -> mOnDelete.run());
        }
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(d -> {
            Button ok = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            ok.setOnClickListener(b -> {
                AgendaEntry.Meal meal = mIsNew ? mOriginal.getMeal()
                        : (mealGroup.getCheckedRadioButtonId() == R.id.agenda_meal_lunch
                            ? AgendaEntry.Meal.LUNCH : AgendaEntry.Meal.DINNER);
                AgendaEntry result = new AgendaEntry(mOriginal.getId(), mOriginal.getScope(), mDate, meal);
                if (kindGroup.getCheckedRadioButtonId() == R.id.agenda_kind_recipe) {
                    if (mRecipeId == null) {
                        recipeInput.setError(mContext.getString(R.string.PAG_pick_recipe));
                        return;
                    }
                    result.setRecipe(mRecipeId, mRecipeTitle);
                } else {
                    // < and > are refused by the server
                    String text = textInput.getText().toString().replace("<", "").replace(">", "").trim();
                    if (text.isEmpty()) {
                        textInput.setError(mContext.getString(R.string.PAG_empty_text));
                        return;
                    }
                    result.setFreeText(text);
                }
                ok.setEnabled(false);
                AgendaApi.Callback<Void> cb = (status, unused) -> {
                    ok.setEnabled(true);
                    if (status == AgendaApi.Status.OK) {
                        dialog.dismiss();
                        mListener.onSaved(result);
                    } else {
                        Toast.makeText(mContext, statusMessage(status), Toast.LENGTH_LONG).show();
                    }
                };
                if (mIsNew) mApi.add(result, cb); else mApi.update(result, cb);
            });
        });
        dialog.show();
    }

    static int statusMessage(AgendaApi.Status status) {
        switch (status) {
            case SLOT_FULL: return R.string.PAG_slot_full;
            case OFFLINE:   return R.string.PAG_offline_short;
            default:        return R.string.PAG_error;
        }
    }
}
