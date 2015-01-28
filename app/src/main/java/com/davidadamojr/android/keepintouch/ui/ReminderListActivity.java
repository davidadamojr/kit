package com.davidadamojr.android.keepintouch.ui;

import android.support.v4.app.Fragment;

public class ReminderListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment(){
        return new ReminderListFragment();
    }


}