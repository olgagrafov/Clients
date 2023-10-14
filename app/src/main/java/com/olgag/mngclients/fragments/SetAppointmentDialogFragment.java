package com.olgag.mngclients.fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.olgag.mngclients.R;
import com.olgag.mngclients.db.AppointmentDBHelper;
import com.olgag.mngclients.methods.MethodsForApp;
import com.olgag.mngclients.model.Appointment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

//import android.widget.CalendarView;

public class SetAppointmentDialogFragment extends DialogFragment implements
        View.OnClickListener, AppointmentDBHelper.OnSetArrAppointmentStart, OnDayClickListener {

    private TextView txt900, txt915,txt930, txt945,
            txt1000, txt1015, txt1030, txt1045,
            txt1100, txt1115, txt1130, txt1145,
            txt1200, txt1215, txt1230, txt1245,
            txt1300, txt1315, txt1330, txt1345,
            txt1400, txt1415, txt1430, txt1445,
            txt1500, txt1515, txt1530, txt1545,
            txt1600, txt1615, txt1630, txt1645,
            txt1700, txt1715, txt1730, txt1745;
    private Context context;
    private String userId;
    private AppointmentDBHelper appointmentDB;
    private long appointmentStart;
    private Calendar mCal;
    private ArrayList<Long> arrAppointmentTime = new ArrayList<>();
    private OnAppointmentSetClickListener listener;
    private CalendarView calendarView;

    public SetAppointmentDialogFragment(Context con, String usId) {
        context = con;
        userId = usId;
        listener = (OnAppointmentSetClickListener)con;
    }


    public interface OnAppointmentSetClickListener{
        void setAppointment(long appStart);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View vw = inflater.inflate(R.layout.dialog_set_appointment, container, false);

        return init(vw);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog appointmentDialog = super.onCreateDialog(savedInstanceState);
        appointmentDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return appointmentDialog;
    }

    @Override
    public void setArrAppointmentTime(ArrayList<Long> arrAppointmentStart) {
        arrAppointmentTime.clear();
        if(arrAppointmentStart.size()>0) {
            arrAppointmentTime.addAll(arrAppointmentStart);
            setEventsToCalendar();
        }
    }

    @Override
    public void getAppointmentDB(Appointment ap) {

    }

    //click on day and set busy  if the time was scheduled
    @Override
    public void onDayClick(EventDay eventDay) {
       // Toast.makeText(context, eventDay.isEnabled() + "first" + eventDay.getCalendar().getTimeInMillis(), Toast.LENGTH_SHORT).show();
        if(eventDay.isEnabled()) {
            setAllFree();
            String strC = MethodsForApp.formatDateWithHourToStringForArray(eventDay.getCalendar().getTimeInMillis()).substring(0,6);
            mCal = eventDay.getCalendar();

            for(int i=0; i<arrAppointmentTime.size(); i++) {
                String timrStr = MethodsForApp.formatDateWithHourToStringForArray(arrAppointmentTime.get(i));
                // Log.i("timrStr" , timrStr);
                if(timrStr.indexOf(strC)>-1) {
                String strBusy = timrStr.substring(6,11); //timrStr.substring(6,8);
                Log.i("strBusy" , strBusy);
                switch (strBusy) {
                    case "09:00":
                        txt900.setText(getString(R.string.busy));
                        txt900.setEnabled(false);
                        break;
                    case "09:15":
                        txt915.setText(getString(R.string.busy));
                        txt915.setEnabled(false);
                        break;
                    case "09:30":
                        txt930.setText(getString(R.string.busy));
                        txt930.setEnabled(false);
                        break;
                    case "09:45":
                        txt945.setText(getString(R.string.busy));
                        txt945.setEnabled(false);
                        break;
                    case "10:00":
                        txt1000.setText(getString(R.string.busy));
                        txt1000.setEnabled(false);
                        break;
                    case "10:15":
                        txt1015.setText(getString(R.string.busy));
                        txt1015.setEnabled(false);
                        break;
                    case "10:30":
                        txt1030.setText(getString(R.string.busy));
                        txt1030.setEnabled(false);
                        break;
                    case "10:45":
                        txt1045.setText(getString(R.string.busy));
                        txt1045.setEnabled(false);
                        break;
                    case "11:00":
                        txt1100.setText(getString(R.string.busy));
                        txt1100.setEnabled(false);
                        break;
                    case "11:15":
                        txt1115.setText(getString(R.string.busy));
                        txt1115.setEnabled(false);
                        break;
                    case "11:30":
                        txt1130.setText(getString(R.string.busy));
                        txt1130.setEnabled(false);
                        break;
                    case "11:45":
                        txt1145.setText(getString(R.string.busy));
                        txt1145.setEnabled(false);
                        break;
                    case "12:00":
                        txt1200.setText(getString(R.string.busy));
                        txt1200.setEnabled(false);
                        break;
                    case "12:15":
                        txt1215.setText(getString(R.string.busy));
                        txt1215.setEnabled(false);
                        break;
                    case "12:30":
                        txt1230.setText(getString(R.string.busy));
                        txt1230.setEnabled(false);
                        break;
                    case "12:45":
                        txt1245.setText(getString(R.string.busy));
                        txt1245.setEnabled(false);
                        break;
                    case "13:00":
                        txt1300.setText(getString(R.string.busy));
                        txt1300.setEnabled(false);
                        break;
                    case "13:15":
                        txt1315.setText(getString(R.string.busy));
                        txt1315.setEnabled(false);
                        break;
                    case "13:30":
                        txt1330.setText(getString(R.string.busy));
                        txt1330.setEnabled(false);
                        break;
                    case "13:45":
                        txt1345.setText(getString(R.string.busy));
                        txt1345.setEnabled(false);
                        break;
                    case "14:00":
                        txt1400.setText(getString(R.string.busy));
                        txt1400.setEnabled(false);
                        break;
                    case "14:15":
                        txt1415.setText(getString(R.string.busy));
                        txt1415.setEnabled(false);
                        break;
                    case "14:30":
                        txt1430.setText(getString(R.string.busy));
                        txt1430.setEnabled(false);
                        break;
                    case "14:45":
                        txt1445.setText(getString(R.string.busy));
                        txt1445.setEnabled(false);
                        break;
                    case "15:00":
                        txt1500.setText(getString(R.string.busy));
                        txt1500.setEnabled(false);
                        break;
                    case "15:15":
                        txt1515.setText(getString(R.string.busy));
                        txt1515.setEnabled(false);
                        break;
                    case "15:30":
                        txt1530.setText(getString(R.string.busy));
                        txt1530.setEnabled(false);
                        break;
                    case "15:45":
                        txt1545.setText(getString(R.string.busy));
                        txt1545.setEnabled(false);
                        break;
                    case "16:00":
                        txt1600.setText(getString(R.string.busy));
                        txt1600.setEnabled(false);
                        break;
                    case "16:15":
                        txt1615.setText(getString(R.string.busy));
                        txt1615.setEnabled(false);
                        break;
                    case "16:30":
                        txt1630.setText(getString(R.string.busy));
                        txt1630.setEnabled(false);
                        break;
                    case "16:45":
                        txt1645.setText(getString(R.string.busy));
                        txt1645.setEnabled(false);
                        break;
                    case "17:00":
                        txt1700.setText(getString(R.string.busy));
                        txt1700.setEnabled(false);
                        break;
                    case "17:15":
                        txt1715.setText(getString(R.string.busy));
                        txt1715.setEnabled(false);
                        break;
                    case "17:30":
                        txt1730.setText(getString(R.string.busy));
                        txt1730.setEnabled(false);
                        break;
                    case "17:45":
                        txt1745.setText(getString(R.string.busy));
                        txt1745.setEnabled(false);
                        break;
                }
            }
          }

        }
    }



    private void setEventsToCalendar(){ //to mark dates with events
        List<EventDay> events = new ArrayList<>();
        Calendar calendar ;
        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(arrAppointmentTime.get(0));
        //int tmpDayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
       // int eventCounts = 0;
        for(int i=0; i<arrAppointmentTime.size(); i++) {
            calendar = Calendar.getInstance();
            calendar.setTimeInMillis(arrAppointmentTime.get(i));
 //           int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);

//            if(tmpDayOfYear == dayOfYear){
//                eventCounts++;
//            }
//            else
//                eventCounts=0;
//
//            tmpDayOfYear = dayOfYear;

             events.add(new EventDay(calendar, R.drawable.ic_menu_today,  R.color.colorPrimary));

//            if(eventCounts>3) {
//                for(int j = 1; j<6; j++) {
//                  calendar =   events.get(events.size() - j).getCalendar();
//                  events.set(events.size() - j, new EventDay(calendar, R.drawable.ic_menu_today, Color.RED));
//                }
//            }
        }
        calendarView.setEvents(events);

        //if first day has events mark the busy time
        String strNow = MethodsForApp.formatDateWithHourToStringForArray(appointmentStart).substring(0,6);
        String firstApointmentStr = MethodsForApp.formatDateWithHourToStringForArray(arrAppointmentTime.get(0));
        if(firstApointmentStr.indexOf(strNow)>-1){
           onDayClick(events.get(0));
        }


    }

    @Override
    public void onClick(View v) {

        //if create appointment for today, i must start the  day from 00:00
        if(mCal.get(Calendar.HOUR_OF_DAY)>0) {
           // Log.i("before add appointment" , mCal.get(Calendar.MINUTE) + " - " + mCal.get(Calendar.HOUR_OF_DAY));
            int setStartTime = mCal.get(Calendar.MINUTE) + (mCal.get(Calendar.HOUR_OF_DAY) * 60);//00:00
            mCal.add(Calendar.MINUTE, -setStartTime);
          // Log.i("after appointment" , mCal.get(Calendar.MINUTE) + " - " + mCal.get(Calendar.HOUR_OF_DAY));
        }
    //  Log.i("appointmentStart" , mCal.get(Calendar.DATE) + " - " + mCal.get(Calendar.HOUR_OF_DAY )+ " - " + mCal.get(Calendar.MINUTE));

        switch (v.getId()){
            case R.id.text900:
                mCal.add(Calendar.MINUTE, 540);
                break;
            case R.id.text915:
                mCal.add(Calendar.MINUTE, 555);
                break;
            case R.id.text930:
                mCal.add(Calendar.MINUTE, 570);
                break;
            case R.id.text945:
                mCal.add(Calendar.MINUTE, 585);
                break;
            case R.id.text1000:
                mCal.add(Calendar.MINUTE, 600);
                break;
            case R.id.text1015:
                mCal.add(Calendar.MINUTE, 615);
                break;
            case R.id.text1030:
                mCal.add(Calendar.MINUTE, 630);
                break;
            case R.id.text1045:
                mCal.add(Calendar.MINUTE, 645);
                break;
            case R.id.text1100:
                mCal.add(Calendar.MINUTE, 660);
                break;
            case R.id.text1115:
                mCal.add(Calendar.MINUTE, 675);
                break;
            case R.id.text1130:
                mCal.add(Calendar.MINUTE, 690);
                break;
            case R.id.text1145:
                mCal.add(Calendar.MINUTE, 705);
                break;
            case R.id.text1200:
                mCal.add(Calendar.MINUTE, 720);
                break;
            case R.id.text1215:
                mCal.add(Calendar.MINUTE, 735);
                break;
            case R.id.text1230:
                mCal.add(Calendar.MINUTE, 750);
                break;
            case R.id.text1245:
                mCal.add(Calendar.MINUTE, 765);
                break;
            case R.id.text1300:
                mCal.add(Calendar.MINUTE, 780);;
                break;
            case R.id.text1315:
                mCal.add(Calendar.MINUTE, 795);
                break;
            case R.id.text1330:
                mCal.add(Calendar.MINUTE, 810);
                break;
            case R.id.text1345:
                mCal.add(Calendar.MINUTE, 825);
                break;
            case R.id.text1400:
                mCal.add(Calendar.MINUTE, 840);
                break;
            case R.id.text1415:
                mCal.add(Calendar.MINUTE, 855);
                break;
            case R.id.text1430:
                mCal.add(Calendar.MINUTE, 870);
                break;
            case R.id.text1445:
                mCal.add(Calendar.MINUTE, 885);
                break;
            case R.id.text1500:
                mCal.add(Calendar.MINUTE, 900);
                break;
            case R.id.text1515:
                mCal.add(Calendar.MINUTE, 915);
                break;
            case R.id.text1530:
                mCal.add(Calendar.MINUTE, 930);
                break;
            case R.id.text1545:
                mCal.add(Calendar.MINUTE, 945);
                break;
            case R.id.text1600:
                mCal.add(Calendar.MINUTE, 960);
                break;
            case R.id.text1615:
                mCal.add(Calendar.MINUTE, 975);
                break;
            case R.id.text1630:
                mCal.add(Calendar.MINUTE, 990);
                break;
            case R.id.text1645:
                mCal.add(Calendar.MINUTE, 1005);
                break;
            case R.id.text1700:
                mCal.add(Calendar.MINUTE, 1020);
                break;
            case R.id.text1715:
                mCal.add(Calendar.MINUTE, 1035);
                break;
            case R.id.text1730:
                mCal.add(Calendar.MINUTE, 1050);
                break;
            case R.id.text1745:
                mCal.add(Calendar.MINUTE, 1065);
                break;

        }
        appointmentStart = mCal.getTimeInMillis();
        listener.setAppointment(appointmentStart);
        dismiss();
    }

    private View init(View vw){
        mCal= Calendar.getInstance();

        appointmentStart = mCal.getTimeInMillis();
        appointmentDB = new AppointmentDBHelper(context, userId);
        appointmentDB.getAllAppointments(appointmentStart);



        calendarView = (CalendarView) vw.findViewById(R.id.calendarView);
        Calendar min = Calendar.getInstance();
        Calendar max = Calendar.getInstance();
        min.add(Calendar.DATE,-1);
        max.add(Calendar.MONTH,1);
        calendarView.setMinimumDate(min);
        calendarView.setMaximumDate(max);
        calendarView.setOnDayClickListener(this);

        txt900= vw.findViewById(R.id.text900);
        txt900.setOnClickListener(this);
        txt915= vw.findViewById(R.id.text915);
        txt915.setOnClickListener(this);
        txt930= vw.findViewById(R.id.text930);
        txt930.setOnClickListener(this);
        txt945= vw.findViewById(R.id.text945);
        txt945.setOnClickListener(this);

        txt1000= vw.findViewById(R.id.text1000);
        txt1000.setOnClickListener(this);
        txt1015= vw.findViewById(R.id.text1015);
        txt1015.setOnClickListener(this);
        txt1030= vw.findViewById(R.id.text1030);
        txt1030.setOnClickListener(this);
        txt1045= vw.findViewById(R.id.text1045);
        txt1045.setOnClickListener(this);

        txt1100= vw.findViewById(R.id.text1100);
        txt1100.setOnClickListener(this);
        txt1115= vw.findViewById(R.id.text1115);
        txt1115.setOnClickListener(this);
        txt1130= vw.findViewById(R.id.text1130);
        txt1130.setOnClickListener(this);
        txt1145= vw.findViewById(R.id.text1145);
        txt1145.setOnClickListener(this);

        txt1200= vw.findViewById(R.id.text1200);
        txt1200.setOnClickListener(this);
        txt1215= vw.findViewById(R.id.text1215);
        txt1215.setOnClickListener(this);
        txt1230= vw.findViewById(R.id.text1230);
        txt1230.setOnClickListener(this);
        txt1245= vw.findViewById(R.id.text1245);
        txt1245.setOnClickListener(this);

        txt1300= vw.findViewById(R.id.text1300);
        txt1300.setOnClickListener(this);
        txt1315= vw.findViewById(R.id.text1315);
        txt1315.setOnClickListener(this);
        txt1330= vw.findViewById(R.id.text1330);
        txt1330.setOnClickListener(this);
        txt1345= vw.findViewById(R.id.text1345);
        txt1345.setOnClickListener(this);

        txt1400= vw.findViewById(R.id.text1400);
        txt1400.setOnClickListener(this);
        txt1415= vw.findViewById(R.id.text1415);
        txt1415.setOnClickListener(this);
        txt1430= vw.findViewById(R.id.text1430);
        txt1430.setOnClickListener(this);
        txt1445= vw.findViewById(R.id.text1445);
        txt1445.setOnClickListener(this);

        txt1500= vw.findViewById(R.id.text1500);
        txt1500.setOnClickListener(this);
        txt1515= vw.findViewById(R.id.text1515);
        txt1515.setOnClickListener(this);
        txt1530= vw.findViewById(R.id.text1530);
        txt1530.setOnClickListener(this);
        txt1545= vw.findViewById(R.id.text1545);
        txt1545.setOnClickListener(this);

        txt1600= vw.findViewById(R.id.text1600);
        txt1600.setOnClickListener(this);
        txt1615= vw.findViewById(R.id.text1615);
        txt1615.setOnClickListener(this);
        txt1630= vw.findViewById(R.id.text1630);
        txt1630.setOnClickListener(this);
        txt1645= vw.findViewById(R.id.text1645);
        txt1645.setOnClickListener(this);

        txt1700= vw.findViewById(R.id.text1700);
        txt1700.setOnClickListener(this);
        txt1715= vw.findViewById(R.id.text1715);
        txt1715.setOnClickListener(this);
        txt1730= vw.findViewById(R.id.text1730);
        txt1730.setOnClickListener(this);
        txt1745= vw.findViewById(R.id.text1745);
        txt1745.setOnClickListener(this);

        return vw;
    }

    private void setAllFree(){
        txt900.setText("09:00");
        txt900.setEnabled(true);
        txt915.setText("09:15");
        txt915.setEnabled(true);
        txt930.setText("09:30");
        txt930.setEnabled(true);
        txt945.setText("09:45");
        txt945.setEnabled(true);

        txt1000.setText("10:00");
        txt1000.setEnabled(true);
        txt1015.setText("10:15");
        txt1015.setEnabled(true);
        txt1030.setText("10:30");
        txt1030.setEnabled(true);
        txt1045.setText("10:45");
        txt1045.setEnabled(true);

        txt1100.setText("11:00");
        txt1100.setEnabled(true);
        txt1115.setText("11:15");
        txt1115.setEnabled(true);
        txt1130.setText("11:30");
        txt1130.setEnabled(true);
        txt1145.setText("11:45");
        txt1145.setEnabled(true);

        txt1200.setText("12:00");
        txt1200.setEnabled(true);
        txt1215.setText("12:15");
        txt1215.setEnabled(true);
        txt1230.setText("12:30");
        txt1230.setEnabled(true);
        txt1245.setText("12:45");
        txt1245.setEnabled(true);

        txt1300.setText("13:00");
        txt1300.setEnabled(true);
        txt1315.setText("13:15");
        txt1315.setEnabled(true);
        txt1330.setText("13:30");
        txt1330.setEnabled(true);
        txt1345.setText("13:45");
        txt1345.setEnabled(true);

        txt1400.setText("14:00");
        txt1400.setEnabled(true);
        txt1415.setText("14:15");
        txt1415.setEnabled(true);
        txt1430.setText("14:30");
        txt1430.setEnabled(true);
        txt1445.setText("14:45");
        txt1445.setEnabled(true);

        txt1500.setText("15:00");
        txt1500.setEnabled(true);
        txt1515.setText("15:15");
        txt1515.setEnabled(true);
        txt1530.setText("15:30");
        txt1530.setEnabled(true);
        txt1545.setText("15:45");
        txt1545.setEnabled(true);

        txt1600.setText("16:00");
        txt1600.setEnabled(true);
        txt1615.setText("16:15");
        txt1615.setEnabled(true);
        txt1630.setText("16:30");
        txt1630.setEnabled(true);
        txt1645.setText("16:45");
        txt1645.setEnabled(true);

        txt1700.setText("17:00");
        txt1700.setEnabled(true);
        txt1715.setText("17:15");
        txt1715.setEnabled(true);
        txt1730.setText("17:30");
        txt1730.setEnabled(true);
        txt1745.setText("17:45");
        txt1745.setEnabled(true);
    }


}
