package com.davidadamojr.android.keepintouch.data;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

public class Reminder implements Parcelable {

    private static final String JSON_ID = "id"; //id of the pending intent
    private static final String JSON_CONTACT_NAME = "contact_name";
    private static final String JSON_FREQUENCY = "frequency"; //reminder interval
    private static final String JSON_PHONE_NUMBER = "phone_number"; // phone number of contact
    private static final String JSON_NEXT_REMINDER = "next_reminder"; // the next scheduled reminder

    private int mId;
    private String mContactName;
    private String mFrequency;
    private String mPhoneNumber;
    private long mNextReminder;

    public Reminder(String contactName, String frequency, String phoneNumber, long nextReminder){
        //Generate unique identifier
        mId = (int) System.currentTimeMillis() / 1000 * -1; // timestamps are negative
        mContactName = contactName;
        mFrequency = frequency;
        mPhoneNumber = phoneNumber;
        mNextReminder = nextReminder;
    }

    public Reminder(JSONObject json) throws JSONException {
        mId = Integer.parseInt(json.getString(JSON_ID));
        mContactName = json.getString(JSON_CONTACT_NAME);
        mFrequency = json.getString(JSON_FREQUENCY);
        mPhoneNumber = json.getString(JSON_PHONE_NUMBER);
        mNextReminder = json.getLong(JSON_NEXT_REMINDER);
    }

    public void setNextReminder(long nextReminder){
        mNextReminder = nextReminder;
    }

    public String getContactName() { return mContactName; }

    public String getFrequency() { return mFrequency; }

    public String getPhoneNumber() { return mPhoneNumber; }

    public void setFrequency(String frequency){
        mFrequency = frequency;
    }

    public long getNextReminder() { return mNextReminder; }

    public int getId() { return mId; }

    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(JSON_ID, Integer.toString(mId));
        json.put(JSON_CONTACT_NAME, mContactName);
        json.put(JSON_FREQUENCY, mFrequency);
        json.put(JSON_PHONE_NUMBER, mPhoneNumber);
        json.put(JSON_NEXT_REMINDER, mNextReminder);

        return json;
    }

    public static final Parcelable.Creator<Reminder> CREATOR = new Parcelable.Creator<Reminder>(){
        public Reminder createFromParcel(Parcel in){
            int id = in.readInt();
            String contactName = in.readString();
            String frequency = in.readString();
            String phoneNumber = in.readString();
            long nextReminder = in.readLong();
            Reminder reminder = new Reminder(contactName, frequency, phoneNumber, nextReminder);
            return reminder;
        }

        public Reminder[] newArray(int size){
            return new Reminder[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(mId);
        parcel.writeString(mContactName);
        parcel.writeString(mFrequency);
        parcel.writeString(mPhoneNumber);
        parcel.writeLong(mNextReminder);
    }
}
