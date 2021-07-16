package com.example.cookbook;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

public class RecipeMailDisplayActivity extends SingleFragmentActivity {
    //private static final String EXTRA_RECIPE_ID="com.example.cookbook.recipe_id";
    public static Intent newIntent(Context packageContexte) {
        Intent intent=new Intent(packageContexte, RecipeMailDisplayActivity.class);
        //intent.putExtra(EXTRA_RECIPE_ID, recipeId);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        //UUID recipeId=(UUID) getIntent().getSerializableExtra(EXTRA_RECIPE_ID);
        return RecipeMailDisplayFragment.newInstance();
    }
}
