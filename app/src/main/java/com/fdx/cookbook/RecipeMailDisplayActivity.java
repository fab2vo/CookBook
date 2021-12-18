package com.fdx.cookbook;

import android.content.Context;
import android.content.Intent;
import androidx.fragment.app.Fragment;

public class RecipeMailDisplayActivity extends SingleFragmentActivity {
    //private static final String EXTRA_RECIPE_ID="com.fdx.cookbook.recipe_id";
    public static Intent newIntent(Context packageContexte) {
        Intent intent=new Intent(packageContexte, RecipeMailDisplayActivity.class);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        return RecipeMailDisplayFragment.newInstance();
    }
}
