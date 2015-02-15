package com.davidadamojr.android.keepintouch.ui;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.provider.ContactsContract;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.davidadamojr.android.keepintouch.R;
import com.davidadamojr.android.keepintouch.core.ReminderAlarmReceiver;
import com.davidadamojr.android.keepintouch.core.ReminderService;
import com.davidadamojr.android.keepintouch.data.Reminder;
import com.davidadamojr.android.keepintouch.data.ReminderLab;

public class ContactListFragment extends Fragment implements
    LoaderManager.LoaderCallbacks<Cursor>,
    AdapterView.OnItemClickListener {

    /*
    * Defines an array that contains column names to move from the Cursor to the ListView
    */
    @SuppressLint("InlinedApi")
    private static final String[] FROM_COLUMNS = {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY :
            ContactsContract.Contacts.DISPLAY_NAME
    };

    private static final String[] PROJECTION = {
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.LOOKUP_KEY,
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
                    ContactsContract.Contacts.DISPLAY_NAME_PRIMARY :
                    ContactsContract.Contacts.DISPLAY_NAME
    };

    private static final String SELECTION = ContactsContract.Contacts.HAS_PHONE_NUMBER + " = 1";


    //the column index for the _ID column
    private static final int CONTACT_ID_INDEX = 0;

    //the column index for the LOOKUP_KEY column
    private static final int LOOKUP_KEY_INDEX = 1;

    //the column index for the contact name
    private static final int CONTACT_NAME_INDEX = 2;

    private static final int REQUEST_FREQUENCY = 0; //reminder frequency dialog

    /*
    * Defines an array that contains resource ids for the layout views that get the Cursor
    * column contents.
    */
    private static final int[] TO_IDS = {
        R.id.reminder_list_item_name
    };

    private static final String DIALOG_FREQUENCY = "frequency";

    public static final String ID_EXTRA = "com.davidadamojr.android.keepintouch.alarm_id";
    public static final String PHONE_NUMBER_EXTRA = "com.davidadamojr.android.keepintouch.phonenumber";
    public static final String NAME_EXTRA = "com.davidadamojr.android.keepintouch.name";
    public static final String TIME_EXTRA = "com.davidadamojr.android.keepintouch.time";

    ListView mContactsList;

    long mContactId;
    String mContactKey;
    String mContactName;
    Uri mContactUri; // Content URI for the selected contact
    String mPhoneNumber;

    // An adapter that binds the result Cursor to the ListView
    private SimpleCursorAdapter mCursorAdapter;

    DialogFragment mFrequencyDialog;

    View mActiveView; // the list item for which a frequency is currently being set

    public ContactListFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getActivity().setTitle("Contacts");
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_contact_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        mContactsList = (ListView) getActivity().findViewById(R.id.contact_list_view);

        mCursorAdapter = new SimpleCursorAdapter(
                getActivity(),
                R.layout.item_contact_list,
                null,
                FROM_COLUMNS,
                TO_IDS,
                0
        );

        mContactsList.setAdapter(mCursorAdapter);

        // set the item click listener to be the current fragment
        mContactsList.setOnItemClickListener(this);

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args){
        // load from the "Contacts table"
        Uri contentUri = ContactsContract.Contacts.CONTENT_URI;

        // no sort oder, just every row that has a phone number
        // project says we want just the _id and the name column
        return new CursorLoader(
                getActivity(),
                contentUri,
                PROJECTION,
                SELECTION,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor){
        // Put the result Cursor in the adapter for the ListView
        mCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader){
        // Delete the reference to the existing cursor
        mCursorAdapter.swapCursor(null);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View item, int position, long rowID) {
        mActiveView = item;

        SimpleCursorAdapter cursorAdapter = (SimpleCursorAdapter) parent.getAdapter();
        Cursor cursor = cursorAdapter.getCursor();

        // Move the cursor to the selected contact
        cursor.moveToPosition(position);
        mContactId = cursor.getLong(CONTACT_ID_INDEX);
        mContactKey = cursor.getString(LOOKUP_KEY_INDEX);
        mContactUri = ContactsContract.Contacts.getLookupUri(mContactId, mContactKey);
        mContactName = cursor.getString(CONTACT_NAME_INDEX);

        // show dialog
        android.support.v4.app.FragmentManager fm = getActivity().getSupportFragmentManager();
        mFrequencyDialog = SelectFrequencyFragment.newInstance(mContactKey, mContactName);
        mFrequencyDialog.setTargetFragment(ContactListFragment.this, REQUEST_FREQUENCY);
        mFrequencyDialog.show(fm, DIALOG_FREQUENCY);
    }

    public void onDialogOKPressed(String frequency){
        mPhoneNumber = getPhoneNumber();

        TextView frequencyTextView = (TextView) mActiveView.findViewById(R.id.reminder_list_item_frequency);
        frequencyTextView.setText(frequency);
        frequencyTextView.setVisibility(View.VISIBLE);

        // calculate the time of the next alarm for this reminder
        long reminderTime;
        long timeAdvance;
        if (frequency.equals("Daily")){
            timeAdvance = 864 * 100000;
            reminderTime = System.currentTimeMillis() + timeAdvance;
        } else if (frequency.equals("Weekly")){
            timeAdvance = 6048 * 100000;
            reminderTime = System.currentTimeMillis() + timeAdvance;
        } else if (frequency.equals("Biweekly")) {
            timeAdvance = 12096 * 100000;
            reminderTime = System.currentTimeMillis() + timeAdvance;
        } else if (frequency.equals("Monthly")){
            timeAdvance = 2592 * 1000000;
            reminderTime = System.currentTimeMillis() + timeAdvance;
        } else {
            timeAdvance = 0;
            reminderTime = System.currentTimeMillis();
        }

        Reminder reminder = new Reminder(mContactName, frequency, mPhoneNumber, reminderTime);
        ReminderLab.get(getActivity()).addReminder(reminder);

        // set the alarm
        Intent alarmIntent = new Intent(getActivity().getApplicationContext(), ReminderAlarmReceiver.class);
        alarmIntent.putExtra(ID_EXTRA, reminder.getId());
        alarmIntent.putExtra(PHONE_NUMBER_EXTRA, mPhoneNumber);
        alarmIntent.putExtra(NAME_EXTRA, mContactName);
        alarmIntent.putExtra(TIME_EXTRA, timeAdvance);

        // use the position of the reminder in the ArrayList as requestCode
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity().getApplicationContext(), reminder.getId(), alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Log.i("ContactListFragment", "Alarm created with ID: " + reminder.getId());
        AlarmManager alarmManager = (AlarmManager) getActivity().getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + timeAdvance, pendingIntent);
    }

    public String getPhoneNumber(){
        // get the contacts phone number so it can be added to the database - this ideally should be done on a separate thread
        Cursor cursorPhone = getActivity().getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND (" +
                        ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE + " OR " +
                        ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
                        ContactsContract.CommonDataKinds.Phone.TYPE_WORK + " OR " +
                        ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
                        ContactsContract.CommonDataKinds.Phone.TYPE_HOME + ")",
                new String[]{Long.toString(mContactId)},
                null);

        if (cursorPhone.moveToFirst()){
            mPhoneNumber = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        }

        cursorPhone.close();

        Toast phoneNoToast = Toast.makeText(getActivity(), "The phone number is " + mPhoneNumber, Toast.LENGTH_SHORT);
        phoneNoToast.show();
        return mPhoneNumber;
    }

    @Override
    public void onPause(){
        super.onPause();
        // onPause is a good place to save persistent data
        ReminderLab.get(getActivity()).saveReminders();
    }
}
