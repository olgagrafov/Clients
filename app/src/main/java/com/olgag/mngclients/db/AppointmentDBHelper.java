package com.olgag.mngclients.db;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.olgag.mngclients.R;
import com.olgag.mngclients.controler.Alert;
import com.olgag.mngclients.controler.AppointmentAdapter;
import com.olgag.mngclients.model.Appointment;
import com.olgag.mngclients.model.Client;

import java.util.ArrayList;

public class AppointmentDBHelper {
    private AppointmentAdapter appointmentAdapter;
    private RecyclerView recAppList;
    private Context context;
    private FirebaseFirestore dbFB;
    private String userId;
    private String pathTblAppointment;
    private ArrayList<Long> arrAppointmentStart = new ArrayList<>();
    private ArrayList<Appointment> arrAppointmentsList= new ArrayList<>();
    private OnSetArrAppointmentStart listener;

    public AppointmentDBHelper(Context context, String userId) {
        this.context = context;
        this.userId = userId;
        this.dbFB = FirebaseFirestore.getInstance();
        this.pathTblAppointment = context.getString(R.string.tbl_users) + userId  + context.getString(R.string.tbl_appointment);
        this.listener = (OnSetArrAppointmentStart)context;
    }

    public AppointmentDBHelper(Context context, String userId, boolean withoutListener) {
        this.context = context;
        this.userId = userId;
        this.dbFB = FirebaseFirestore.getInstance();
        this.pathTblAppointment = context.getString(R.string.tbl_users) + userId  + context.getString(R.string.tbl_appointment);
    }

    public AppointmentDBHelper(Context context, String userId,
                               AppointmentAdapter appointmentAdapter, RecyclerView recAppList) {
        this.dbFB =  FirebaseFirestore.getInstance();
        this.userId = userId;
        this.context = context;
        this.appointmentAdapter = appointmentAdapter;
        this.recAppList = recAppList;
        this.pathTblAppointment = context.getString(R.string.tbl_users) + userId  + context.getString(R.string.tbl_appointment);
    }

    public interface OnSetArrAppointmentStart{

        void setArrAppointmentTime(ArrayList<Long> arrAppointmentStart);
        void getAppointmentDB(Appointment ap);
    }

    public void updateAppointment(Appointment appointment) {
        dbFB.collection(pathTblAppointment)
                .document(appointment.getAppointmentId())
                .set(appointment, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //  new Alert(context, context.getString(R.string.title_success_added), Gravity.BOTTOM, Toast.LENGTH_SHORT);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        new Alert(context, context.getString(R.string.document_added_error) + " " + e.getCause(), Gravity.CENTER, Toast.LENGTH_LONG , ContextCompat.getColor(context, R.color.error_color));
                    }
                });
    }

    public void addUpdateAppointment(Appointment appointment) {
        String pathTblClient =  context.getString(R.string.tbl_users) + userId  + context.getString(R.string.tbl_clients);
        dbFB.collection(pathTblClient)
                .document(appointment.getClientId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Client cl = documentSnapshot.toObject(Client.class);
                        if(cl !=null) {
                            appointment.setClientName(cl.getClientName());
                            appointment.setClientPhonNumber(cl.getClientPhonNumber1());
                            updateAppointment(appointment);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        new Alert(context, e.toString(), Gravity.CENTER, Toast.LENGTH_LONG, ContextCompat.getColor(context, R.color.error_color));
                    }
                }) ;

    }

    public void getAppointmentById(String idAppointment){
        dbFB.collection(pathTblAppointment)
                .document(idAppointment)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Appointment ap = documentSnapshot.toObject(Appointment.class);
                        if(ap !=null)  listener.getAppointmentDB(ap);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        new Alert(context, e.toString(), Gravity.CENTER, Toast.LENGTH_LONG, ContextCompat.getColor(context, R.color.error_color));
                    }
                }) ;
    }
    public void getAllAppointments(long startFromNow) {
        arrAppointmentStart.clear();
        dbFB.collection(pathTblAppointment)
                .orderBy("appointmentStart" , Query.Direction.ASCENDING)
                .whereGreaterThan("appointmentStart", startFromNow)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                              //  arrAppointmentStart.add(MethodsForApp.formatDateWithHourToStringForArray(document.getLong("appointmentStart")));
                                arrAppointmentStart.add(document.getLong("appointmentStart"));

                                // new Alert(context, document.getLong("appointmentStart") + "", Gravity.CENTER, Toast.LENGTH_LONG, ContextCompat.getColor(context, R.color.error_color));

                            }
                          //   new Alert(context, arrAppointmentStart.size() + "", Gravity.CENTER, Toast.LENGTH_LONG, ContextCompat.getColor(context, R.color.error_color));

                            listener.setArrAppointmentTime(arrAppointmentStart);

                        } else {
                            new Alert(context, "Error getting documents" + task.getException(), Gravity.CENTER, Toast.LENGTH_LONG, ContextCompat.getColor(context, R.color.error_color));
                        }
                    }
                });
    }

    public void getAllAppointmentsList(long startFromNow) {
        arrAppointmentsList.clear();
        dbFB.collection(pathTblAppointment)
                .orderBy("appointmentStart" , Query.Direction.ASCENDING)
                .whereGreaterThan("appointmentStart", startFromNow)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //  arrAppointmentStart.add(MethodsForApp.formatDateWithHourToStringForArray(document.getLong("appointmentStart")));
                                arrAppointmentsList.add(document.toObject(Appointment.class));

                                // new Alert(context, document.getLong("appointmentStart") + "", Gravity.CENTER, Toast.LENGTH_LONG, ContextCompat.getColor(context, R.color.error_color));

                            }
                            //   new Alert(context, arrAppointmentStart.size() + "", Gravity.CENTER, Toast.LENGTH_LONG, ContextCompat.getColor(context, R.color.error_color));


                            appointmentAdapter.delAll();
                            recAppList.setAdapter(appointmentAdapter);
                            appointmentAdapter.addAll(arrAppointmentsList);

                        } else {
                            new Alert(context, "Error getting documents" + task.getException(), Gravity.CENTER, Toast.LENGTH_LONG, ContextCompat.getColor(context, R.color.error_color));
                        }
                    }
                });
    }


    public void deleteAppointmentById(String idAppointment){
        dbFB.collection(pathTblAppointment)
                .document(idAppointment)
                .delete()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        new Alert(context,e.toString(), Gravity.CENTER, Toast.LENGTH_LONG, ContextCompat.getColor(context, R.color.error_color));
                    }
                });
    }
}
