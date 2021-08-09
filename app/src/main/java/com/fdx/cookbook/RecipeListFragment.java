package com.fdx.cookbook;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

//-------------------------------------------------------------------------------------------------
// print 16/07/2021
// Saved GitHUb V2.0
//----------------------------------------------------------------------------------------------
public class RecipeListFragment extends Fragment {
    private RecyclerView mRecipeRecyclerView;
    private RecipeAdapter mAdapter;
    private SessionInfo mSession;
    private Recipe mRecipeInit;
    private MenuItem mMessageItem;
    private Menu mMenu;
    private static final String SAVED_SORT_STATUS="sort";
    private int mSortOption;
    private int finalRate;
    private static final int maskSortTitle=1 <<3;
    private static final int maskSortSource=1;
    private static final int maskSortNote=1 <<2;
    private static final int maskSortSeason=1 <<4;
    private static final int maskSortDifficulty=1 <<5;
    private static final String TAG = "CB_RecipeListFragment";
    private static final String DIALOG_RATE = "DialogRate";
    private static final int REQUEST_RATE = 0;
    private static final String UUIDNULL="00000000-0000-0000-0000-000000000000";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mSession= SessionInfo.get(getActivity());
        mSortOption=0;
        mRecipeInit=new Recipe();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        User u=mSession.getUser();
        activity.getSupportActionBar().setSubtitle(getString(R.string.recipe_display_author,u.getName(),u.getFamily()));
        AsyncCallClass instanceAsync = new AsyncCallClass(getContext());
        instanceAsync.execute();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        //todo P3 enlever request rate reliquat
        if (requestCode == REQUEST_RATE) {
            Integer rate = (Integer) data
                    .getSerializableExtra(RatePickerFragment.EXTRA_RATE);
            updateUI();
        }
        //todo test retour recipeeditfragment => remove empty recipe
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_list, container, false);
        mRecipeRecyclerView = (RecyclerView) view.findViewById(R.id.recipe_recycler_view);
        mRecipeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        if (savedInstanceState!=null){
            mSortOption=savedInstanceState.getInt(SAVED_SORT_STATUS);
        }
        updateUI();
        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        updateUI();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SAVED_SORT_STATUS, mSortOption);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_recipe_list, menu);
        mMessageItem = menu.findItem(R.id.new_mail);
        CookBook cookbook=CookBook.get(getActivity());
        mMessageItem.setVisible(cookbook.isThereMail());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.new_recipe:
                //Recipe recipe=new Recipe();
                //SessionInfo session=SessionInfo.get(getActivity());
                //recipe.setOwner(session.getUser());
                //recipe.updateTS(AsynCallFlag.NEWRECIPE, true);
                //recipe.setTitle(getString(R.string.recipe_title_label));
                //CookBook.get(getActivity()).addRecipe(recipe);
                //Intent intent= RecipeActivity.newIntent(getActivity(),recipe.getId());
                Intent intent= RecipeActivity.newIntent(getActivity(), UUID.fromString( UUIDNULL));
                startActivity(intent);
                return true;
            case R.id.list_logout:
                mSession.setReqNewSession(true);
                Intent intent2=new Intent(getActivity().getApplicationContext(), SplashActivity.class);
                startActivity(intent2);
                return true;
            case R.id.list_sync:
                AsyncCallClass instanceAsync = new AsyncCallClass(getContext());
                instanceAsync.execute();
                return true;
            case R.id.new_mail:
                CookBook cookbook=CookBook.get(getActivity());
                if (cookbook.isThereMail()) {
                Intent intent3= RecipeMailDisplayActivity.newIntent(getActivity());
                startActivity(intent3); } else {
                mMessageItem.setVisible(cookbook.isThereMail());}
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateUI() {
        CookBook cookbook=CookBook.get(getActivity());
        List<Recipe> recipes_in=cookbook.getRecipes();
        List<Recipe> recipes=new ArrayList<>();
        recipes.addAll(recipes_in);
        for(Recipe r:recipes_in){
            if (!r.IsVisible()){
                recipes.remove(r);
            }
        }
        if ((mSortOption & maskSortTitle) == maskSortTitle) {
            Collections.sort(recipes,
                    (r1, r2)->{return(r1.getTitle().compareTo(r2.getTitle()));});}
        if ((mSortOption & maskSortNote) == maskSortNote) {
            Collections.sort(recipes,
                    (r1, r2)->{return((int)(r2.getNoteAvg()-r1.getNoteAvg()));});}
        if ((mSortOption & maskSortSource) == maskSortSource) {
            Collections.sort(recipes,
                    (r1, r2)->{return(r1.getOwner().getName().compareTo(r2.getOwner().getName()));});}
        if ((mSortOption & maskSortSeason) == maskSortSeason) {
            Collections.sort(recipes,
                    (r1, r2)->{return(RecipeSeason.getIndex(r1.getSeason())
                            -RecipeSeason.getIndex(r2.getSeason()));});}
        if ((mSortOption & maskSortDifficulty) == maskSortDifficulty) {
            Collections.sort(recipes,
                    (r1, r2)->{return(RecipeDifficulty.getIndex(r1.getDifficulty())
                            -RecipeDifficulty.getIndex(r2.getDifficulty()));});}
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
        private TextView mDifficulty;
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
            mEditIcon=(ImageView) itemView.findViewById(R.id.recipe_edit);
            mSunIcon=(ImageView) itemView.findViewById(R.id.recipe_img_sun);
            mIceIcon=(ImageView) itemView.findViewById(R.id.recipe_img_ice);
            mDifficulty=(TextView) itemView.findViewById(R.id.recipe_difficulty);
            mPhotoView=(ImageView) itemView.findViewById(R.id.recipe_photo);
            mPhotoFile=CookBook.get(getActivity()).getPhotoFile(mRecipe);
            mRating=(RatingBar) itemView.findViewById(R.id.recipe_list_ratingBar);
            mRating.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(getActivity(), "Call star", Toast.LENGTH_SHORT).show();
                }
            });
            mTitleTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSortOption=mSortOption ^ maskSortTitle;
                    updateUI();
                }
            });
            mNoteTextView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    mSortOption=mSortOption ^ maskSortNote;
                    updateUI();
                }
            });
            mSourceTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSortOption=mSortOption ^ maskSortSource;
                    updateUI();
                }
            });
            /*
            mEditIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });*/
            mSunIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSortOption=mSortOption ^ maskSortSeason;
                    updateUI();
                }
            });
            mIceIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSortOption=mSortOption ^ maskSortSeason;
                    updateUI();
                }
            });
            mDifficulty.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSortOption=mSortOption ^ maskSortDifficulty;
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
            mSourceTextView.setText("("+mRecipe.getOwner().getName()+")");
            // mSourceTextView.setText(mRecipe.getFlag()); for debug
            mRating.setRating((float) mRecipe.getNoteAvg());
            DecimalFormat df = new DecimalFormat("#.#");
            mNoteTextView.setText(df.format(mRecipe.getNoteAvg())+"/5");
            mPhotoFile=CookBook.get(getActivity()).getPhotoFile(mRecipe);
            int idff= RecipeDifficulty.getIndex(mRecipe.getDifficulty());
            String[] stringArray = getResources().getStringArray(R.array.recipe_difficulty_array);
            mDifficulty.setText(stringArray[idff]);
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
            //todo P2 devrait se declencher sur le rating
            FragmentManager manager = getFragmentManager();
            RatePickerFragment dialog = RatePickerFragment.newInstance(mRecipe.getId());
            dialog.setTargetFragment(RecipeListFragment.this, REQUEST_RATE);
            dialog.show(manager, DIALOG_RATE);
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
