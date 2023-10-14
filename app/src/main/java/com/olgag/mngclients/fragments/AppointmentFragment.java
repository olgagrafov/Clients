package com.olgag.mngclients.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.olgag.mngclients.R;
import com.olgag.mngclients.controler.Alert;
import com.olgag.mngclients.controler.AppointmentAdapter;
import com.olgag.mngclients.db.AppointmentDBHelper;
import com.olgag.mngclients.db.NotesDBHelper;
import com.olgag.mngclients.methods.MethodsForApp;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AppointmentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AppointmentFragment extends Fragment implements View.OnClickListener,
        NotesDBHelper.OnGetSMSTemplateSuccess,
        DatePickerFragment.OnDateSetClickListener,
        AdapterView.OnItemSelectedListener {

    private static final String TAB_NUMBER = "tab_number";
    private static final String TAB_NAME = "calendar";
    private static final String USER_ID = "userid";

    private Spinner spinner;
    private RecyclerView recAppList;
    private ImageButton searchAppointment;
    private TextView txtViDateFromApp, txtViDdateToApp, txtTemplateSMS;
    private Calendar calendar;
    private DialogFragment dateFragment;
    private long fromdDateApp, toDateApp;
    private boolean isClickOnFrom, isSMSSent, isFilterBySMS = false;
    private AppointmentDBHelper appointmentDB;
    private AppointmentAdapter appointmentAdapter;
    private Animation myAnim;
    private NotesDBHelper smsTemplateDB;

    private int mParam1;
    private String mParam2;
    private String userId, strSMSTemplate;



    public AppointmentFragment() {
        // Required empty public constructor
    }


    public static AppointmentFragment newInstance(int param1, String param2, String userId) {
        AppointmentFragment fragment = new AppointmentFragment();
        Bundle args = new Bundle();
        args.putInt(TAB_NUMBER, param1);
        args.putString(TAB_NAME, param2);
        args.putString(USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getInt(TAB_NUMBER);
            mParam2 = getArguments().getString(TAB_NAME);
            userId = getArguments().getString(USER_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vw = inflater.inflate(R.layout.fragment_appointment, container, false);

        smsTemplateDB = new NotesDBHelper(getContext(), userId, true);


        txtTemplateSMS =  vw.findViewById(R.id.sms_template);
        txtTemplateSMS.setOnClickListener(this);


        calendar = Calendar.getInstance();
        fromdDateApp = calendar.getTimeInMillis();
        calendar.add(Calendar.MONTH, +1);
        toDateApp = calendar.getTimeInMillis();

        txtViDateFromApp = vw.findViewById(R.id.app_from);
        txtViDateFromApp.setOnClickListener(this);
        txtViDateFromApp.setText(MethodsForApp.formatDateToString(fromdDateApp));

        txtViDdateToApp = vw.findViewById(R.id.app_to);
        txtViDdateToApp.setOnClickListener(this);
        txtViDdateToApp.setText(MethodsForApp.formatDateToString(toDateApp));

        spinner = vw.findViewById(R.id.spinner_is_sms);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.sms_sent, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);



        searchAppointment = vw.findViewById(R.id.search_app_by_date);
        searchAppointment.setOnClickListener(this);
        myAnim = AnimationUtils.loadAnimation(getContext(), R.anim.img_click);

        dateFragment = new DatePickerFragment(getContext());

        recAppList = vw.findViewById(R.id.recycViewAppointments);
        recAppList.setLayoutManager(new GridLayoutManager(getContext(), 1));


        appointmentAdapter = new AppointmentAdapter(getContext(), userId);
        appointmentDB = new AppointmentDBHelper(getContext(), userId, appointmentAdapter, recAppList);

        return vw;

    }

    @Override
    public void onResume() {
        super.onResume();
        spinner.setSelection(0);
        appointmentDB.getAllAppointmentsList(fromdDateApp);
        smsTemplateDB.getSMSTemplateFromDB();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.app_from:
                dateFragment.show(getFragmentManager(), "datePicker");
                isClickOnFrom = true;
                break;
            case R.id.app_to:
                dateFragment.show(getFragmentManager(), "datePicker");
                isClickOnFrom = false;
                break;
            case R.id.sms_template:

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(getString(R.string.SMS_template));

                final EditText input = new EditText(getContext());
                input.setText(strSMSTemplate);
                builder.setView(input);

                builder.setPositiveButton(getString(R.string.save), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        strSMSTemplate = input.getText().toString();
                        smsTemplateDB.addUpdateSMSTemplate(strSMSTemplate);
                        appointmentAdapter.setStrSMSTemplate(strSMSTemplate);

                    }
                });

                builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog smsAlert = builder.create();
                smsAlert.show();

                Button buttonPositive =  smsAlert.getButton(AlertDialog.BUTTON_POSITIVE);
                Button buttonNegative =  smsAlert.getButton(AlertDialog.BUTTON_NEGATIVE);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(50,0,50,0);
                buttonPositive.setLayoutParams(params);
                buttonNegative.setLayoutParams(params);

                break;
            case R.id.search_app_by_date:
                searchAppointment.startAnimation(myAnim);
                if(fromdDateApp<toDateApp) {
                    if(isFilterBySMS)
                        appointmentAdapter.filter(fromdDateApp, toDateApp, isSMSSent);
                    else
                        appointmentAdapter.filter(fromdDateApp,toDateApp);
                }
                else {
                    new Alert(getContext(), getString(R.string.date_error), Gravity.CENTER,
                            Toast.LENGTH_LONG, ContextCompat.getColor(getContext(), R.color.error_color));
                }
                break;
        }
    }


    @Override
    public void setSMSTemplate(String smsTemplate) {
        strSMSTemplate = smsTemplate;
        appointmentAdapter.setStrSMSTemplate(smsTemplate);
    }

    @Override
    public void setDate(int year, int month, int dayOfMonth) {
        calendar = new GregorianCalendar(year, month, dayOfMonth);

        if(isClickOnFrom){
            fromdDateApp = calendar.getTimeInMillis();
            spinner.setSelection(0);
            appointmentDB.getAllAppointmentsList(fromdDateApp);
            txtViDateFromApp.setText(MethodsForApp.formatDateToString(fromdDateApp));
        }
        else {
            calendar.add(Calendar.HOUR, 12);
            toDateApp = calendar.getTimeInMillis();
            txtViDdateToApp.setText(MethodsForApp.formatDateToString(toDateApp));
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position){
            case 0:
                isFilterBySMS = false;
                break;
            case 1:
                isSMSSent = true;
                isFilterBySMS = true;
                break;
            case 2:
                isSMSSent = false;
                isFilterBySMS = true;
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}