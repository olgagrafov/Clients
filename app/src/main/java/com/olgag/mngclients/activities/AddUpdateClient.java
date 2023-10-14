package com.olgag.mngclients.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;
import com.olgag.mngclients.R;
import com.olgag.mngclients.controler.Alert;
import com.olgag.mngclients.controler.OrderAdapter;
import com.olgag.mngclients.db.ClientDBHelper;
import com.olgag.mngclients.db.OrderDBHelper;
import com.olgag.mngclients.methods.MethodsForApp;
import com.olgag.mngclients.model.Client;
import com.olgag.mngclients.model.Order;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.UUID;

public class AddUpdateClient extends AppCompatActivity implements View.OnClickListener, MethodsForApp,
        OrderAdapter.OnOrderClickListener, OrderDBHelper.OnGetAllClientsOrderSuccess {
    private ImageButton btnGoBack, imgSaveClient;
    private Button btnSaveClient;
    private TextView linkAddClientNote, linkAddPhoneNumber2, linkAddOrder, lblAddProduct;
    private TextInputLayout lblClientDescription, lblClientPhoneNumber2;
    private EditText editClientName, editClientAddress, editClientDescription,
            editClientPhonNumber1, editClientPhonNumber2;
    private String userId;
    private OrderAdapter orderAdapter;
    private Client curentClient;
    private OrderDBHelper ordDBhelper;
    private RecyclerView recOrderList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_client);

        findAllViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.link_add_client_note:
                showView(lblClientDescription, linkAddClientNote);
                editClientDescription.requestFocus();
                break;
            case R.id.link_add_phone_number2:
                showView(lblClientPhoneNumber2, linkAddPhoneNumber2);
                editClientPhonNumber2.requestFocus();
                break;
            case R.id.link_add_order:
                    openOrderForUpdate(null, -1, false);
                break;
            case R.id.imgBtnGoBack:
                MethodsForApp.closeKeyboard(this,editClientName);
                goToMainActivity();
                break;
            case R.id.imgBtnSaveClient:
            case R.id.btn_save_client:
                MethodsForApp.closeKeyboard(this,editClientName);
                if(inputsIsGood() && userId != null) {
                    setCurentClient();
                    goToMainActivity();
                    }
                break;
            default:
                break;
        }
    }

    @Override
    public void openOrderForUpdate(Order curentOrder, int position, boolean isOrderForUpdate) {
        if(inputsIsGood() && userId != null) {
            setCurentClient();
            Intent intent;
            intent = new Intent(this, AddUpdateOrder.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("user", userId);
            intent.putExtra("client", curentClient);
            intent.putExtra("order", curentOrder);
            intent.putExtra("isOrderForUpdate", isOrderForUpdate);
            startActivity(intent);
        }
    }

    private void showView(View viewVisible, View viewGone){
        viewVisible.setVisibility(View.VISIBLE);
        viewGone.setVisibility(View.GONE);
    }

    private void setCurentClient() {
       String clientUniqueID = UUID.randomUUID().toString();

       if(curentClient!=null)
            clientUniqueID = curentClient.getClientId();

        Calendar calendar = Calendar.getInstance();

       curentClient = new Client(clientUniqueID, editClientName.getText().toString().trim(),
               editClientAddress.getText().toString().trim(),
               editClientDescription.getText().toString().trim(),
               editClientPhonNumber1.getText().toString(),
               editClientPhonNumber2.getText().toString(), calendar.getTimeInMillis(),
               false, false);

       if(orderAdapter != null && orderAdapter.getClientOrders().size()>0){
            curentClient.setLatestOrder(orderAdapter.getClientOrders().get(0).getOrdDateCreated());

        for (Order or: orderAdapter.getClientOrders() ) {
            if (!or.isClosed()) {
                curentClient.setSomeOrderOpen(true);
                break;
            }
        }

        for (Order or: orderAdapter.getClientOrders() ) {
            if (or.isRequestBid()) {
                curentClient.setSomeBidRequest(true);
                break;
            }
        }
     }
        ClientDBHelper clienttDB = new ClientDBHelper(this, userId);
        clienttDB.addNewClient(curentClient);
    }

    private boolean inputsIsGood() {
        String msgError="";

//        if (editClientPhonNumber2.getText().toString().trim().length()>0 &&
//                editClientPhonNumber2.getText().toString().trim().length() <10) {
//            editClientPhonNumber2.setError(getString(R.string.client_phone_number_empty));
//            editClientPhonNumber2.requestFocus();
//            msgError+=getString(R.string.client_phone_number_empty);
//        }

        if (editClientPhonNumber1.getText().toString().trim().length() != 10) {
            editClientPhonNumber1.setError(getString(R.string.client_phone_number_empty));
            editClientPhonNumber1.requestFocus();
            msgError+=getString(R.string.client_phone_number_empty);
        }

        if (editClientName.getText().toString().trim().length() < 1) {
            editClientName.setError(getString(R.string.client_name_empty));
            editClientName.requestFocus();
            if(msgError.length()>0)
                msgError+="\n";
            msgError+=getString(R.string.client_name_empty);
        }

        if(msgError.length()>0) {
            new Alert(this, msgError, Gravity.CENTER, Toast.LENGTH_LONG, ContextCompat.getColor(this, R.color.error_color));
            return false;
        }
        else
            return true;
    }

    private void goToMainActivity(){
      //  MethodsForApp.closeKeyboard(this,editClientName);
        Intent intent;
        intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("pagerItem", 1);
        intent.putExtra("user", userId);
        startActivity(intent);
        finish();
    }

    private void init() {
        Intent intent = getIntent();
        userId = intent.getStringExtra("user");
        curentClient = intent.getParcelableExtra("client");
        ordDBhelper = new OrderDBHelper(this,userId );

        if(curentClient!=null) {
            lblAddProduct.setText(getString(R.string.update_current_client));
        }

        orderAdapter = new OrderAdapter(this, false, userId);

        if(curentClient!=null) {
            ordDBhelper.getAllClientOrders(curentClient.getClientId());

            editClientName.setText(curentClient.getClientName());
            editClientAddress.setText(curentClient.getClientAddress());
            editClientPhonNumber1.setText(curentClient.getClientPhonNumber1());
            editClientPhonNumber2.setText(curentClient.getClientPhonNumber2());
            editClientDescription.setText(curentClient.getClientDescription());


            if(curentClient.getClientDescription()!=null)
                showView(lblClientDescription, linkAddClientNote);
            if(!curentClient.getClientPhonNumber2().isEmpty())
                showView(lblClientPhoneNumber2, linkAddPhoneNumber2);
        }
    }

    private void findAllViews(){
        imgSaveClient = findViewById(R.id.imgBtnSaveClient);
        imgSaveClient.setOnClickListener(this);
        btnSaveClient = findViewById(R.id.btn_save_client);
        btnSaveClient.setOnClickListener(this);

        btnGoBack = findViewById(R.id.imgBtnGoBack);
        btnGoBack.setOnClickListener(this);

        lblAddProduct = findViewById(R.id.lbl_add_client);
        linkAddOrder = findViewById(R.id.link_add_order);
        linkAddOrder.setOnClickListener(this);
        linkAddPhoneNumber2 =findViewById(R.id.link_add_phone_number2);
        linkAddPhoneNumber2.setOnClickListener(this);
        lblClientPhoneNumber2 = findViewById(R.id.lblClientPhoneNumber2);
        lblClientDescription = findViewById(R.id.lblClientDescription);
        linkAddClientNote = findViewById(R.id.link_add_client_note);
        linkAddClientNote.setOnClickListener(this);
        editClientName = findViewById(R.id.inputClientName);
        editClientAddress = findViewById(R.id.inputClientAddress);
        editClientPhonNumber1 = findViewById(R.id.inputClientPhoneNumber1);
        editClientPhonNumber2 = findViewById(R.id.inputClientPhoneNumber2);
        editClientDescription = findViewById(R.id.inputClientDescription);

        recOrderList = findViewById(R.id.recycViewOrders);
        recOrderList.setLayoutManager(new GridLayoutManager(this, 1));

    }

    @Override
    public void setClientOrdersAdapter(ArrayList<Order> orders) {
        recOrderList.setAdapter(orderAdapter );
        Collections.sort(orders, Order.DateCreated);
        orderAdapter.addAll(orders);
    }
}



