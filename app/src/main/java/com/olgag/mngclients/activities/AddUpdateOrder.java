package com.olgag.mngclients.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.olgag.mngclients.R;
import com.olgag.mngclients.controler.Alert;
import com.olgag.mngclients.db.AppointmentDBHelper;
import com.olgag.mngclients.db.OrderDBHelper;
import com.olgag.mngclients.fragments.DatePickerFragment;
import com.olgag.mngclients.fragments.SetAppointmentDialogFragment;
import com.olgag.mngclients.methods.DecimalDigitsInputFilter;
import com.olgag.mngclients.methods.MethodsForApp;
import com.olgag.mngclients.model.Appointment;
import com.olgag.mngclients.model.Client;
import com.olgag.mngclients.model.Order;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;

public class AddUpdateOrder extends AppCompatActivity implements View.OnClickListener, DatePickerFragment.OnDateSetClickListener,
        CompoundButton.OnCheckedChangeListener, SetAppointmentDialogFragment.OnAppointmentSetClickListener,
        OrderDBHelper.OnGetAllClientsOrderSuccess, AppointmentDBHelper.OnSetArrAppointmentStart{

    private ImageButton btnGoBack, imgSaveOrder, imgClearAppointment, imgOpenOrder;
    private Button btnSaveOrder;
    private TextView linkViewDateOrd, titleForOrderActivit, linkCloseOrder, linkDateClosed, linkCreateAppointment;
    private TextInputEditText inputPrdSerialNumber, inputOrdReference, inputOrdCost, inputOrdDescription, inputProductName;
    private CheckBox checkIsOrderHasGuarantee;
    private RadioButton radioYes, radioNo, radioReceivedYes, radioReceivedNo;
    private long ordCreatedDate, ordClosedDate=0, appointmentStart = 0;

    private Order curentOrder;
    private String  userId;
    private Client currentClient;
    private boolean isOrderForUpdate, isClickOnCreateOrder, isDeleteAppointment,
            isOrderClosed, isRequestBid, isReceivedDevice;

    private DialogFragment newFragment;
    private SetAppointmentDialogFragment appointmentDialog;

    private AppointmentDBHelper appointmentDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_update_order);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.txt_view_date_ord:
                newFragment.show(getSupportFragmentManager(), "datePicker");
                isClickOnCreateOrder = true;
                break;
            case R.id.txt_date__closed:
                newFragment.show(getSupportFragmentManager(), "datePicker");
                isClickOnCreateOrder = false;
                break;
            case R.id.img_btn_go_back_to_client:
                MethodsForApp.closeKeyboard(this, inputOrdDescription);
                if(currentClient!=null)
                    goToClientActivity();
                else goToMainACtivity();
                break;
            case R.id.img_btn_save_order:
            case R.id.btn_save_order:
                MethodsForApp.closeKeyboard(this, inputOrdDescription);
                saveOrder();
                break;
            case R.id.radio_btn_no:
                isRequestBid = false;
                break;
            case R.id.radio_btn_yes:
                isRequestBid = true;
                break;
            case R.id.radio_received_no:
                isReceivedDevice = false;
                break;
            case R.id.radio_received_yes:
                isReceivedDevice = true;
                break;
            case  R.id.link_close_order:
                Date dt= new Date();
                ordClosedDate = dt.getTime() ;
                showOrderClose(true);
                break;
            case R.id.img_open_order:
                showOrderClose(false);
                break;
            case R.id.txt_view_create_appointment:
                FragmentManager manager = getSupportFragmentManager();
                appointmentDialog = new SetAppointmentDialogFragment(this, userId );
                appointmentDialog.show(manager, "setAppointment");
                break;
            case R.id.img_remove_appointment:
                linkCreateAppointment.setText(R.string.create_appointment);
                imgClearAppointment.setVisibility(View.INVISIBLE);
                isDeleteAppointment = true;
                appointmentStart = 0;
                break;
            default:
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked)
            checkIsOrderHasGuarantee.setText(getString(R.string.order_has_guarantee));
        else
            checkIsOrderHasGuarantee.setText(getString(R.string.order_dosnt_has_guarantee));
    }

    @Override
    public void setDate( int year, int month, int dayOfMonth) {
        Calendar calendar =new GregorianCalendar(year, month, dayOfMonth);
        if(isClickOnCreateOrder){
            ordCreatedDate = calendar.getTimeInMillis();
            linkViewDateOrd.setText(MethodsForApp.formatDateToString(ordCreatedDate ));
        }
        else {
             ordClosedDate = calendar.getTimeInMillis();
             linkDateClosed.setText(MethodsForApp.formatDateToString(ordClosedDate));
        }
    }
    @Override
    public void setAppointment(long appStart) {
        appointmentStart = appStart;
        isDeleteAppointment = false;
        linkCreateAppointment.setText(getString(R.string.The_appointment_was_scheduled_for) + "\n" + MethodsForApp.formatDateWithHourToString(appStart));
        imgClearAppointment.setVisibility(View.VISIBLE);
    }

    @Override
    public void getAppointmentDB(Appointment ap) {
        setAppointment(ap.getAppointmentStart());
    }

    private void showOrderClose(boolean isCloseOrder) {
        isOrderClosed = isCloseOrder;
        if(isCloseOrder) {
            findViewById(R.id.txt_order__closed).setVisibility(View.VISIBLE);
            linkDateClosed.setVisibility(View.VISIBLE);
            linkCloseOrder.setVisibility(View.GONE);
            linkDateClosed.setText(MethodsForApp.formatDateToString(ordClosedDate));
            imgOpenOrder.setVisibility(View.VISIBLE);
        }
        else{
            findViewById(R.id.txt_order__closed).setVisibility(View.GONE);
            linkDateClosed.setVisibility(View.GONE);
            linkCloseOrder.setVisibility(View.VISIBLE);
            imgOpenOrder.setVisibility(View.GONE);
        }
    }

    private void saveOrder() {
        OrderDBHelper orderDB = new OrderDBHelper(this, userId);
            if (isOrderForUpdate) {
                if(dateIsValid(ordCreatedDate,ordClosedDate)) {
                    int cost = 0;
                    if (!inputOrdCost.getText().toString().isEmpty()) {
                        if (checkCostValue())
                            cost = (int) (Float.parseFloat(inputOrdCost.getText().toString()) * 100);
                        else
                            return;
                    }
                    curentOrder = new Order(cost, curentOrder.getIdOrder(),
                            curentOrder.getClientID(),
                            inputProductName.getText().toString().trim(),
                            inputOrdReference.getText().toString().trim(),
                            inputPrdSerialNumber.getText().toString().trim(),
                            inputOrdDescription.getText().toString().trim(),
                            curentOrder.getClientName(), checkIsOrderHasGuarantee.isChecked(),
                            isOrderClosed, isRequestBid,ordCreatedDate,ordClosedDate, isReceivedDevice);
                    orderDB.addUpdateNewOrder( curentOrder, getString(R.string.order_added));
                    if(isDeleteAppointment)
                        appointmentDB.deleteAppointmentById(curentOrder.getIdOrder());
                    else if(appointmentStart>0) {
                        appointmentDB.addUpdateAppointment(new Appointment(curentOrder.getIdOrder(),curentOrder.getClientID(),  appointmentStart, false));
                    }
                    if(isOrderClosed)
                        gotToWorkCardActivity();
                    else if(currentClient!=null)
                        goToClientActivity();
                    else goToMainACtivity();
                }
            }else {
                if(inputsIsGood()) {
                    setCurentOrder();
                    orderDB.addUpdateNewOrder(curentOrder,getString(R.string.order_added));
                    if(isDeleteAppointment)
                        appointmentDB.deleteAppointmentById(curentOrder.getIdOrder());
                    else if(appointmentStart>0) {
                        appointmentDB.addUpdateAppointment(new Appointment(curentOrder.getIdOrder(),curentOrder.getClientID(),  appointmentStart, false));
                    }
                    if(isOrderClosed)
                        gotToWorkCardActivity();
                    else if(currentClient!=null)
                        goToClientActivity();
                    else goToMainACtivity();
            }
        }
    }

    private boolean inputsIsGood() {
       if(inputProductName.getText().toString().trim().length()==0) {
           new Alert(this, getString(R.string.product_name_empty), Gravity.CENTER, Toast.LENGTH_SHORT, ContextCompat.getColor(this, R.color.error_color));
           return false;
        }

       if(!dateIsValid(ordCreatedDate,ordClosedDate)) {
             return false;
        }

        if(!inputOrdCost.getText().toString().trim().isEmpty())
            return (checkCostValue());

       else
            return true;

    }

    private boolean checkCostValue(){
        if(!inputOrdCost.getText().toString().trim().matches("\\d+(?:\\.\\d+)?")) {
            new Alert(this, getString(R.string.wrong_value), Gravity.CENTER, Toast.LENGTH_SHORT, ContextCompat.getColor(this, R.color.error_color));
            return false;
        }
        if(Float.parseFloat(inputOrdCost.getText().toString())<1) {
            new Alert(this, getString(R.string.wrong_value), Gravity.CENTER, Toast.LENGTH_SHORT, ContextCompat.getColor(this, R.color.error_color));
            return false;
        }
        return true;
    }
    private boolean dateIsValid(long startGate, long endDate){
        if(endDate!=0 && startGate > endDate) {
            new Alert(this, getString(R.string.date_error), Gravity.CENTER, Toast.LENGTH_SHORT, ContextCompat.getColor(this, R.color.error_color));
            return false;
        }
            return true;
    }

    private void goToClientActivity(){

       // MethodsForApp.closeKeyboard(this, inputOrdDescription);
        Intent intent;
        intent = new Intent(this, AddUpdateClient.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("user", userId);
        intent.putExtra("client", currentClient);
        startActivity(intent);
        finish();
    }
    private void goToMainACtivity(){
        Intent intent;
        intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("pagerItem", 2);
        intent.putExtra("user", userId);
        startActivity(intent);
        finish();
    }

    private void gotToWorkCardActivity(){
        Intent intent;
        intent = new Intent(this, WorkCard.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("user", userId);
        intent.putExtra("client", currentClient);
        intent.putExtra("curentOrder", curentOrder);
        intent.putExtra("gotoclient", (currentClient!=null)?true:false);
        startActivity(intent);
        finish();
    }

    private void setCurentOrder() {
      //  Toast.makeText(this, ordClosedDate+ "", Toast.LENGTH_SHORT).show();
        String orderUniqueID = UUID.randomUUID().toString();
        int cost=0;
        if(!inputOrdCost.getText().toString().isEmpty()) {
           cost = (int) (Float.parseFloat(inputOrdCost.getText().toString()) * 100);
        }
        curentOrder = new Order(cost, orderUniqueID,  currentClient.getClientId(),
                inputProductName.getText().toString().trim(), inputOrdReference.getText().toString().trim(),
                inputPrdSerialNumber.getText().toString().trim(), inputOrdDescription.getText().toString().trim(),
                currentClient.getClientName(),  checkIsOrderHasGuarantee.isChecked(),isOrderClosed, isRequestBid,
                ordCreatedDate,ordClosedDate,isReceivedDevice);
    }


    private void init() {
        MethodsForApp.checkLocal(this);
        Intent intent = getIntent();
        userId = intent.getStringExtra("user");
        currentClient = intent.getParcelableExtra("client");

        appointmentDB = new AppointmentDBHelper(this, userId);

        isOrderForUpdate = intent.getBooleanExtra("isOrderForUpdate", false);
        curentOrder = intent.getParcelableExtra("order");

        newFragment = new DatePickerFragment(this);

        linkCreateAppointment = findViewById(R.id.txt_view_create_appointment);
        linkCreateAppointment.setOnClickListener(this);
        linkCreateAppointment.setText(R.string.create_appointment);
        imgClearAppointment = findViewById(R.id.img_remove_appointment);
        imgClearAppointment.setOnClickListener(this);

        radioYes = findViewById(R.id.radio_btn_yes);
        radioYes.setOnClickListener(this);
        radioYes.setText(R.string.yes);
        radioNo = findViewById(R.id.radio_btn_no);
        radioNo.setText(R.string.no);
        radioNo.setOnClickListener(this);

        radioReceivedYes = findViewById(R.id.radio_received_yes);
        radioReceivedYes.setOnClickListener(this);
        radioReceivedYes.setText(R.string.yes);
        radioReceivedNo = findViewById(R.id.radio_received_no);
        radioReceivedNo.setOnClickListener(this);
        radioReceivedNo.setText(R.string.no);

         btnGoBack = findViewById(R.id.img_btn_go_back_to_client);
         btnGoBack.setOnClickListener(this);

         imgSaveOrder= findViewById(R.id.img_btn_save_order);
         imgSaveOrder.setOnClickListener(this);
         btnSaveOrder = findViewById(R.id.btn_save_order);
         btnSaveOrder.setText(getString(R.string.save));
         btnSaveOrder.setOnClickListener(this);

         inputPrdSerialNumber = findViewById(R.id.inputSerialNumber);
         inputOrdReference = findViewById(R.id.input_ord_reference);
         inputOrdCost = findViewById(R.id.input_ord_cost);
         inputOrdCost.setFilters(new DecimalDigitsInputFilter[]{new DecimalDigitsInputFilter(7, 2)});

         inputOrdDescription = findViewById(R.id.input_ord_description);

         linkViewDateOrd = findViewById(R.id.txt_view_date_ord);
         linkViewDateOrd.setOnClickListener(this);
         linkCloseOrder = findViewById(R.id.link_close_order);
         linkCloseOrder.setOnClickListener(this);
         linkCloseOrder.setText(R.string.close_order);
         linkDateClosed = findViewById(R.id.txt_date__closed);
         linkDateClosed.setOnClickListener(this);
         imgOpenOrder = findViewById(R.id.img_open_order);
         imgOpenOrder.setOnClickListener(this);

         checkIsOrderHasGuarantee = findViewById(R.id.check_is_ord_exist);
         checkIsOrderHasGuarantee.setOnCheckedChangeListener(this);
         checkIsOrderHasGuarantee.setText(getString(R.string.order_dosnt_has_guarantee));

         titleForOrderActivit = findViewById(R.id.txt_title_order_activity);
         inputProductName = findViewById(R.id.input_product_name);

        ((TextInputLayout)findViewById(R.id.lblProductName)).setHint(getString(R.string.product_name));
        ((TextInputLayout)findViewById(R.id.lblSerialNumber)).setHint(getString(R.string.serial_number));
        ((TextInputLayout)findViewById(R.id.lblOrdRreference)).setHint(getString(R.string.reference));
        ((TextInputLayout)findViewById(R.id.lblOrdCcost)).setHint(getString(R.string.cost));
        ((TextInputLayout)findViewById(R.id.lblOrdDescription)).setHint(getString(R.string.description));
        ((TextView)findViewById(R.id.txtBid)).setText(getString(R.string.bid));
        ((TextView)findViewById(R.id.txtOrderCcreated)).setText(getString(R.string.order_created));
        ((TextView)findViewById(R.id.txtReceivedDevice)).setText(getString(R.string.received_device));
        ((TextView)findViewById(R.id.txt_order__closed)).setText(getString(R.string.order_closed));


            if (curentOrder != null) {
                appointmentDB.getAppointmentById(curentOrder.getIdOrder());
                titleForOrderActivit.setText(getString(R.string.update_current_order));
                inputProductName.setText(curentOrder.getPrdNameAndModel());
                inputPrdSerialNumber.setText(curentOrder.getSerialNumber());
                inputPrdSerialNumber.requestFocus();
                inputOrdReference.setText(curentOrder.getOrderReference());
                if (curentOrder.getCostOrder() != 0)
                    inputOrdCost.setText((curentOrder.getCostOrder() / 100f) + "");
                inputOrdDescription.setText(curentOrder.getOrderDescription());
                checkIsOrderHasGuarantee.setChecked(curentOrder.isGuarantee());
                ordCreatedDate = curentOrder.getOrdDateCreated();
                linkViewDateOrd.setText(MethodsForApp.formatDateToString(ordCreatedDate));
                radioYes.setChecked(curentOrder.isRequestBid());
                isRequestBid = curentOrder.isRequestBid();
                radioReceivedYes.setChecked(curentOrder.isReceivedDevice());
                isReceivedDevice = curentOrder.isReceivedDevice();
                showOrderClose(curentOrder.isClosed());
            }
            else {
                titleForOrderActivit.setText(getString(R.string.add_new_order));
                linkViewDateOrd.setText(MethodsForApp.formatDateToString(0));
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.HOUR,-6);
                ordCreatedDate =calendar.getTimeInMillis();
                //Toast.makeText(this, Calendar.WEEK_OF_YEAR + "", Toast.LENGTH_SHORT).show();
            }
        }

    @Override
    public void setClientOrdersAdapter(ArrayList<Order> orders) {}

    @Override
    public void setArrAppointmentTime(ArrayList<Long> arrAppointmentStart) {
        appointmentDialog.setArrAppointmentTime(arrAppointmentStart);
    }


}