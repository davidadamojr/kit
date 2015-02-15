package com.davidadamojr.android.keepintouch.data;

import android.content.Context;
import android.util.Log;

import com.davidadamojr.android.keepintouch.util.ReminderJSONSerializer;

import java.util.ArrayList;
import java.util.UUID;

public class ReminderLab {
    private static final String TAG = "ReminderLab";
    private static final String FILENAME = "reminders.json";

    private ArrayList<Reminder> mReminders;
    private ReminderJSONSerializer mSerializer;

    private static ReminderLab sReminderLab;
    private Context mAppContext;

    public ReminderLab(Context appContext){
        mAppContext = appContext;
        mSerializer = new ReminderJSONSerializer(mAppContext, FILENAME);

        try {
            mReminders = mSerializer.loadReminders();
        } catch (Exception e) {
            mReminders = new ArrayList<Reminder>();
            Log.e(TAG, "Error loading reminders: ", e);
        }
    }

    public boolean saveReminders(){
        try {
            mSerializer.saveReminders(mReminders);
            Log.d(TAG, "Reminders saved to file");
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error saving reminders: ", e);
            return false;
        }
    }

    public static ReminderLab get(Context c){
        if (sReminderLab == null){
            sReminderLab = new ReminderLab(c.getApplicationContext());
        }

        return sReminderLab;
    }

    public Reminder getReminder(int id){
        for (Reminder reminder : mReminders){
            if (reminder.getId() == id)
                return reminder;
        }
        return null;
    }

    public void addReminder(Reminder reminder) { mReminders.add(reminder); Log.i(TAG, Integer.toString(mReminders.size())); }

    public void deleteReminder(Reminder reminder) { mReminders.remove(reminder); }

    public ArrayList<Reminder> getReminders() { return mReminders; }
}
