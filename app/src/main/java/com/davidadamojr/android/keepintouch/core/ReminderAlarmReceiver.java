package com.davidadamojr.android.keepintouch.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.davidadamojr.android.keepintouch.ui.ContactListFragment;

/**
 * Created by ABACUS on 2/1/2015.
 */
public class ReminderAlarmReceiver extends BroadcastReceiver {

    private static final String TAG = "ReminderAlarm";
    @Override
    public void onReceive(Context context, Intent intent){
        // Log.d(TAG, "Alarm received!");
        Intent i = new Intent(context, ReminderService.class);
        i.putExtra(ContactListFragment.NAME_EXTRA, intent.getStringExtra(ContactListFragment.NAME_EXTRA));
        i.putExtra(ContactListFragment.PHONE_NUMBER_EXTRA, intent.getStringExtra(ContactListFragment.PHONE_NUMBER_EXTRA));
        i.putExtra(ContactListFragment.ID_EXTRA, intent.getIntExtra(ContactListFragment.ID_EXTRA, 0));
        i.putExtra(ContactListFragment.TIME_EXTRA, intent.getLongExtra(ContactListFragment.TIME_EXTRA, 0));
        context.startService(i);
    }
}
