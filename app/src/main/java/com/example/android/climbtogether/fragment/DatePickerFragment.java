package com.example.android.climbtogether.fragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import com.example.android.climbtogether.R;

import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by MD on 2017-12-13.
 */

public class DatePickerFragment extends DialogFragment {
    private DatePicker mDatePicker;

    public interface DateDialogListener {
        void onFinishDialog(Date date);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_date, null);
        mDatePicker = (DatePicker) v.findViewById(R.id.dialog_date_date_picker);
        return new android.support.v7.app.AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle("enter the title here")
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int year = mDatePicker.getYear();
                                int month = mDatePicker.getMonth();
                                int day = mDatePicker.getDayOfMonth();
                                Date date = new GregorianCalendar(year, month, day).getTime();
                                DateDialogListener activity = (DateDialogListener) getActivity();
                                activity.onFinishDialog(date);
                                dismiss();
                            }
                        })
                .create();
    }
}
