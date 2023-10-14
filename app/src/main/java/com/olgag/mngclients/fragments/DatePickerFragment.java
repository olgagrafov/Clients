package com.olgag.mngclients.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {
    private Context context;
    private OnDateSetClickListener listener;

    public DatePickerFragment(Context con) {
        this.context = con;
        this.listener = (OnDateSetClickListener)con;
    }

    final Calendar c = Calendar.getInstance();
    int year = c.get(Calendar.YEAR);
    int month = c.get(Calendar.MONTH);
    int day = c.get(Calendar.DAY_OF_MONTH);
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        listener.setDate(year, month, dayOfMonth);

    }

    public interface OnDateSetClickListener{
        void setDate(int year, int month, int dayOfMonth);
    }
}