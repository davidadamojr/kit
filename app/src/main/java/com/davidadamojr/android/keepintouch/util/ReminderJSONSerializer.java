package com.davidadamojr.android.keepintouch.util;

import android.content.Context;

import com.davidadamojr.android.keepintouch.data.Reminder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

public class ReminderJSONSerializer {

    private Context mContext;
    private String mFilename;

    public ReminderJSONSerializer(Context c, String f){
        mContext = c;
        mFilename = f;
    }

    public void saveReminders(ArrayList<Reminder> reminders) throws JSONException, IOException {
        //Build an array in JSON
        JSONArray array = new JSONArray();
        for (Reminder reminder : reminders){
            array.put(reminder.toJSON());
        }

        //Write the file to disk
        Writer writer = null;
        try {
            OutputStream out = mContext.openFileOutput(mFilename, Context.MODE_PRIVATE);
            writer = new OutputStreamWriter(out);
            writer.write(array.toString());
        } finally {
            if (writer != null)
                writer.close();
        }
    }

    public ArrayList<Reminder> loadReminders() throws IOException, JSONException {
        ArrayList<Reminder> reminders = new ArrayList<Reminder>();
        BufferedReader reader = null;
        try {
            //Open and read the file into a StringBuilder
            InputStream in = mContext.openFileInput(mFilename);
            reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder jsonString = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null){
                //Line breaks are omitted and irrelevant
                jsonString.append(line);
            }

            //Parse the JSON using JSONTokener
            JSONArray array = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();
            //Build the array of reminders from JSONObjects
            for (int i = 0; i<array.length(); i++){
                reminders.add(new Reminder(array.getJSONObject(i)));
            }
        } catch (FileNotFoundException e){
            //ignore this one; it happens when starting fresh
        } finally {
            if (reader != null)
                reader.close();
        }

        return reminders;
    }
}
