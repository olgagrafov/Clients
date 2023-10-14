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
import com.olgag.mngclients.controler.ClientAdapter;
import com.olgag.mngclients.model.Appointment;
import com.olgag.mngclients.model.Client;
import com.olgag.mngclients.model.Order;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class ClientDBHelper {
    private Context context;
    private FirebaseFirestore dbFB;
    private String userId;
    private ArrayList<Client> arrClients = new ArrayList<Client>();
    private ArrayList<Order> orders = new ArrayList<>();
    private HashSet<Appointment> appointments = new HashSet<>();
    private ClientAdapter clientAdapter;
    private RecyclerView recClienttList;
    private String pathTblClient;
    private OnGetClientByIdSuccess listener;

    public ClientDBHelper(Context context,  String userId) {
        this.dbFB = FirebaseFirestore.getInstance();
        this.context = context;
        this.userId = userId;
        this.pathTblClient =context.getString(R.string.tbl_users) + userId  + context.getString(R.string.tbl_clients);
    }

    public ClientDBHelper(Context context,  String userId, Boolean isSetListener) {
        this.dbFB = FirebaseFirestore.getInstance();
        this.context = context;
        this.userId = userId;
        this.pathTblClient =context.getString(R.string.tbl_users) + userId  + context.getString(R.string.tbl_clients);
        if(isSetListener)
            this.listener = (OnGetClientByIdSuccess) context;
    }

    public ClientDBHelper(String userId, Context context, ClientAdapter clientAdapter, RecyclerView recClienttList) {
        this.dbFB =  FirebaseFirestore.getInstance();
        this.userId = userId;
        this.context = context;
        this.clientAdapter = clientAdapter;
        this.recClienttList = recClienttList;
        this.pathTblClient =context.getString(R.string.tbl_users) + userId  + context.getString(R.string.tbl_clients);
    }

    public interface OnGetClientByIdSuccess{
        void setCurrentClient(Client client);
    }
    public void getAllClients() {
        arrClients.clear();
        dbFB.collection(pathTblClient)
                .orderBy("latestOrder" , Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                arrClients.add(document.toObject(Client.class));
                            }
                              recClienttList.setAdapter(clientAdapter);
                              clientAdapter.delAll();
                              clientAdapter.addAll(arrClients);

                        } else {
                            new Alert(context, "Error getting documents" + task.getException(), Gravity.CENTER, Toast.LENGTH_LONG, ContextCompat.getColor(context, R.color.error_color));
                        }
                    }
                });
    }

    public void getAllClientsWithOpenOrders() {
        arrClients.clear();
        dbFB.collection(pathTblClient)
                .whereEqualTo("someOrderOpen", true)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                arrClients.add(document.toObject(Client.class));
                            }
                            Collections.sort(arrClients, Client.DateCreated);
                            recClienttList.setAdapter(clientAdapter);
                            clientAdapter.delAll();
                            clientAdapter.addAll(arrClients);
                        } else {
                            new Alert(context, "Error getting documents" + task.getException(), Gravity.CENTER, Toast.LENGTH_LONG, ContextCompat.getColor(context, R.color.error_color));
                        }
                    }
                });
    }
    public void getAllClientsWithRequstedBid() {
        arrClients.clear();
        dbFB.collection(pathTblClient)
                .whereEqualTo("someBidRequest", true)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                arrClients.add(document.toObject(Client.class));
                            }
                            Collections.sort(arrClients, Client.DateCreated);
                            recClienttList.setAdapter(clientAdapter);
                            clientAdapter.delAll();
                            clientAdapter.addAll(arrClients);
                        } else {
                            new Alert(context, "Error getting documents" + task.getException(), Gravity.CENTER, Toast.LENGTH_LONG, ContextCompat.getColor(context, R.color.error_color));
                        }
                    }
                });
    }
    public void addNewClient(Client client, String clientUniqueID, String strMessage){
        dbFB.collection(pathTblClient)
                .document(clientUniqueID)
                .set(client, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    //  new Alert(context, strMessage, Gravity.BOTTOM, Toast.LENGTH_SHORT);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        new Alert(context,context.getString(R.string.document_added_error) + e.getCause(), Gravity.CENTER, Toast.LENGTH_LONG, ContextCompat.getColor(context, R.color.error_color));
                    }
                });

    }
    public void addNewClient(Client client){

        dbFB.collection(pathTblClient)
                .document(client.getClientId())
                .set(client, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) { }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        new Alert(context,context.getString(R.string.document_added_error) + e.getCause(), Gravity.CENTER, Toast.LENGTH_LONG, ContextCompat.getColor(context, R.color.error_color));
                    }
                });

    }

    public void updateClientClosOrdRequestBid(String clientID,boolean isClosed,  boolean isrequestBid){
        dbFB.collection(pathTblClient)
                .document(clientID)
        .update(
                "someOrderOpen", isClosed,
                     "someBidRequest", isrequestBid
    );
}

    public void setFordelCurrentClient(String  clientId){
        OrderDBHelper orderDB = new OrderDBHelper(context, userId);
        dbFB.collection(context.getString(R.string.tbl_users) + userId  + context.getString(R.string.tbl_orders))
                .whereEqualTo("clientID", clientId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // new Alert(context, task.getResult().size()+"", Gravity.BOTTOM, Toast.LENGTH_LONG);
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                orders.add(document.toObject(Order.class));
                                orderDB.delCurrentOrderByID(document.toObject(Order.class).getIdOrder());
                            }
                            delCurentClient(clientId,orders );
                            setAappointmentsForRestor();
                        } else {
                            new Alert(context, "Error getting documents" + task.getException(), Gravity.CENTER, Toast.LENGTH_LONG, ContextCompat.getColor(context, R.color.error_color));
                        }
                    }
                });
    }

    public void delCurentClient(String currentClientID, ArrayList<Order> orders){
        dbFB.collection(pathTblClient)
                .document(currentClientID)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {   }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        new Alert(context,e.toString(), Gravity.CENTER, Toast.LENGTH_LONG, ContextCompat.getColor(context, R.color.error_color));
                    }
           });
    }
    private void setAappointmentsForRestor(){
        for (Order or: orders) {
            getAppointmentById(or.getIdOrder());
        }


    }

    private void getAppointmentById(String idAppointment){
            String pathTblAppointment = context.getString(R.string.tbl_users) + userId  + context.getString(R.string.tbl_appointment);
            dbFB.collection(pathTblAppointment)
                    .document(idAppointment)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {

                            Appointment ap = documentSnapshot.toObject(Appointment.class);
                            if(ap !=null)
                              appointments.add(documentSnapshot.toObject(Appointment.class));

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            new Alert(context, e.toString(), Gravity.CENTER, Toast.LENGTH_LONG, ContextCompat.getColor(context, R.color.error_color));
                        }
                    }) ;
        }


    public void restoreClintWithHisOrders(Client currentClien){
        OrderDBHelper orderDB = new OrderDBHelper(context, userId);
        dbFB.collection(pathTblClient)
                .document(currentClien.getClientId())
                .set(currentClien, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        for (Order ord: orders ) {
                            orderDB.addUpdateNewOrder(ord);
                        }
                        restoreClientsAppointment();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        new Alert(context,context.getString(R.string.document_added_error) + e.getCause(), Gravity.CENTER, Toast.LENGTH_LONG, ContextCompat.getColor(context, R.color.error_color));
                    }
                });
    }
    private void restoreClientsAppointment(){

      AppointmentDBHelper  appointmentDB= new AppointmentDBHelper(context, userId, true);
      for(Appointment ap: appointments){
           appointmentDB.addUpdateAppointment(ap);
     //    Toast.makeText(context, appointments.size() + "", Toast.LENGTH_LONG).show();
      }
    }

    public void getClientById(String clientID){
       // new Alert(context, clientID, Gravity.BOTTOM, Toast.LENGTH_LONG);
        dbFB.collection(pathTblClient)
                .document(clientID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                   @Override
                   public void onSuccess(DocumentSnapshot documentSnapshot) {
                       Client cl = documentSnapshot.toObject(Client.class);
                       if(cl !=null) listener.setCurrentClient(cl);
                        //   new Alert(context, cl.toString(), Gravity.BOTTOM, Toast.LENGTH_LONG);
                   }
               })
               .addOnFailureListener(new OnFailureListener() {
                   @Override
                   public void onFailure(@NonNull Exception e) {
                       new Alert(context, e.toString(), Gravity.CENTER, Toast.LENGTH_LONG, ContextCompat.getColor(context, R.color.error_color));
                   }
               }) ;

    }

}
