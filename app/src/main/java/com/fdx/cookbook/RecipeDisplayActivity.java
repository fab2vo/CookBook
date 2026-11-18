package com.fdx.cookbook;

import android.content.Context;
import android.content.Intent;
import androidx.fragment.app.Fragment;

import java.util.UUID;

public class RecipeDisplayActivity extends SingleFragmentActivity {
    private static final String EXTRA_RECIPE_ID="com.fdx.cookbook.recipe_id";
    private static final String EXTRA_GUEST="com.fdx.cookbook.guest";

    public static Intent newIntent(Context packageContexte, UUID recipeId) {
        Intent intent=new Intent(packageContexte, RecipeDisplayActivity.class);
        intent.putExtra(EXTRA_RECIPE_ID, recipeId);
        return intent;
    }

    /**
     * Read-only display of a recipe that is NOT in the local cookbook (planned in the agenda).
     * The recipe, downloaded by AgendaApi.getRecipe, is handed over with RecipeDisplayFragment.setGuestRecipe.
     */
    public static Intent newGuestIntent(Context packageContexte, Recipe guest) {
        RecipeDisplayFragment.setGuestRecipe(guest);
        Intent intent=newIntent(packageContexte, guest.getId());
        intent.putExtra(EXTRA_GUEST, true);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        UUID recipeId=(UUID) getIntent().getSerializableExtra(EXTRA_RECIPE_ID);
        if (getIntent().getBooleanExtra(EXTRA_GUEST, false)) {
            return RecipeDisplayFragment.newGuestInstance(recipeId);
        }
        return RecipeDisplayFragment.newInstance(recipeId);
    }
}
