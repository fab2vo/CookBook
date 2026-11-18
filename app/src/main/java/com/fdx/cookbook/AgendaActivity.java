package com.fdx.cookbook;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

public class AgendaActivity extends SingleFragmentActivity {
    public static Intent newIntent(Context packageContexte) {
        return new Intent(packageContexte, AgendaActivity.class);
    }

    @Override
    protected Fragment createFragment() {
        return AgendaFragment.newInstance();
    }
}
