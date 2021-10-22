package com.fdx.cookbook;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;

import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class RecipeListFragment extends Fragment {
    private RecyclerView mRecipeRecyclerView;
    private RecipeAdapter mAdapter;
    private SessionInfo mSession;
    private Recipe mRecipeInit;
    private MenuItem mMessageItem;
    private static final String SAVED_SORT_STATUS="sort";
    private AsyncCallClass mInstanceAsync;
    private ListMask mListMask;
    private static final String TAG = "CB_ListFra";
    private static final String DIALOG_RATE = "DialogRate";
    private static final int REQUEST_RATE = 0;
    private static final String UUIDNULL="00000000-0000-0000-0000-000000000000";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mSession= SessionInfo.get(getActivity());
        mRecipeInit=new Recipe();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        User u=mSession.getUser();
        //String nameInSubtitle=getString(R.string.P2U_txt,u.getName(),u.getFamily());
        String nameInSubtitle=u.getName();
        activity.getSupportActionBar().setSubtitle(nameInSubtitle);
        mInstanceAsync = new AsyncCallClass(getContext());
        startSync();
        mListMask=new ListMask(mSession.getUser());
        String mask=mSession.getListMask();
        if ((!mask.equals(""))&&(mask!=null)) mListMask.updateFromString(mask);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) return;
        else {
            startSync();
            updateUI();}
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_list, container, false);
        mRecipeRecyclerView = (RecyclerView) view.findViewById(R.id.recipe_recycler_view);
        mRecipeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        if (savedInstanceState!=null){
            mListMask.updateFromString(savedInstanceState.getString(SAVED_SORT_STATUS));
        }
        updateUI();
        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        String mask=mSession.getListMask();
        if ((!mask.equals(""))&&(mask!=null)) mListMask.updateFromString(mask);
        updateUI();
    }

    @Override
    public void onPause(){
        super.onPause();
        mSession.setListMask(mListMask.convertToString());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SAVED_SORT_STATUS, mListMask.convertToString());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_recipe_list, menu);
        mMessageItem = menu.findItem(R.id.new_mail);
        CookBook cookbook=CookBook.get(getActivity());
        Boolean b=((cookbook.isThereMail())||(mSession.IsRecipeRequest()));
        mMessageItem.setVisible(b);
        mMessageItem.setShowAsAction(b ? MenuItem.SHOW_AS_ACTION_ALWAYS:MenuItem.SHOW_AS_ACTION_NEVER);
        //MenuCompat.setGroupDividerEnabled(menu, true);
        MenuItem searchItem = menu.findItem(R.id.menu_item_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Toast.makeText(getContext(), getString(mListMask.toggleSearch(s)),
                        Toast.LENGTH_SHORT ).show();
                updateUI();
                return true;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                if (s.equals("")){
                    Toast.makeText(getContext(), getString(mListMask.toggleSearch(s)),
                        Toast.LENGTH_SHORT ).show();
                    updateUI();
                }
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        Intent intent;
        switch (item.getItemId()){
            case R.id.new_recipe:
                intent= RecipeActivity.newIntent(getActivity(), UUID.fromString( UUIDNULL));
                startActivity(intent);
                return true;
            case R.id.list_logout:
                mSession.setReqNewSession(true);
                intent=new Intent(getActivity().getApplicationContext(), SplashActivity.class);
                startActivity(intent);
                return true;
            case R.id.list_sync:
                startSync();
                return true;
            case R.id.new_mail:
                CookBook cookbook=CookBook.get(getActivity());
                Boolean thereismail=(cookbook.isThereMail())||(mSession.IsRecipeRequest());
                if (!thereismail) {
                    mMessageItem.setVisible(false);
                    mMessageItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
                } else {
                    intent= RecipeMailDisplayActivity.newIntent(getActivity());
                    startActivity(intent);
                }
                return true;
            case R.id.list_filter:
                mListMask.reset();
                updateUI();
                return true;
            case R.id.list_com:
                intent= RecipeCommunityActivity.newIntent(getActivity());
                startActivity(intent);
                return true;
            case R.id.feedback:
                intent = new Intent(Intent.ACTION_SENDTO);
                intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback on application");
                intent.putExtra(Intent.EXTRA_TEXT, "Your comment");
                intent.setData(Uri.parse("mailto:cookbookfamily.founder@gmail.com"));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void startSync(){
        AsyncTask.Status as=mInstanceAsync.getStatus();
        if (as== AsyncTask.Status.RUNNING){
        }
        if (as== AsyncTask.Status.PENDING){
            mInstanceAsync.execute();
        }
        if (as== AsyncTask.Status.FINISHED){
            mInstanceAsync=new AsyncCallClass(getContext());
            mInstanceAsync.execute();
        }
    }

    private void updateUI() {
        CookBook cookbook=CookBook.get(getActivity());
        List<Recipe> recipes_in=cookbook.getRecipes();
        List<Recipe> recipes=new ArrayList<>();
        recipes.addAll(recipes_in);
        for(Recipe r:recipes_in){
            if (!mListMask.isRecipeSelected(r)) recipes.remove(r);
        }
        if (recipes.isEmpty()){
            Integer message;
            if (cookbook.getRecipesVisibles().isEmpty())
                message = (cookbook.isThereMail() ? R.string.TEW : R.string.TEZ);
            else message=R.string.TEY;
            Toast.makeText(getContext(), getString(message), Toast.LENGTH_LONG).show();
        } else {
            if (mListMask.isTitleSorted()) {
                Collections.sort(recipes,
                        (r1, r2) -> {
                            return (r1.getTitle().compareTo(r2.getTitle()));
                        });
            } else {
                Collections.sort(recipes,
                        (r1, r2) -> {
                            return (r2.getDate().compareTo(r1.getDate()));
                        });
            }
            if (mListMask.isNoteSorted()) {
                Collections.sort(recipes,
                        (r1, r2) -> {
                            return (r2.getNbNotes()-r1.getNbNotes()+(int)(100* (r2.getNoteAvg() - r1.getNoteAvg())));
                        });
            }
        }
        if (mAdapter==null){
            mAdapter=new RecipeAdapter(recipes);
            mRecipeRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setRecipes(recipes);
            mAdapter.notifyDataSetChanged();
        }
    }

    private class RecipeHolder extends RecyclerView.ViewHolder
                                implements View.OnClickListener {
        private TextView mTitleTextView;
        private TextView mSourceTextView;
        private TextView mNoteTextView;
        private TextView mRatingTextView;
        private TextView mDifficulty;
        private TextView mType;
        private ImageView mEditIcon;
        private ImageView mPhotoView;
        private ImageView mSunIcon;
        private ImageView mIceIcon;
        private File mPhotoFile;
        private Recipe mRecipe;
        private RatingBar mRating;
        public RecipeHolder(LayoutInflater inflater,ViewGroup parent){
            super(inflater.inflate(R.layout.list_item_recipe, parent, false));
            itemView.setOnClickListener(this);
            mTitleTextView= (TextView) itemView.findViewById(R.id.recipe_title);
            mSourceTextView= (TextView) itemView.findViewById(R.id.recipe_source);
            mNoteTextView= (TextView) itemView.findViewById(R.id.recipe_note);
            mNoteTextView= (TextView) itemView.findViewById(R.id.recipe_note);
            mEditIcon=(ImageView) itemView.findViewById(R.id.recipe_edit);
            mSunIcon=(ImageView) itemView.findViewById(R.id.recipe_img_sun);
            mIceIcon=(ImageView) itemView.findViewById(R.id.recipe_img_ice);
            mDifficulty=(TextView) itemView.findViewById(R.id.recipe_difficulty);
            mType=(TextView) itemView.findViewById(R.id.recipe_type);
            mPhotoView=(ImageView) itemView.findViewById(R.id.recipe_photo);
            mPhotoFile=CookBook.get(getActivity()).getPhotoFile(mRecipe);
            mRating=(RatingBar) itemView.findViewById(R.id.recipe_list_ratingBar);
            mRatingTextView= (TextView) itemView.findViewById(R.id.textEmpty);
            mRatingTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentManager manager = getFragmentManager();
                    RatePickerFragment dialog = RatePickerFragment.newInstance(mRecipe.getId());
                    dialog.setTargetFragment(RecipeListFragment.this, REQUEST_RATE);
                    dialog.show(manager, DIALOG_RATE);
                }
            });
            mTitleTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(),
                            getString(mListMask.toggleTitle()),
                            Toast.LENGTH_SHORT ).show();
                    updateUI();
                }
            });
            mNoteTextView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    Toast.makeText(getContext(), getString(mListMask.toggleSortNote()),
                            Toast.LENGTH_SHORT ).show();
                    updateUI();
                }
            });
            mSourceTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(), getString(mListMask.toggleUser(mRecipe.getOwner()),
                            mRecipe.getOwner().getName()),Toast.LENGTH_SHORT ).show();
                    updateUI();
                }
            });
            mSunIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(), getString(mListMask.toggleSun()),Toast.LENGTH_SHORT ).show();
                    updateUI();
                }
            });
            mIceIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(), getString(mListMask.toggleWinter()),
                            Toast.LENGTH_SHORT ).show();
                    updateUI();
                }
            });
            mDifficulty.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(),
                            getString(mListMask.toggleDifficulty(mRecipe.getDifficulty())),
                            Toast.LENGTH_SHORT ).show();
                    updateUI();
                }
            });
            mType.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(),
                            getString(mListMask.toggleType(mRecipe.getType())),
                            Toast.LENGTH_SHORT ).show();
                    updateUI();
                }
            });
            mEditIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent= RecipeActivity.newIntent(getActivity(),mRecipe.getId());
                    startActivity(intent);
                }
            });
            mPhotoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=RecipeDisplayActivity.newIntent(getActivity(),mRecipe.getId());
                    startActivity(intent);
                }
            });
        }
        public void bind(Recipe recipe){
            mRecipe=recipe;
            mTitleTextView.setText(mRecipe.getTitle());
            mSourceTextView.setText(getString(R.string.P1_de, mRecipe.getOwner().getName()));
            //mSourceTextView.setText(mRecipe.getFlag()); // for debug
            mRating.setRating((float) mRecipe.getNoteAvg());
            DecimalFormat df = new DecimalFormat("#.#");
            mNoteTextView.setText(df.format(mRecipe.getNoteAvg())+" ("+mRecipe.getNbNotes()+")");
            mPhotoFile=CookBook.get(getActivity()).getPhotoFile(mRecipe);
            int idff= RecipeDifficulty.getIndex(mRecipe.getDifficulty());
            String[] stringArray = getResources().getStringArray(R.array.recipe_difficulty_array);
            mDifficulty.setText(stringArray[idff]);
            int ityp= RecipeType.getIndex(mRecipe.getType());
            String[] stringArrayTyp = getResources().getStringArray(R.array.recipe_type_array);
            mType.setText(stringArrayTyp[ityp]);
            if (mPhotoFile==null || !mPhotoFile.exists()){
                mPhotoView.setImageDrawable(getResources().getDrawable(R.drawable.ic_recipe_see));
            } else {
                Bitmap bitmap=PictureUtils.getScaledBitmap(mPhotoFile.getPath(), getActivity());
                mPhotoView.setImageBitmap(bitmap);
            }
            mIceIcon.setImageResource((mRecipe.IsWinter()) ? R.drawable.ic_recipe_ice : R.drawable.ic_recipe_ice_disabled);
            mSunIcon.setImageResource((mRecipe.IsSummer()) ? R.drawable.ic_recipe_sun : R.drawable.ic_recipe_sun_disabled);
            mEditIcon.setVisibility((mRecipe.getOwner().getId().equals(mSession.getUser().getId())) ? View.VISIBLE : View.GONE);
        }

        @Override
        public void onClick(View v){

        }
    }

    private class RecipeAdapter extends RecyclerView.Adapter<RecipeHolder> {
        private List<Recipe> mRecipes;
        public RecipeAdapter(List<Recipe> recipes){
            mRecipes=recipes;
        }

        @NonNull
        @Override
        public RecipeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater=LayoutInflater.from(getActivity());
            return new RecipeHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull RecipeHolder recipeHolder, int i) {
            Recipe recipe=mRecipes.get(i);
            recipeHolder.bind(recipe);
        }

        @Override
        public int getItemCount() {
            return mRecipes.size();
        }

        public void setRecipes(List<Recipe> recipes){
            mRecipes=recipes;
        }
    }
}
