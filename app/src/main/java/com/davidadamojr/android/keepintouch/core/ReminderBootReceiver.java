package com.davidadamojr.android.keepintouch.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.davidadamojr.android.keepintouch.data.Reminder;
import com.davidadamojr.android.keepintouch.data.ReminderLab;

import java.util.ArrayList;

public class ReminderBootReceiver extends BroadcastReceiver {

    public static final String REMINDERS_EXTRA = "com.davidadamojr.android.keepintouch.reminders";

    private ArrayList<Reminder> mReminders;

    public ReminderBootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        reScheduleAlarms(context);
    }

    public void reScheduleAlarms(Context context){
        mReminders = ReminderLab.get(context).getReminders();
        Intent remindersIntent = new Intent(context, BootReminderService.class);
        remindersIntent.putParcelableArrayListExtra(REMINDERS_EXTRA, mReminders);
        context.startService(remindersIntent);
    }
}
