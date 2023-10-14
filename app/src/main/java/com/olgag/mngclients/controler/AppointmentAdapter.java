package com.olgag.mngclients.controler;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.olgag.mngclients.R;
import com.olgag.mngclients.db.AppointmentDBHelper;
import com.olgag.mngclients.methods.MethodsForApp;
import com.olgag.mngclients.model.Appointment;

import java.util.ArrayList;

public class AppointmentAdapter  extends RecyclerView.Adapter<AppointmentAdapter.AppointmentHolder> {
    private Context context;
    private String userId, strSMSTemplate;
    private ArrayList<Appointment> arrApprList = new ArrayList<>();
    private ArrayList<Appointment> arrDateList = new ArrayList<>();
    private int position;

    public String getStrSMSTemplate() {
        return strSMSTemplate;
    }

    public void setStrSMSTemplate(String strSMSTemplate) {
        this.strSMSTemplate = strSMSTemplate;
    }

    public AppointmentAdapter(Context context, String userId) {
        this.context = context;
        this.userId = userId;
    }

    public void add(Appointment appointment){
        arrApprList.add(appointment);
        arrDateList.add(appointment);
        notifyItemInserted(arrApprList.size()-1);
    }

    public void addAll(ArrayList<Appointment> appointments){
        this.arrDateList.addAll(appointments);
        this.arrApprList.addAll(appointments);
    }

    public void del(Appointment appointment){
        arrApprList.remove(appointment);
        arrDateList.remove(appointment);
        notifyItemRemoved(position);
    }

    public void delAll(){
        int size = arrApprList.size();
        arrApprList.clear();
        arrDateList.clear();
        notifyItemRangeRemoved(0, size);
    }

    public ArrayList<Appointment> getAppointmetsList() {
        return arrApprList;
    }

    @NonNull
    @Override
    public AppointmentAdapter.AppointmentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return  new AppointmentHolder(LayoutInflater.from(context).inflate(R.layout.appointment_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentAdapter.AppointmentHolder holder, int position) {
        holder.bind(arrApprList.get(position));
    }

    @Override
    public int getItemCount() {
        return arrApprList.size();
    }

    public void filter(long fromdDate, long toDate) {
        arrApprList.clear();
        for (Appointment ap : arrDateList) {
            if (ap.getAppointmentStart() >= fromdDate && ap.getAppointmentStart() <= toDate) {
                arrApprList.add(ap);
            }
        }
        notifyDataSetChanged();
    }
    public void filter(long fromdDate, long toDate, boolean smsSent) {
        arrApprList.clear();
        for (Appointment ap : arrDateList) {
            if ((ap.getAppointmentStart() >= fromdDate && ap.getAppointmentStart() <= toDate) && ap.isSMSSent() == smsSent) {
                arrApprList.add(ap);
            }
        }
        notifyDataSetChanged();
    }

    //******************************AppointmentHolder*************************
    public class AppointmentHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView txtClientName, txtAppointmentStart;
        private ImageButton btnSentSMS;
        private FloatingActionButton  btnDelApp;
        private Appointment currentAppointment;

        public AppointmentHolder(@NonNull View itemView) {
            super(itemView);
            txtClientName  = itemView.findViewById(R.id.txtViewClientAppName);
            txtAppointmentStart  = itemView.findViewById(R.id.txtViewAppTime);
            btnSentSMS  = itemView.findViewById(R.id.img_btn_sms);
            btnSentSMS.setOnClickListener(this);
            btnDelApp  = itemView.findViewById(R.id.imgBtnDeleteApp);
            btnDelApp.setOnClickListener(this);
        }

        public void bind(Appointment appointment) {
            currentAppointment = appointment;
            txtClientName.setText(currentAppointment.getClientName());
            txtAppointmentStart.setText(MethodsForApp.formatDateWithHourToString(currentAppointment.getAppointmentStart()));
            if(currentAppointment.isSMSSent()){
                btnSentSMS.setImageResource(R.drawable.ic_sent_sms);
                btnSentSMS.setEnabled(false);
            }
            else{
                btnSentSMS.setImageResource(R.drawable.ic_menu_send);
                btnSentSMS.setEnabled(true);
            }
        }

        @Override
        public void onClick(View v) {
            AppointmentDBHelper appointmentDB = new AppointmentDBHelper(context,userId,true);
            switch (v.getId()){
                case R.id.img_btn_sms:

//                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
//                        try {

                            String smsText =getStrSMSTemplate().replace("DATE",txtAppointmentStart.getText().toString().substring(6) );
                            smsText = smsText.replace("TIME", txtAppointmentStart.getText().toString().substring(0,5));
                            smsText = smsText.replace("NAME", currentAppointment.getClientName());

                   // Toast.makeText(context, smsText, Toast.LENGTH_SHORT).show();

//                            SmsManager smsManager = SmsManager.getDefault();
//                            smsManager.sendTextMessage(currentAppointment.getClientPhonNumber(), null, smsText, null, null);

                            currentAppointment.setSMSSent(true);
                            appointmentDB.updateAppointment(currentAppointment);

                           @SuppressLint("ResourceType") Animator set = AnimatorInflater.loadAnimator(context, R.anim.flip);
                            set.setTarget(btnSentSMS);
                            set.start();

                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    btnSentSMS.setImageResource(R.drawable.ic_sent_sms);
                                    btnSentSMS.setEnabled(false);
                                }
                            }, 400);

//                        } catch (Exception ex) {
//                            Toast.makeText(context, ex.getMessage().toString(),
//                                    Toast.LENGTH_LONG).show();
//                            ex.printStackTrace();
//                        }
//                    }
//                    else Toast.makeText(context,"check Permission",Toast.LENGTH_LONG).show();

                    Intent it = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"+ currentAppointment.getClientPhonNumber()));
                    it.putExtra("sms_body", smsText);
                    context.startActivity(it);

                    break;
                case R.id.imgBtnDeleteApp:
                    position = getAdapterPosition();
                    del(currentAppointment);
                    appointmentDB.deleteAppointmentById(currentAppointment.getAppointmentId());
                    Snackbar snackbar = Snackbar.make(v,MethodsForApp.mskeMessageForSnack(context.getString(R.string.appointment_deleted)),
                            Snackbar.LENGTH_LONG).
                            setAction(context.getString(R.string.undo), new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    add(currentAppointment);
                                    appointmentDB.addUpdateAppointment(currentAppointment);
                                }
                            });
                    snackbar.show();
                    break;
            }
        }

    }
}
