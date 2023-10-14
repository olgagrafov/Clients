package com.olgag.mngclients.controler;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.olgag.mngclients.R;

public class Alert {
    private Context context;
    private Toast myToast;
    private TextView txtToast;
    private View toastLayout;
    private String msgAlert;
    private int myGravity, myDuration;

    public Alert(Context context, String msgAlert, int toastGravity, int toastDuration) {
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        toastLayout = inflater.inflate(R.layout.custom_toast,
                ((Activity) context).findViewById(R.id.custom_toast_container));
        txtToast =  toastLayout.findViewById(R.id.txtToast);
        myToast = new Toast(context);
        this.msgAlert = msgAlert;
        this.myGravity = toastGravity;
        this.myDuration = toastDuration;
        toastShow();
    }

    public Alert(Context context, String msgAlert, int toastGravity, int toastDuration, int msgColor) {
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        toastLayout = inflater.inflate(R.layout.custom_toast,
                ((Activity) context).findViewById(R.id.custom_toast_container));
        txtToast =  toastLayout.findViewById(R.id.txtToast);
        txtToast.setTextColor(msgColor);
        myToast = new Toast(context);
        this.msgAlert = msgAlert;
        this.myGravity = toastGravity;
        this.myDuration = toastDuration;
        toastShow();
    }

    private void toastShow(){
        txtToast.setText(msgAlert);
        myToast.setGravity(myGravity, 0, 260);
        myToast.setDuration(myDuration);
        myToast.setView(toastLayout);
        myToast.show();
    }
}
