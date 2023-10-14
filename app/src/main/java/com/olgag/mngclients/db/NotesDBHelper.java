package com.olgag.mngclients.db;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.olgag.mngclients.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.olgag.mngclients.controler.Alert;

import java.util.HashMap;
import java.util.Map;

public class NotesDBHelper {
    private Context context;
    private FirebaseFirestore dbFB;
    private String userId;
    private String pathTblNotes;
    private OnGetDateSuccess listener;
    private OnGetSMSTemplateSuccess smsListener;

    public NotesDBHelper(){}

    public NotesDBHelper(Context context, String userId) {
        this.context = context;
        this.userId = userId;
        this.dbFB = FirebaseFirestore.getInstance();
        this.listener = (OnGetDateSuccess) context;
        this.pathTblNotes = context.getString(R.string.tbl_users) + userId  + context.getString(R.string.tbl_notes);
    }

    public NotesDBHelper(Context context, String userId, boolean isSMSTempate) {
        this.context = context;
        this.userId = userId;
        this.dbFB = FirebaseFirestore.getInstance();
        if(isSMSTempate)
            this.smsListener = (OnGetSMSTemplateSuccess) context;
        this.pathTblNotes = context.getString(R.string.tbl_users) + userId  + context.getString(R.string.tbl_notes);
    }


    public interface OnGetDateSuccess{
        void setTitle(String title);
        void setNote(String note);
    }
    public interface OnGetSMSTemplateSuccess{
        void setSMSTemplate(String smsTemplate);
    }

    public void addUpdateNewTitle(String strTitle) {
        Map<String, Object>  title = new HashMap<>();
        title.put("title", strTitle);
        dbFB.collection(pathTblNotes)
                .document("Title")
                .set(title, SetOptions.merge())
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

    public void getTitle(){
        dbFB.collection(pathTblNotes)
                .document("Title")
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                            listener.setTitle(documentSnapshot.getString("title"));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        new Alert(context, e.toString(), Gravity.CENTER, Toast.LENGTH_LONG, ContextCompat.getColor(context, R.color.error_color));
                    }
                }) ;
    }

    public void addUpdateNewFooter(String strFooter) {
        Map<String, Object>  footer = new HashMap<>();
        footer.put("footer", strFooter);
        dbFB.collection(pathTblNotes)
                .document("Footer")
                .set(footer, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                   //     new Alert(context, context.getString(R.string.footer_success_added), Gravity.BOTTOM, Toast.LENGTH_SHORT);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        new Alert(context, context.getString(R.string.document_added_error) + " " + e.getCause(), Gravity.CENTER, Toast.LENGTH_LONG , ContextCompat.getColor(context, R.color.error_color));
                    }
                });
    }
    public void getFooter(){
        dbFB.collection(pathTblNotes)
                .document("Footer")
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        listener.setNote(documentSnapshot.getString("footer"));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        new Alert(context, e.toString(), Gravity.CENTER, Toast.LENGTH_LONG, ContextCompat.getColor(context, R.color.error_color));
                    }
                }) ;
    }

    public void addUpdateSMSTemplate(String strSMSTemplate) {
        Map<String, Object>  footer = new HashMap<>();
        footer.put("smstemplate", strSMSTemplate);
        dbFB.collection(pathTblNotes)
                .document("SMSTemplate")
                .set(footer, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //     new Alert(context, context.getString(R.string.footer_success_added), Gravity.BOTTOM, Toast.LENGTH_SHORT);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        new Alert(context, context.getString(R.string.document_added_error) + " " + e.getCause(), Gravity.CENTER, Toast.LENGTH_LONG , ContextCompat.getColor(context, R.color.error_color));
                    }
                });
    }
    public void getSMSTemplateFromDB(){
        dbFB.collection(pathTblNotes)
                .document("SMSTemplate")
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                     smsListener.setSMSTemplate(documentSnapshot.getString("smstemplate"));

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
