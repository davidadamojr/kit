package com.davidadamojr.android.keepintouch.core;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.davidadamojr.android.keepintouch.R;
import com.davidadamojr.android.keepintouch.data.Reminder;
import com.davidadamojr.android.keepintouch.ui.ContactListFragment;

/**
 * Created by ABACUS on 2/1/2015.
 */
public class ReminderService extends IntentService {

    public ReminderService(){
        super("ReminderService");
    }

    @Override
    protected void onHandleIntent(Intent intent){
        Log.i("ReminderService", "Service started!");
        // create notification
        String phoneNumber = intent.getStringExtra(ContactListFragment.PHONE_NUMBER_EXTRA);
        String contactName = intent.getStringExtra(ContactListFragment.NAME_EXTRA);
        String reminderId = intent.getStringExtra(ContactListFragment.ID_EXTRA);
        String phoneNumberUri = "tel:" + phoneNumber;

        PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(Intent.ACTION_DIAL), 0);
        Notification notification = new NotificationCompat.Builder(this)
                .setTicker(R.string.call_reminder + contactName)
                .setSmallIcon(android.R.drawable.stat_notify_voicemail)
                .setContentTitle("Call Reminder")
                .setContentText("Please call " + contactName)
                .setContentIntent(pi)
                .setAutoCancel(true)
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notification);

        // schedule next alarm

        // commit without rescheduling the alarm

        stopService(intent);
    }
}
