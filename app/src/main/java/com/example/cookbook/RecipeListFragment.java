package com.example.cookbook;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class RecipeListFragment extends Fragment {
    private RecyclerView mRecipeRecyclerView;
    private RecipeAdapter mAdapter;
    private boolean mSubtitleVisible;
    private SessionInfo mSession;
    private static final String SAVED_SUBTITLE_VISIBLE="subtitle";
    private static final String TAG = "DebugRecipeListFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mSession= SessionInfo.get(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_list, container, false);
        mRecipeRecyclerView = (RecyclerView) view.findViewById(R.id.recipe_recycler_view);
        mRecipeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        if (savedInstanceState!=null){
            mSubtitleVisible=savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
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
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE, mSubtitleVisible);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_recipe_list, menu);
        MenuItem subtitleItem=menu.findItem(R.id.show_subtitle);
        if (mSubtitleVisible) {
            subtitleItem.setTitle(R.string.hide_subtitle);
        } else {
            subtitleItem.setTitle(R.string.show_subtitle);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.new_recipe:
                Recipe recipe=new Recipe();
                CookBook.get(getActivity()).addRecipe(recipe);
                Intent intent= RecipeActivity.newIntent(getActivity(),recipe.getId());
                startActivity(intent);
                return true;
            case R.id.show_subtitle:
                mSubtitleVisible= !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void updateSubtitle(){
        CookBook cookBook= CookBook.get(getActivity());
        int recipeCount=cookBook.getRecipes().size();
        String subtitle=getString(R.string.subtitle_format, recipeCount);
        if (!mSubtitleVisible){subtitle=null;}
        AppCompatActivity activity=(AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    private void updateUI() {
        CookBook cookbook=CookBook.get(getActivity());
        List<Recipe> recipes=cookbook.getRecipes();
        if (mAdapter==null){
            Log.d(TAG, "updateUI : Taille Cookbook =" + recipes.size()+" de "+mSession.getName());
            mAdapter=new RecipeAdapter(recipes);
            mRecipeRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setRecipes(recipes);
            mAdapter.notifyDataSetChanged();}
        updateSubtitle();
    }

    private class RecipeHolder extends RecyclerView.ViewHolder
                                implements View.OnClickListener {
        private TextView mTitleTextView;
        private TextView mSourceTextView;
        private TextView mNoteTextView;
        private ImageView mDeleteIcon;
        private Recipe mRecipe;
        public RecipeHolder(LayoutInflater inflater,ViewGroup parent){
            super(inflater.inflate(R.layout.list_item_recipe, parent, false));
            itemView.setOnClickListener(this);
            mTitleTextView= (TextView) itemView.findViewById(R.id.recipe_title);
            mSourceTextView= (TextView) itemView.findViewById(R.id.recipe_source);
            mNoteTextView= (TextView) itemView.findViewById(R.id.recipe_note);
            mDeleteIcon=(ImageView) itemView.findViewById(R.id.recipe_delete);
            // To check can be called by clicking on note
            mNoteTextView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    Toast.makeText(getActivity(), "Note clicked",
                            Toast.LENGTH_SHORT ).show();
                }
            });
            //-
            mDeleteIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!CookBook.get(getActivity()).deleteImage(mRecipe)) {
  //                     Toast.makeText(getActivity(), "Failure in deleting file", Toast.LENGTH_SHORT).show();
                    }
                    CookBook.get(getActivity()).removeRecipe(mRecipe);
                    updateUI();
                }
            });
        }
        public void bind(Recipe recipe){
            mRecipe=recipe;
            mTitleTextView.setText(mRecipe.getTitle());
            mSourceTextView.setText(mRecipe.getSource());
            mNoteTextView.setText(mRecipe.getNoteAvg()+"/5");
        }
        @Override
        public void onClick(View v){
            Intent intent= RecipeActivity.newIntent(getActivity(),mRecipe.getId());
            startActivity(intent);
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
