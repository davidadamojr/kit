package com.davidadamojr.android.keepintouch.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.util.Log;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import com.davidadamojr.android.keepintouch.R;
import com.davidadamojr.android.keepintouch.core.ReminderAlarmReceiver;
import com.davidadamojr.android.keepintouch.data.Reminder;
import com.davidadamojr.android.keepintouch.data.ReminderLab;

public class ReminderListFragment extends ListFragment {

    private ArrayList<Reminder> mReminders;

    private Button mNewReminder;

    private static final String TAG = "ReminderListFragment";

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_reminder_list, menu);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
        getActivity().getMenuInflater().inflate(R.menu.context_reminder_list, menu);
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        getActivity().setTitle("Reminders");
        mReminders = ReminderLab.get(getActivity()).getReminders();

        ReminderAdapter adapter = new ReminderAdapter(mReminders);
        setListAdapter(adapter);

        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_reminder_list, parent, false);

        mNewReminder = (Button) v.findViewById(R.id.empty_new_reminder);
        mNewReminder.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                createNewReminder();
            }
        });

        ListView listView = (ListView) v.findViewById(android.R.id.list);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB){
            // Use floating context menus on Froyo and Gingerbread
            registerForContextMenu(listView);
        } else {
            // Use contextual action bar on Honeycomb and higher
            listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
            listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
                @Override
                public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {
                    // Required, but not used in this implementation
                }

                @TargetApi(Build.VERSION_CODES.HONEYCOMB)
                @Override
                public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                    MenuInflater inflater = actionMode.getMenuInflater();
                    inflater.inflate(R.menu.context_reminder_list, menu);
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                    return false;
                }

                @TargetApi(Build.VERSION_CODES.HONEYCOMB)
                @Override
                public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.menu_item_delete_reminder:
                            ReminderAdapter adapter = (ReminderAdapter) getListAdapter();
                            ReminderLab reminderLab = ReminderLab.get(getActivity());
                            for (int i = adapter.getCount() - 1; i >= 0; i--) {
                                if (getListView().isItemChecked(i)) {
                                    Reminder reminder = (Reminder) adapter.getItem(i);
                                    reminderLab.deleteReminder(reminder);

                                    // disable alarm for this reminder
                                    AlarmManager alarmManager = (AlarmManager) getActivity().getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                                    Intent alarmIntent = new Intent(getActivity().getApplicationContext(), ReminderAlarmReceiver.class);
                                    alarmIntent.putExtra(ContactListFragment.ID_EXTRA, reminder.getId());
                                    alarmIntent.putExtra(ContactListFragment.PHONE_NUMBER_EXTRA, reminder.getPhoneNumber());
                                    alarmIntent.putExtra(ContactListFragment.NAME_EXTRA, reminder.getContactName());
                                    alarmIntent.putExtra(ContactListFragment.TIME_EXTRA, reminder.getNextReminder());
                                    boolean alarmUp = (PendingIntent.getBroadcast(getActivity().getApplicationContext(), reminder.getId(), alarmIntent, PendingIntent.FLAG_NO_CREATE) != null);
                                    if (alarmUp) {
                                        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity().getApplicationContext(), reminder.getId(), alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                                        alarmManager.cancel(pendingIntent);
                                    } else {
                                        Log.i(TAG, "Alarm does not exist!!!!");
                                    }

                                }
                            }

                            actionMode.finish();
                            adapter.notifyDataSetChanged();
                            return true;
                        default:
                            return false;
                    }
                }

                @Override
                public void onDestroyActionMode(ActionMode actionMode) {
                    // Required, but not used in this implementation
                }
            });
        }

        return v;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.menu_item_new_reminder:
                createNewReminder();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void createNewReminder(){
        Intent intent = new Intent(getActivity(), ContactListActivity.class);
        startActivity(intent);
    }

    @Override
    public void onResume(){
        super.onResume();
        ((ReminderAdapter) getListAdapter()).notifyDataSetChanged();
        Log.i(TAG, "Called onResume in ReminderListFragment");
        Log.i(TAG, "The length of mReminders is: " + Integer.toString(mReminders.size()));
    }

    private class ReminderAdapter extends ArrayAdapter<Reminder> {

        public ReminderAdapter(ArrayList<Reminder> reminders) { super(getActivity(), 0, reminders); }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            // if we weren't given a view, inflate one
            if (convertView == null){
                convertView = getActivity().getLayoutInflater()
                        .inflate(R.layout.item_reminder_list, null);
            }

            // configure the view for this reminder
            Reminder reminder = getItem(position);

            TextView contactNameTextView = (TextView) convertView.findViewById(R.id.reminder_list_item_contactName);
            contactNameTextView.setText(reminder.getContactName());
            TextView phoneNoTextView = (TextView) convertView.findViewById(R.id.reminder_list_item_number);
            phoneNoTextView.setText(reminder.getPhoneNumber());
            TextView frequencyTextView = (TextView) convertView.findViewById(R.id.reminder_list_item_frequency);
            frequencyTextView.setText(reminder.getFrequency());

            return convertView;
        }
    }

    public void updateList(){
        ((ReminderAdapter) getListAdapter()).notifyDataSetChanged();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item){
        AdapterViewCompat.AdapterContextMenuInfo info = (AdapterViewCompat.AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position;
        ReminderAdapter adapter = (ReminderAdapter) getListAdapter();
        Reminder reminder = adapter.getItem(position);

        switch (item.getItemId()){
            case R.id.menu_item_delete_reminder:
                ReminderLab.get(getActivity()).deleteReminder(reminder);
                adapter.notifyDataSetChanged();
                return true;
        }

        return super.onContextItemSelected(item);
    }

    @Override
    public void onPause(){
        super.onPause();
        // onPause is a good place to save persistent data
        ReminderLab.get(getActivity()).saveReminders();
    }
}


