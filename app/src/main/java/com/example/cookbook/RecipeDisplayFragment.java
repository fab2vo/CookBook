package com.example.cookbook;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.UUID;

public class RecipeDisplayFragment extends Fragment {
    private static final String ARG_RECIPE_ID="recipe_id";
    private static final String TAG = "DebugRecipeDisplayFrag";
    private TextView mDTextView;
    private Recipe mRecipe;

    public static RecipeDisplayFragment newInstance(UUID recipeId) {
        Bundle args=new Bundle();
        args.putSerializable(ARG_RECIPE_ID, recipeId);
        RecipeDisplayFragment fragment=new RecipeDisplayFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRecipe=new Recipe();
        UUID recipeId=(UUID) getArguments().getSerializable(ARG_RECIPE_ID);
        mRecipe=CookBook.get(getActivity()).getRecipe(recipeId);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_recipe_display, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.recipe_menu_report:
                //Intent i=new Intent(Intent.ACTION_SEND);
                //i.setType("text/plain");
                //i.putExtra(Intent.EXTRA_TEXT, getRecipeReport());
                //i.putExtra(Intent.EXTRA_SUBJECT,getString(R.string.recipe_report_subject));
                //\startActivity(i);
                return true;
            case R.id.recipe_menu_delete:
                if(!CookBook.get(getActivity()).deleteImage(mRecipe)) {
                    Log.d(TAG, "DeleteRecipeFromMenu : no delete image");
                }
                CookBook.get(getActivity()).removeRecipe(mRecipe);
                getActivity().onBackPressed();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        final View v=inflater.inflate(R.layout.fragment_display_recipe, container, false);
        mDTextView=(TextView) v.findViewById(R.id.recipe_display_title);
        mDTextView.setText(mRecipe.getTitle());
        return v;
    }
}
