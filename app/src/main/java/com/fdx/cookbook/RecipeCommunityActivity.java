package com.fdx.cookbook;

import android.content.Context;
import android.content.Intent;
import androidx.fragment.app.Fragment;

public class RecipeCommunityActivity extends SingleFragmentActivity  {
    public static Intent newIntent(Context packageContexte) {
        Intent intent=new Intent(packageContexte, RecipeCommunityActivity.class);
        return intent;
    }
    @Override
    protected Fragment createFragment() {
        return RecipeCommunityFragment.newInstance();
    }
}
