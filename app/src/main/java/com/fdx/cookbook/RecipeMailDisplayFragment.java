package com.fdx.cookbook;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fdx.cookbook.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RecipeMailDisplayFragment extends Fragment {
    private RecyclerView mRecipeRecyclerView;
    private RecipeAdapter mAdapter;
    private SessionInfo mSession;
    private static final String TAG = "CB_R.MailDisplayFrag";

    public static RecipeMailDisplayFragment newInstance() {
        //Bundle args=new Bundle();
        //args.putSerializable(ARG_RECIPE_ID, recipeId);
        RecipeMailDisplayFragment fragment=new RecipeMailDisplayFragment();
        //fragment.setArguments(args);
        return fragment;
    }

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
        List<Recipe> recipes_in=cookbook.getRecipes();
        List<Recipe> recipes=new ArrayList<>();
        for(Recipe r:recipes_in){
            if (r.IsMessage()) recipes.add(r);
        }
        if (mAdapter==null){
            mAdapter=new RecipeMailDisplayFragment.RecipeAdapter(recipes);
            mRecipeRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setRecipes(recipes);
            mAdapter.notifyDataSetChanged();
        }
    }
// -----------------------------------RECIPE HOLDER ------------------------
    private class RecipeHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        private TextView mTitleTextView;
        private TextView mMessage;
        private TextView mFrom;
        private ImageView mPhotoView;
        private ImageView mDelete;
        private ImageView mAdd;
        private File mPhotoFile;
        private Recipe mRecipe;

        public RecipeHolder(LayoutInflater inflater,ViewGroup parent){
            super(inflater.inflate(R.layout.list_item_mail_display, parent, false));
            itemView.setOnClickListener(this);
            mTitleTextView= (TextView) itemView.findViewById(R.id.recipe_MD_title);
            mMessage=(TextView) itemView.findViewById(R.id.mail_display_message);
            mFrom=(TextView) itemView.findViewById(R.id.mail_display_author);
            mDelete=(ImageView) itemView.findViewById(R.id.mail_display_delete);
            mAdd=(ImageView) itemView.findViewById(R.id.mail_display_add);
            mPhotoView=(ImageView) itemView.findViewById(R.id.recipe_MD_photo);
            mPhotoFile=CookBook.get(getActivity()).getPhotoFile(mRecipe);

            mDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mRecipe.setStatus(StatusRecipe.Deleted);
                    CookBook cookbook=CookBook.get(getActivity());
                    cookbook.updateRecipe(mRecipe);
                    updateUI();
                }
            });
            mAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mRecipe.setStatus(StatusRecipe.Visible);
                    CookBook cookbook=CookBook.get(getActivity());
                    cookbook.updateRecipe(mRecipe);
                    updateUI();
                }
            });

        }
        public void bind(Recipe recipe){
            mRecipe=recipe;
            mTitleTextView.setText(mRecipe.getTitle());
            mMessage.setText(mRecipe.getMessage());
            mFrom.setText(mRecipe.getUserFrom().getNameComplete());
            mPhotoFile=CookBook.get(getActivity()).getPhotoFile(mRecipe);
            if (mPhotoFile==null || !mPhotoFile.exists()){
                mPhotoView.setImageDrawable(getResources().getDrawable(R.drawable.ic_recipe_see));
            } else {
                Bitmap bitmap=PictureUtils.getScaledBitmap(mPhotoFile.getPath(), getActivity());
                mPhotoView.setImageBitmap(bitmap);
            }
        }
    @Override
    public void onClick(View v){    }
    }
    // -----------------------------------ADAPTER------------------------
    private class RecipeAdapter extends RecyclerView.Adapter<RecipeMailDisplayFragment.RecipeHolder> {
        private List<Recipe> mRecipes;
        public RecipeAdapter(List<Recipe> recipes){
            mRecipes=recipes;
        }

        @NonNull
        @Override
        public RecipeMailDisplayFragment.RecipeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater=LayoutInflater.from(getActivity());
            return new RecipeMailDisplayFragment.RecipeHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull RecipeMailDisplayFragment.RecipeHolder recipeHolder, int i) {
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
