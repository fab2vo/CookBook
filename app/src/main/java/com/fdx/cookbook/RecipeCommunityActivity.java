package com.fdx.cookbook;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

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
