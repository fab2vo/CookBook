package com.example.cookbook;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class RecipeListFragment extends Fragment {
    private RecyclerView mRecipeRecyclerView;
    private RecipeAdapter mAdapter;
    private static final String TAG = "DebugRecipe";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_list, container, false);
        mRecipeRecyclerView = (RecyclerView) view.findViewById(R.id.recipe_recycler_view);
        mRecipeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        updateUI();
        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        CookBook cookbook=CookBook.get(getActivity());
        List<Recipe> recipes=cookbook.getRecipes();
        if (mAdapter==null){
            mAdapter=new RecipeAdapter(recipes);
            mRecipeRecyclerView.setAdapter(mAdapter);
        } else { mAdapter.notifyDataSetChanged();}
  //      Log.d(TAG, "Taille Cookbook " + recipes.size());
  //      Log.d(TAG, "Recette 5 " + recipes.get(5).getTitle());
  //      Log.d(TAG, "Recette 5 " + recipes.get(5).getSource());
    }

    private class RecipeHolder extends RecyclerView.ViewHolder
                                implements View.OnClickListener {
        private TextView mTitleTextView;
        private TextView mSourceTextView;
        private TextView mNoteTextView;
        private Recipe mRecipe;
        public RecipeHolder(LayoutInflater inflater,ViewGroup parent){
            super(inflater.inflate(R.layout.list_item_recipe, parent, false));
            itemView.setOnClickListener(this);
            mTitleTextView= (TextView) itemView.findViewById(R.id.recipe_title);
            mSourceTextView= (TextView) itemView.findViewById(R.id.recipe_source);
            mNoteTextView= (TextView) itemView.findViewById(R.id.recipe_note);
            // To check can be called by clicking on note
            mNoteTextView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    Toast.makeText(getActivity(), "Note clicked",
                            Toast.LENGTH_SHORT ).show();
                }
            });
            //-
        }
        public void bind(Recipe recipe){
            mRecipe=recipe;
            mTitleTextView.setText(mRecipe.getTitle());
            mSourceTextView.setText(mRecipe.getSource());
            mNoteTextView.setText(mRecipe.getNote()+"/5");
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
    }
}
