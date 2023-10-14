package com.olgag.mngclients.controler;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.olgag.mngclients.R;
import com.olgag.mngclients.activities.AddUpdateClient;
import com.olgag.mngclients.db.ClientDBHelper;
import com.olgag.mngclients.methods.MethodsForApp;
import com.olgag.mngclients.model.Client;

import java.util.ArrayList;
import java.util.Locale;

public class ClientAdapter  extends RecyclerView.Adapter<ClientAdapter.ClientHolder>   {
    private Context context;
    private String userId;
    private ArrayList<Client> arrClienttList = new ArrayList<>();
    private ArrayList<Client> clientNamesList = new ArrayList<>();
    private int position;

    public ClientAdapter(Context context, String userId) {
        this.context = context;
        this.userId = userId;
    }

    public void add(Client client){
        this.arrClienttList.add(client);
        this.clientNamesList.add(client);
        notifyItemInserted(arrClienttList.size()-1);
    }

    public void addAll(ArrayList<Client> clients){

        this.arrClienttList.addAll(clients);
        this.clientNamesList.addAll(clients);
    }

    public void del(Client client){
        this.arrClienttList.remove(client);
        this.clientNamesList.remove(client);
        notifyItemRemoved(position);
    }

    public void delAll(){
        int size = arrClienttList.size();
        this.arrClienttList.clear();
        this.clientNamesList.clear();
        notifyItemRangeRemoved(0, size);
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        arrClienttList.clear();
        if (charText.length() == 0) {
            arrClienttList.addAll(clientNamesList);
        } else {
            for (Client cl : clientNamesList) {
                if (cl.getClientName().toLowerCase(Locale.getDefault()).contains(charText)
                        || cl.getClientPhonNumber1().toLowerCase(Locale.getDefault()).contains(charText)
                        || cl.getClientPhonNumber2().toLowerCase(Locale.getDefault()).contains(charText)) {
                    arrClienttList.add(cl);
                }
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ClientHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ClientAdapter.ClientHolder(LayoutInflater.from(context).inflate(R.layout.client_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ClientHolder holder, int position) {
        holder.bind(arrClienttList.get(position));
    }

    @Override
    public int getItemCount() {
        return arrClienttList.size();
    }


    //------------------------------ HOLDER --------------------------------------

    public class ClientHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {

        private LinearLayout layoutClientRow, layoutPhone;
        private TextView rowClientName, rowClientPhone,rowClientPhone2, rowClientAddress, rowClientLastOrder;
        private FloatingActionButton btnDeleteClient;
        private Client curentClient;
        private ImageButton btnOpenWaze, btnMakeCall, btnMakeCall2;


        public ClientHolder(@NonNull View itemView) {
            super(itemView);

            this.layoutClientRow = itemView.findViewById(R.id.layoutClientRow);
            layoutClientRow.setOnClickListener(this);
            this.layoutPhone = itemView.findViewById(R.id.layout_phone);
            this.rowClientName = itemView.findViewById(R.id.row_client_name);
            this.rowClientPhone = itemView.findViewById(R.id.row_client_phone);
            this.rowClientPhone2 = itemView.findViewById(R.id.row_client_phone2);
            this.btnMakeCall = itemView.findViewById(R.id.img_btn_call);
            this.btnMakeCall2 = itemView.findViewById(R.id.img_btn_call2);
            btnMakeCall.setOnClickListener(this);
            btnMakeCall2.setOnClickListener(this);
            rowClientPhone.setOnClickListener(this);
            rowClientPhone2.setOnClickListener(this);

            this.rowClientAddress = itemView.findViewById(R.id.row_client_address);
            this.rowClientLastOrder  = itemView.findViewById(R.id.row_client_last_order);
            this.btnOpenWaze = itemView.findViewById(R.id.img_btn_go);
            btnOpenWaze.setOnClickListener(this);
            this.btnDeleteClient = itemView.findViewById(R.id.img_btn_delete_client);
            btnDeleteClient.setOnClickListener(this);
        }

        public void bind(Client client) {
            curentClient = client;
            rowClientName.setText(curentClient.getClientName());
            rowClientPhone.setText(curentClient.getClientPhonNumber1());
            if(curentClient.getClientPhonNumber2().length()>0) {
                rowClientPhone2.setText(curentClient.getClientPhonNumber2());
                btnMakeCall2.setVisibility(View.VISIBLE);
                layoutPhone.setGravity( Gravity.START | Gravity.CENTER);

            }
            rowClientAddress.setText(curentClient.getClientAddress());
            rowClientLastOrder.setText(MethodsForApp.formatDateToString(curentClient.getLatestOrder())  + "");
            if(curentClient.getClientAddress().length()<1) {
                itemView.findViewById(R.id.materialCardViewGo).setVisibility(View.GONE);
                rowClientAddress.setVisibility(View.GONE);
            }
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.img_btn_delete_client:
                    ClientDBHelper clienttDB = new ClientDBHelper(context, userId);
                    position = getAdapterPosition();
                    del(curentClient);
                    clienttDB.setFordelCurrentClient(curentClient.getClientId());
                    Snackbar snackbar = Snackbar.make(layoutClientRow, MethodsForApp.mskeMessageForSnack(context.getString(R.string.client_deleted)),
                            Snackbar.LENGTH_LONG)
                         .setAction(context.getString(R.string.undo), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                           add(curentClient);
                           clienttDB.restoreClintWithHisOrders(curentClient);
                        }
                    });
                    snackbar.show();
                    break;
                case R.id.layoutClientRow:
                 //   Toast.makeText(context, "update" + curentClient.toString(), Toast.LENGTH_SHORT).show();
                    Intent intent;
                    intent = new Intent(context, AddUpdateClient.class);
                    intent.putExtra("client",curentClient);
                    intent.putExtra("user", userId);
                    context.startActivity(intent);
                    break;
                case R.id.img_btn_call:
                case R.id.row_client_phone:
                    makeCall(curentClient.getClientPhonNumber1());
                     break;
                case R.id.img_btn_call2:
                case R.id.row_client_phone2:
                    makeCall(curentClient.getClientPhonNumber2());
                     break;
                case R.id.img_btn_go:
                    try
                    {
                        String url = "https://waze.com/ul?q=" + curentClient.getClientAddress();
                        Intent navigateIntent = new Intent( Intent.ACTION_VIEW, Uri.parse( url ) );
                        context.startActivity( navigateIntent );
                    }
                    catch ( ActivityNotFoundException ex  )
                    {
                        // If Waze is not installed, open it in Google Play:
                        Intent navigateIntent = new Intent( Intent.ACTION_VIEW, Uri.parse( "market://details?id=com.waze" ) );
                        context.startActivity(navigateIntent);
                    }


//                    Intent webViewOpen = new Intent(context, WebViewActivity.class);
//                    webViewOpen.putExtra("clientAddress",curentClient.getClientAddress());
//                    context.startActivity(webViewOpen);
//                    try
//                    {
//                        String url = "https://waze.com/ul?navigate=yes&q=" + curentClient.getClientAddress();
//                        Intent navigateIntent = new Intent( Intent.ACTION_VIEW, Uri.parse( url ) );
//                        context.startActivity( navigateIntent );
//                    }
//                    catch ( ActivityNotFoundException ex  )
//                    {
//                        Intent navigateIntent = new Intent( Intent.ACTION_VIEW, Uri.parse( "market://details?id=com.waze" ) );
//                        context.startActivity(navigateIntent);
//                    }
                    break;
            }
        }

        private void makeCall(String clientPhonNumber) {
            Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
            phoneIntent.setData(Uri.parse("tel:"+clientPhonNumber));
            context.startActivity(phoneIntent);
        }
    }
}
