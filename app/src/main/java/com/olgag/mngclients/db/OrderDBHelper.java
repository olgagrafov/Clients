package com.olgag.mngclients.db;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.olgag.mngclients.R;
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
import com.olgag.mngclients.controler.Alert;
import com.olgag.mngclients.model.Appointment;
import com.olgag.mngclients.model.Order;

import java.util.ArrayList;

public class OrderDBHelper {
    private Context context;
    private FirebaseFirestore dbFB;
    private String userId;
    private String pathTblOrder;
    private OnGetAllClientsOrderSuccess listener;
    private  Appointment ap;

    public OrderDBHelper(){  }

    public OrderDBHelper(Context context, String userId) {
        this.context = context;
        this.dbFB = FirebaseFirestore.getInstance();;
        this.userId = userId;
        this.listener = (OnGetAllClientsOrderSuccess) context;
        this.pathTblOrder = context.getString(R.string.tbl_users) + userId  + context.getString(R.string.tbl_orders);
    }

    public interface OnGetAllClientsOrderSuccess{
        void setClientOrdersAdapter(ArrayList<Order> orders);
    }

    public void getAllClientsOpenOrders() {
        dbFB.collection(pathTblOrder)
                .whereEqualTo("closed", false)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                           // new Alert(context, task.getResult().size() + "", Gravity.CENTER, Toast.LENGTH_LONG);
                            ArrayList<Order> arrClientsOrder = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                arrClientsOrder.add(document.toObject(Order.class));
                              }
                         listener.setClientOrdersAdapter(arrClientsOrder);
                        } else {
                            new Alert(context, "Error getting documents" + task.getException(), Gravity.CENTER, Toast.LENGTH_LONG, ContextCompat.getColor(context, R.color.error_color));
                        }
                    }
                });
    }

    public void getAllClientsRequestBids() {
        dbFB.collection(pathTblOrder)
                .whereEqualTo("requestBid", true)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // new Alert(context, task.getResult().size() + "", Gravity.CENTER, Toast.LENGTH_LONG);
                            ArrayList<Order> arrClientsOrder = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                arrClientsOrder.add(document.toObject(Order.class));
                            }
                            listener.setClientOrdersAdapter(arrClientsOrder);
                        } else {
                            new Alert(context, "Error getting documents" + task.getException(), Gravity.CENTER, Toast.LENGTH_LONG, ContextCompat.getColor(context, R.color.error_color));
                        }
                    }
                });
    }

    public void getAllClientOrders(String clientId){
        ArrayList<Order> orders = new ArrayList<>();
        dbFB.collection(pathTblOrder)
               .whereEqualTo("clientID", clientId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                           // new Alert(context, task.getResult().size()+"", Gravity.BOTTOM, Toast.LENGTH_LONG);
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                orders.add(document.toObject(Order.class));
                            }
                            listener.setClientOrdersAdapter(orders);
                        } else {
                            new Alert(context, "Error getting documents" + task.getException(), Gravity.CENTER, Toast.LENGTH_LONG, ContextCompat.getColor(context, R.color.error_color));
                        }
                    }
                });

    }

    public void getAllOrders() {
        ArrayList<Order> orders = new ArrayList<>();
        dbFB.collection(pathTblOrder)
                .orderBy("ordDateCreated", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                orders.add(document.toObject(Order.class));
                            }
                            listener.setClientOrdersAdapter(orders);

                        } else {
                            new Alert(context, "Error getting documents" + task.getException(), Gravity.CENTER, Toast.LENGTH_LONG, ContextCompat.getColor(context, R.color.error_color));
                        }
                    }
                });
    }

    public void getOrderById(String orderId){
        dbFB.collection(pathTblOrder)
                .document(orderId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Order order = documentSnapshot.toObject(Order.class);
//                        if(product !=null)
//                            listener.openProductForUpdate(product);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        new Alert(context, e.toString(), Gravity.CENTER, Toast.LENGTH_LONG, ContextCompat.getColor(context, R.color.error_color));
                    }
                }) ;
    }

    public void addUpdateNewOrder(Order newOrder, String strSuccess) {
        dbFB.collection(pathTblOrder)
                .document(newOrder.getIdOrder())
                .set(newOrder, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                       // new Alert(context, strSuccess, Gravity.BOTTOM, Toast.LENGTH_SHORT);
                        setClientsChanges(newOrder.getClientID());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        new Alert(context, context.getString(R.string.document_added_error) + " " + e.getCause(), Gravity.CENTER, Toast.LENGTH_LONG , ContextCompat.getColor(context, R.color.error_color));
                    }
                });
    }
  private void setClientsChanges(String clientId){
      ClientDBHelper clientDb = new ClientDBHelper(context,userId);
      ArrayList<Order> orders = new ArrayList<>();
      dbFB.collection(pathTblOrder)
             .whereEqualTo("clientID", clientId)
              .get()
              .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                  @Override
                  public void onComplete(@NonNull Task<QuerySnapshot> task) {
                      if (task.isSuccessful()) {
                          for (QueryDocumentSnapshot document : task.getResult()) {
                              orders.add(document.toObject(Order.class));
                          }
                          boolean isClosed=true, isrequestBid=false;
                          for (Order ord: orders ) {
                              if (ord.isClosed()) {
                                  isClosed = false;
                                  break;
                              }
                          }
                          for (Order ord: orders ) {
                              if (ord.isRequestBid()) {
                                  isrequestBid = true;
                                  break;
                              }
                          }
                          clientDb.updateClientClosOrdRequestBid(clientId, isClosed, isrequestBid);
                      } else {
                          new Alert(context, "Error getting documents" + task.getException(), Gravity.CENTER, Toast.LENGTH_LONG, ContextCompat.getColor(context, R.color.error_color));
                      }
                  }
              });
  }

    public void addUpdateNewOrder(Order newOrder) {
        dbFB.collection(pathTblOrder)
                .document(newOrder.getIdOrder())
                .set(newOrder, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if(ap!=null) {
                            AppointmentDBHelper appointmentDB = new AppointmentDBHelper(context, userId, true);
                            appointmentDB.addUpdateAppointment(ap);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        new Alert(context, context.getString(R.string.document_added_error) + " " + e.getCause(), Gravity.CENTER, Toast.LENGTH_LONG , ContextCompat.getColor(context, R.color.error_color));
                    }
                });
    }

    public void delCurrentOrderByID(String idOrder){

        dbFB.collection(pathTblOrder)
                .document(idOrder)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        setAppointmentForRestore(idOrder);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        new Alert(context, context.getString(R.string.document_deleted_error) + " "  + e.getCause(), Gravity.CENTER, Toast.LENGTH_LONG, ContextCompat.getColor(context, R.color.error_color));
                    }
                });

    }

    public void setAppointmentForRestore(String idAppointment){
       String pathTblAppointment = context.getString(R.string.tbl_users) + userId  + context.getString(R.string.tbl_appointment);
        dbFB.collection(pathTblAppointment)
                .document(idAppointment)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        ap = documentSnapshot.toObject(Appointment.class);
                        AppointmentDBHelper appointmentDB = new AppointmentDBHelper(context, userId, true);
                        appointmentDB.deleteAppointmentById(idAppointment);
                      //  if(ap !=null) new Alert(context, ap.toString(), Gravity.CENTER, Toast.LENGTH_LONG, ContextCompat.getColor(context, R.color.error_color));

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
