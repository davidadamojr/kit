package com.davidadamojr.android.keepintouch.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.Toast;

import com.davidadamojr.android.keepintouch.R;

public class SelectFrequencyFragment extends DialogFragment {
    // TODO: add custom frequency later

    private static final String TAG =  "SelectFrequencyFragment";

    private static final String[] mFrequencyOptions = new String[]{"Daily", "Weekly", "Biweekly", "Monthly"};

    public static final String EXTRA_LOOKUP_KEY = "com.davidadamojr.android.keepintouch.lookup_key";
    public static final String EXTRA_CONTACT_NAME = "com.davidadamojr.android.keepintouch.contact_name";

    private String mContactName;
    private int mSelection;

    public static SelectFrequencyFragment newInstance(String lookupKey, String contactName){
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_LOOKUP_KEY, lookupKey);
        args.putSerializable(EXTRA_CONTACT_NAME, contactName);

        SelectFrequencyFragment fragment = new SelectFrequencyFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.frequency_dialog_title);
        builder.setSingleChoiceItems(mFrequencyOptions, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mSelection = i;
            }
        });
        builder.setPositiveButton(R.string.frequency_dialog_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.i("The value of i is " + i, TAG);
                String frequency = mFrequencyOptions[mSelection];
                mContactName = (String) getArguments().getSerializable(EXTRA_CONTACT_NAME);

                // create alarm here

                String toastString = "A " + frequency.toLowerCase() + " reminder has been set for " + mContactName;
                Toast myToast = Toast.makeText(getActivity(), toastString, Toast.LENGTH_LONG);
                myToast.show();

                // pass the selected frequency back to the calling fragment (ContactListFragment)
                ((ContactListFragment) getTargetFragment()).onDialogOKPressed(frequency);

                dismiss();
            }
        });
        builder.setNegativeButton(R.string.frequency_dialog_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dismiss();
            }
        });
        return builder.create();
    }
}
