package com.olgag.mngclients.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.olgag.mngclients.BuildConfig;
import com.olgag.mngclients.R;
import com.olgag.mngclients.controler.MyPagerAdapter;
import com.olgag.mngclients.controler.OrderAdapter;
import com.olgag.mngclients.db.NotesDBHelper;
import com.olgag.mngclients.db.OrderDBHelper;
import com.olgag.mngclients.fragments.AppointmentFragment;
import com.olgag.mngclients.fragments.DatePickerFragment;
import com.olgag.mngclients.fragments.OrderFragment;
import com.olgag.mngclients.methods.MethodsForApp;
import com.olgag.mngclients.model.Order;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        OrderAdapter.OnOrderClickListener,
        ViewPager.OnPageChangeListener,
        NotesDBHelper.OnGetSMSTemplateSuccess,
        OrderDBHelper.OnGetAllClientsOrderSuccess,
        DatePickerFragment.OnDateSetClickListener {

    private static final int REQUEST_PERMISSIONS = 1;
    private static final String[] PERMISSIONS = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};



    private FragmentPagerAdapter adapterViewPager;
    private FloatingActionButton btnAddClientOrProduct, btnCloseApp, btnSettings;
    private ViewPager vpPager;
    private String userId, smsTemplate;
    private ArrayList<Order> orders = new ArrayList<>();
    private int currentItem;
    private boolean doubleBackToExitPressedOnce = false;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        userId = intent.getStringExtra("user");
        currentItem = intent.getIntExtra("pagerItem", 0);

        btnAddClientOrProduct = findViewById(R.id.btn_add);

       // if(isStoragePermissionGranted()) {
            init();
     //  }
    }

    private   boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_PERMISSIONS);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_PERMISSIONS:
                boolean isPerpermissionForAllGranted = false;
                if (grantResults.length > 0 && permissions.length == grantResults.length) {
                    for (int i = 0; i < permissions.length; i++) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            isPerpermissionForAllGranted = true;
                        } else {


                            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                                    Manifest.permission.READ_CALL_LOG)) {
                            } else {
                                String message = "You have previously declined this permission.\n" +
                                        "You must approve this permission.";
                                Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).setAction("Settings", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID)));
                                    }
                                });
                                snackbar.show();
                            }

                            isPerpermissionForAllGranted = false;
                        }
                    }

                    // Log.e("value", "Permission Granted, Now you can use local drive .");
                } else {
                    isPerpermissionForAllGranted = true;
                    // Log.e("value", "Permission Denied, You cannot use local drive .");
                }

                if (isPerpermissionForAllGranted) {
                    init();
                }
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void init() {
        MethodsForApp.checkLocal(this);
        vpPager = findViewById(R.id.vpPager);
        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager(), this, userId);

        vpPager.setAdapter(adapterViewPager);
        vpPager.setCurrentItem(currentItem);
        vpPager.addOnPageChangeListener(this);

        btnAddClientOrProduct.setOnClickListener(this);

        btnCloseApp = findViewById(R.id.btn_close_app);
        btnCloseApp.setOnClickListener(this);

        btnSettings = findViewById(R.id.btn_settings);
        btnSettings.setOnClickListener(this);
        ((TextView)findViewById(R.id.appName)).setText(getString(R.string.app_name));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FirebaseAuth.getInstance().signOut();
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.btn_add:
                intent = new Intent(this, AddUpdateClient.class);
                intent.putExtra("user", userId);
                startActivity(intent);
                break;
            case R.id.btn_close_app:
                showSnackForCloseApp();
                break;
            case R.id.btn_settings:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
        }
    }


    @Override
    public void onPageSelected(int currentItem) {
        this.currentItem = currentItem;
        //Fragment fragment = adapterViewPager.getItem(currentItem);
        switch (currentItem) {
            case 0:
                btnAddClientOrProduct.setVisibility(View.GONE);
                setSMSTemplate(smsTemplate);
                break;
            case 1:
                btnAddClientOrProduct.setVisibility(View.VISIBLE);
                break;
            case 2:
                btnAddClientOrProduct.setVisibility(View.GONE);
                callToOrderFragmentSetOrderAdapter();
                break;
        }

    }
    @Override
    public void onPageScrollStateChanged(int state) {
    }
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(currentItem==1) {
            btnAddClientOrProduct.setVisibility(View.VISIBLE);
        }
        else {
            btnAddClientOrProduct.setVisibility(View.GONE);
        }
    }

    @Override
    public void openOrderForUpdate(Order order, int currentItem, boolean isOrderForUpdate) {
        //Toast.makeText(this, "main" +  or.toString(), Toast.LENGTH_SHORT).show();
        Intent intent;
        intent = new Intent(this, AddUpdateOrder.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("user", userId);
        intent.putExtra("order", order);
        intent.putExtra("isOrderForUpdate", isOrderForUpdate);

        startActivity(intent);
    }

    @Override
    public void setSMSTemplate(String sms_template) {
        //Log.i("sms_template: ", sms_template);
        if(currentItem == 0) {
            AppointmentFragment appFragment = (AppointmentFragment) vpPager
                    .getAdapter()
                    .instantiateItem(vpPager, vpPager.getCurrentItem());
            if (sms_template == null || sms_template.isEmpty())
                  this.smsTemplate = "שלום NAME,\n" +
                          "נקבע לך פגישה בתאריך: DATE בשעה: TIME,\n" +
                          "בברכה," ;
            else
                this.smsTemplate =sms_template;
            appFragment.setSMSTemplate(this.smsTemplate);
        }
    }

    @Override
    public void setDate(int year, int month, int dayOfMonth) {
        switch (currentItem) {
            case 0:
                AppointmentFragment appFragment = (AppointmentFragment) vpPager
                        .getAdapter()
                        .instantiateItem(vpPager, vpPager.getCurrentItem());
                appFragment.setDate(year, month, dayOfMonth);
                break;
            case 2:
                OrderFragment fragment = (OrderFragment) vpPager
                        .getAdapter()
                        .instantiateItem(vpPager, vpPager.getCurrentItem());
                fragment.setDate(year, month, dayOfMonth);
            break;
        }
    }

    @Override
    public void setClientOrdersAdapter(ArrayList<Order> or) {
        orders.clear();
        orders = or;
        if(currentItem==2) {
            callToOrderFragmentSetOrderAdapter();        }
    }
    private void callToOrderFragmentSetOrderAdapter(){
        OrderFragment fragment = (OrderFragment) vpPager
                .getAdapter()
                .instantiateItem(vpPager, vpPager.getCurrentItem());
        fragment.setClientOrdersAdapter(orders);
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        showSnackForCloseApp();
    }

    private void showSnackForCloseApp(){
        Snackbar snackbar = Snackbar.make(btnAddClientOrProduct, MethodsForApp.mskeMessageForSnack(getString(R.string.click_exit)),
                Snackbar.LENGTH_LONG).
                setAction(getString(R.string.exit), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                });
        snackbar.show();
    }
}