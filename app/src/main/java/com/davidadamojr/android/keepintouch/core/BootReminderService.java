package com.davidadamojr.android.keepintouch.core;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.davidadamojr.android.keepintouch.data.Reminder;
import com.davidadamojr.android.keepintouch.ui.ContactListFragment;

import java.util.ArrayList;

public class BootReminderService extends IntentService {
    public BootReminderService() {
        super("BootReminderService");
    }

    public void onHandleIntent(Intent intent){
        ArrayList<Reminder> reminders = intent.getParcelableArrayListExtra(ReminderBootReceiver.REMINDERS_EXTRA);
        for (Reminder reminder : reminders){
            Intent alarmIntent = new Intent(getApplicationContext(), ReminderAlarmReceiver.class);
            alarmIntent.putExtra(ContactListFragment.ID_EXTRA, reminder.getId());
            alarmIntent.putExtra(ContactListFragment.PHONE_NUMBER_EXTRA, reminder.getPhoneNumber());
            alarmIntent.putExtra(ContactListFragment.NAME_EXTRA, reminder.getContactName());
            alarmIntent.putExtra(ContactListFragment.TIME_EXTRA, reminder.getNextReminder());
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), reminder.getId(), alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, reminder.getNextReminder(), pendingIntent);
        }

        stopService(intent);
    }
}
