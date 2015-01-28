package com.davidadamojr.android.keepintouch.data;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

public class Reminder {

    private static final String JSON_ID = "id"; //id of the pending intent
    private static final String JSON_CONTACT_NAME = "contact_name";
    private static final String JSON_FREQUENCY = "frequency"; //reminder interval
    private static final String JSON_PHONE_NUMBER = "phone_number"; // phone number of contact

    private UUID mId;
    private String mContactName;
    private String mFrequency;
    private String mPhoneNumber;

    public Reminder(String contactName, String frequency, String phoneNumber){
        //Generate unique identifier
        mId = UUID.randomUUID();
        mContactName = contactName;
        mFrequency = frequency;
        mPhoneNumber = phoneNumber;
    }

    public Reminder(JSONObject json) throws JSONException {
        mId = UUID.fromString(json.getString(JSON_ID));
        mContactName = json.getString(JSON_CONTACT_NAME);
        mFrequency = json.getString(JSON_FREQUENCY);
        mPhoneNumber = json.getString(JSON_PHONE_NUMBER);
    }

    public String getContactName() { return mContactName; }

    public String getFrequency() { return mFrequency; }

    public String getPhoneNumber() { return mPhoneNumber; }

    public void setFrequency(String frequency){
        mFrequency = frequency;
    }

    public UUID getId() { return mId; }

    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(JSON_ID, mId.toString());
        json.put(JSON_CONTACT_NAME, mContactName);
        json.put(JSON_FREQUENCY, mFrequency);
        json.put(JSON_PHONE_NUMBER, mPhoneNumber);

        return json;
    }
}
