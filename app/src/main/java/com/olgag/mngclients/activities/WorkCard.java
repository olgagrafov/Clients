package com.olgag.mngclients.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.olgag.mngclients.R;
import com.olgag.mngclients.controler.Alert;
import com.olgag.mngclients.db.ClientDBHelper;
import com.olgag.mngclients.db.NotesDBHelper;
import com.olgag.mngclients.methods.MethodsForApp;
import com.olgag.mngclients.model.Client;
import com.olgag.mngclients.model.Order;

import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Locale;


public class WorkCard extends AppCompatActivity implements View.OnClickListener,
        ClientDBHelper.OnGetClientByIdSuccess, NotesDBHelper.OnGetDateSuccess, AdapterView.OnItemSelectedListener{

    private NotesDBHelper notestDB;
    private static ImageView imageViewSignature;
    private Spinner spinner;
    private ImageButton imgBtnSaveTitle, imgBtnSaveNote, btnSaveWorkCard, btnCloseWorkCard, btnShareByWhatsApp;
    private TextView txtTitle, txtClientDetails, txtClientName,txtPhoneNumber,txtClientAddress, txtModel, txtSerialNumber,
            txtOrdDetails,txtOpenOrdDate, txtGuarantee, txtOrdDescription, txtSignature, txtClientDescription,
            txtAskForCheckPrice, txtCloseOrdDate, txtNote, txtReceivedDevice, txtOrderCost;
    private TextInputEditText inputTitle, inputNote;
    private LinearLayout lblTxtTitle, lblTxtNote, con;
    private Order curentOrder;
    private String userId, appLng;
    private boolean  isPermission=false, isPrintNote = true, isPrintTitle = true,
            isGotoClient, requestPermissionForPdf=true, shareWhatsApp = false;
    private static final int REQUEST = 1;
    private Client currentClient;
    private Animation myAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_card);


        Resources resources = getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration configStr = resources.getConfiguration();
        Locale startLocal = configStr.locale;
        if(startLocal.toString().length() == 5)
            appLng = startLocal.toString().substring(3,5);
        else
            appLng = startLocal.toString();

        if(appLng.equals("IL"))
            appLng="HE";

        init();


    }

    @SuppressLint("WrongConstant")
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.txt_signature:
                makeSignature();
                break;
            case R.id.btn_save_work_card:
                shareWhatsApp = false;
                createPDF();
                break;
            case R.id.txt_title:
                lblTxtTitle.setVisibility(View.VISIBLE);
                txtTitle.setVisibility(View.GONE);
                inputTitle.setText(txtTitle.getText());
                break;
            case R.id.img_btn_save_title:
                lblTxtTitle.setVisibility(View.GONE);
                txtTitle.setVisibility(View.VISIBLE);
                txtTitle.setText(inputTitle.getText());
                notestDB.addUpdateNewTitle(inputTitle.getText().toString());
                MethodsForApp.closeKeyboard(this,txtTitle);
                break;
            case R.id.txt_note:
                lblTxtNote.setVisibility(View.VISIBLE);
                txtNote.setVisibility(View.GONE);
                inputNote.setText(txtNote.getText());
                break;
            case R.id.img_btn_save_note:
                lblTxtNote.setVisibility(View.GONE);
                txtNote.setVisibility(View.VISIBLE);
                txtNote.setText(inputNote.getText());
                notestDB.addUpdateNewFooter(inputNote.getText().toString());
                MethodsForApp.closeKeyboard(this,txtNote);
                break;
            case R.id.btn_close_work_card:
                closeWorkCard();
                break;
            case R.id.btn_share_with_whatsapp:
                shareWhatsApp = true;
                btnShareByWhatsApp.startAnimation(myAnim);
                createPDF();
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MethodsForApp.setUIperLang(appLng.toLowerCase(), getResources());
    }

    static void setSignature(Context con) {
        File imgfile = new File(con.getCacheDir(),   con.getString(R.string.sig_file_name));
        Bitmap btm = BitmapFactory.decodeFile(String.valueOf(imgfile));
        imageViewSignature.setImageBitmap(btm);
        if(imgfile.exists())
            imgfile.delete();
    }

    private void  createPDF() {

        txtSignature.setTextColor(Color.BLACK);
        if(!isPrintTitle)
            txtTitle.setText("");
        if(!isPrintNote)
            txtNote.setText("");
        //border!!! imageViewSignature

        try {
            File file = new File(this.getCacheDir(),    getString(R.string.work_card_file_name));
            FileOutputStream fOut = null;
            fOut = new FileOutputStream(file);
            PdfDocument document = new PdfDocument();
            PdfDocument.PageInfo pageInfo = new
                    PdfDocument.PageInfo.Builder(1004 , 1512 , 1).create();
            PdfDocument.Page page = document.startPage(pageInfo);

            con.setBackgroundColor(Color.WHITE);
            con.setDrawingCacheEnabled(true);

            Bitmap bitmap = null;
            if(con.getDrawingCache()==null)
            {
                bitmap = loadLargeBitmapFromView(con);// for Samsung Galaxy note 8.0
            }
            else
            {
                bitmap = Bitmap.createBitmap(con.getDrawingCache());
            }
            con.setDrawingCacheEnabled(false);

            Matrix m = new Matrix();
            m.setRectToRect(new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight()), new RectF(0, 0, 1004, 1512), Matrix.ScaleToFit.CENTER);
            Bitmap bitmap2 =  Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);

            Canvas canvas = page.getCanvas();
            canvas.drawBitmap(bitmap2, 50,50, null);

            document.finishPage(page);
            document.writeTo(fOut);
            document.close();

            if(shareWhatsApp)
                shareWithWhatsApp();
            else
                shareWorkCard();

        }
       catch (Exception e){
           new Alert(this, e.toString(), Gravity.CENTER, Toast.LENGTH_LONG, ContextCompat.getColor(this, R.color.error_color));
           closeWorkCard();
       }
    }

    private Bitmap loadLargeBitmapFromView(View v)
    {
        Bitmap b = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
        v.draw(c);
        return b;
    }


   private void makeSignature()
    {
        String[] lng= getResources().getStringArray(R.array.lng_for_config);
     //   Toast.makeText(this, lng[spinner.getSelectedItemPosition()], Toast.LENGTH_SHORT).show();
        Intent inOpemSognature = new Intent(this, SignatureActivity.class);
        inOpemSognature.putExtra("lang", lng[spinner.getSelectedItemPosition()]);
        startActivity(inOpemSognature);
    }

    private void shareWithWhatsApp () {
        String phoneNumber ="972" +  currentClient.getClientPhonNumber1().substring(1);
        File file = new File(this.getCacheDir(),    getString(R.string.work_card_file_name));

        if (file.exists()) {
     //       Uri urFile = FileProvider.getUriForFile(this, "com.olgag.mngclients", file);

//            Intent intent = ShareCompat.IntentBuilder.from(WorkCard.this)
//                    .setStream(urFile)
//                    .setType("application/pdf")
//                    .getIntent()
//                    .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            intent.putExtra("jid", PhoneNumberUtils.stripSeparators(phoneNumber) + "@s.whatsapp.net");
//            startActivity(intent);


          //  intent.setPackage("com.whatsapp");
           // startActivity(Intent.createChooser(intent, "send"));

               try {
                    Uri urFile =  FileProvider.getUriForFile(this, "com.olgag.mngclients", file);
                    Intent sendIntent = new Intent(Intent.ACTION_SEND);
                    sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                       sendIntent.addFlags(Intent.FLAG_ACTIVITY_REQUIRE_NON_BROWSER);
                   }
                   sendIntent.putExtra(Intent.EXTRA_STREAM,  urFile ); //Uri.fromFile(file));
                    sendIntent.putExtra("jid", PhoneNumberUtils.stripSeparators(phoneNumber) + "@s.whatsapp.net");
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.setType("application/pdf");
                    startActivity(sendIntent);

                } catch (Exception e) {
                    Toast.makeText(this, "WhatsApp not installed.", Toast.LENGTH_SHORT).show();
                }

            } else
                Toast.makeText(this, "File not found", Toast.LENGTH_SHORT).show();

    }

    private void shareWorkCard() throws UnsupportedEncodingException {

        File file = new File(this.getCacheDir(),    getString(R.string.work_card_file_name));

      // Log.i("file : ", file.toString());
        if(file.exists()) {
            Uri urFile = FileProvider.getUriForFile(this, "com.olgag.mngclients", file);

            Intent intent = ShareCompat.IntentBuilder.from(WorkCard.this)
                    .setStream(urFile) // uri from FileProvider
                    .setType("application/pdf")
                    .getIntent()
                    .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(intent, "send"));


            //   Log.i("file : ", urFile.toString());
//            Intent intent = new Intent();
//            intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
//            intent.setType("application/pdf");
//           // intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//            intent.putExtra(Intent.EXTRA_STREAM, urFile);
//            intent.putExtra(Intent.EXTRA_SUBJECT, "Work card for: " + currentClient.getClientName());
//            intent.putExtra(Intent.EXTRA_TEXT, "Check out work card for: " + currentClient.getClientName());
//            startActivity(Intent.createChooser(intent, "Share File"));
        }
         else
            Toast.makeText(this, "file not found", Toast.LENGTH_SHORT).show();
    }

    private void closeWorkCard(){
        MethodsForApp.setUIperLang(appLng.toLowerCase(), getResources());
        Intent close_intent;
        close_intent = new Intent(this, AddUpdateClient.class);
        if(!isGotoClient) {
            close_intent = new Intent(this, MainActivity.class);
            close_intent.putExtra("pagerItem", 2);
        }
        close_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        close_intent.putExtra("user", userId);
        close_intent.putExtra("client", currentClient);
        startActivity(close_intent);
        finish();
    }

    private void init() {

        Intent getIntent = getIntent();
        userId = getIntent.getStringExtra("user");
        curentOrder = getIntent.getParcelableExtra("curentOrder");
        currentClient = getIntent.getParcelableExtra("client");
        isGotoClient= getIntent.getBooleanExtra("gotoclient", false);

        ClientDBHelper clienttDB = new ClientDBHelper(this, userId, true);
        clienttDB.getClientById(curentOrder.getClientID());

        notestDB = new NotesDBHelper(this, userId);
        notestDB.getTitle();
        notestDB.getFooter();

        spinner = findViewById(R.id.spinner_setLng);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.lng_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        con = findViewById(R.id.all_context);

        btnSaveWorkCard = findViewById(R.id.btn_save_work_card);
        btnSaveWorkCard.setOnClickListener(this);
        btnCloseWorkCard = findViewById(R.id.btn_close_work_card);
        btnCloseWorkCard.setOnClickListener(this);
        btnShareByWhatsApp = findViewById(R.id.btn_share_with_whatsapp);
        btnShareByWhatsApp.setOnClickListener(this);
        myAnim = AnimationUtils.loadAnimation(this, R.anim.img_click);

        imageViewSignature = findViewById(R.id.img_signature);
        txtTitle = findViewById(R.id.txt_title);
        txtTitle.setOnClickListener(this);
        inputTitle = findViewById(R.id.input_txt_title);
        lblTxtTitle = findViewById(R.id.lbl_txt_title);
        imgBtnSaveTitle =  findViewById(R.id.img_btn_save_title);
        imgBtnSaveTitle.setOnClickListener(this);

        txtNote = findViewById(R.id.txt_note);
        txtNote.setOnClickListener(this);
        inputNote  = findViewById(R.id.input_txt_note);
        lblTxtNote = findViewById(R.id.lbl_txt_note);
        imgBtnSaveNote  =  findViewById(R.id.img_btn_save_note);
        imgBtnSaveNote.setOnClickListener(this);
        txtSignature = findViewById(R.id.txt_signature);
        txtSignature.setOnClickListener(this);

        txtClientDetails = findViewById(R.id.txt_client_details);
        txtClientName = findViewById(R.id.txt_client_name);
        txtPhoneNumber = findViewById(R.id.txt_phone_number);
        txtClientAddress = findViewById(R.id.txt_client_address);
        txtClientDescription = findViewById(R.id.txt_client_description);
        txtOrdDetails = findViewById(R.id.txt_ord_details);
        txtModel = findViewById(R.id.txt_ord_model);
        txtSerialNumber =  findViewById(R.id.txt_serial_number);
        txtOpenOrdDate = findViewById(R.id.txt_open_order_date);
        txtGuarantee = findViewById(R.id.txt_guarantee);
        txtOrdDescription = findViewById(R.id.txt_ord_description);
        txtAskForCheckPrice = findViewById(R.id.txt_ask_for_check_price);
        txtOrderCost = findViewById(R.id.txt_order_cost);
        txtCloseOrdDate = findViewById(R.id.txt_close_order_date);
        txtReceivedDevice = findViewById(R.id.txt_received_device);
      }

    private void changeUIbylanguage() {

        if(currentClient!=null) {
            txtClientName.setText(getString(R.string.name) + ": " + currentClient.getClientName());
            txtPhoneNumber.setText(getString(R.string.phone_number) + ": " + currentClient.getClientPhonNumber1());
            if (currentClient.getClientAddress().length() > 0)
                txtClientAddress.setText(getString(R.string.address) + ": " + currentClient.getClientAddress());
            else {
                txtClientAddress.setVisibility(View.GONE);
            }
            if (currentClient.getClientDescription().length() > 0)
                txtClientDescription.setText(getString(R.string.description) + ": " + currentClient.getClientDescription());
            else {
                txtClientDescription.setVisibility(View.GONE);
            }

        }

        TextInputLayout  txtLayoutTitle = findViewById(R.id.txt_laytitle);
        txtLayoutTitle.setHint(getString(R.string.title));
        if(!isPrintTitle) {
            txtTitle.setText(getString(R.string.set_title));
            inputTitle.setText(getString(R.string.set_title));
        }

        TextInputLayout  txttxtLayoutTitleNote = findViewById(R.id.txt_laynote);
        txttxtLayoutTitleNote.setHint(getString(R.string.note));
        if(!isPrintNote) {
            txtNote.setText(getString(R.string.set_note));
            inputNote.setText(getString(R.string.set_note));
        }

        txtClientDetails = findViewById(R.id.txt_client_details);
        txtClientDetails.setText(getString(R.string.client_data));

        txtSignature.setText(getString(R.string.customer_signature));

        txtOrdDetails.setText(getString(R.string.details_for) + " " + curentOrder.getPrdNameAndModel() + ":");
        txtOpenOrdDate.setText(getString(R.string.start_date) + " " +  MethodsForApp.formatDateToString(curentOrder.getOrdDateCreated()));

        if(curentOrder.getOrderReference().length()>1)
            txtModel.setText(getString(R.string.reference) + ": " + curentOrder.getOrderReference());
        else
            txtModel.setVisibility(View.GONE);

        if(curentOrder.getSerialNumber().length()>1)
            txtSerialNumber.setText(getString(R.string.serial_number) + ": " + curentOrder.getSerialNumber());
        else
            txtSerialNumber.setVisibility(View.GONE);;

        if(curentOrder.isGuarantee())
            txtGuarantee.setText(getString(R.string.order_has_guarantee));
        else
            txtGuarantee.setText(getString(R.string.order_dosnt_has_guarantee));

        if(curentOrder.isRequestBid())
            txtAskForCheckPrice.setText(getString(R.string.client_requested_to_check));
        else
            txtAskForCheckPrice.setVisibility(View.GONE);

        if(curentOrder.getCostOrder()>0)
            txtOrderCost.setText(getString(R.string.cost) + ": " + curentOrder.getCostOrder()/100f);
        else
            txtOrderCost.setVisibility(View.GONE);

        if(curentOrder.getOrderDescription().length()>0)
            txtOrdDescription.setText( getString(R.string.description) + ": " + curentOrder.getOrderDescription());
        else
            txtOrdDescription.setVisibility(View.GONE);

        if(curentOrder.getOrdDateClosed()>0)
            txtCloseOrdDate.setText(getString(R.string.end_date) + " " + MethodsForApp.formatDateToString(curentOrder.getOrdDateClosed()));

        if(curentOrder.isReceivedDevice())
            txtReceivedDevice.setText( getString(R.string.received_device));
        else txtReceivedDevice.setVisibility(View.GONE);
    }


    @Override
    public void setCurrentClient(Client cl) {
        currentClient = cl;
        changeUIbylanguage();
    }

    @Override
    public void setTitle(String title) {
        if(title== null || title.length()<1) {
            txtTitle.setText(getString(R.string.set_title));
            isPrintTitle = false;
        }
        else
            txtTitle.setText(title);
    }

    @Override
    public void setNote(String note) {
        if(note== null || note.length()<1) {
            txtNote.setText(getString(R.string.set_note));
            isPrintNote = false;
        }
        else {
            txtNote.setText(note);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        changeLang(position);
    }

    private void changeLang(int position){
        switch (position){
            case 0:
                MethodsForApp.setUIperLang("he", getResources());
                break;
            case 1:   MethodsForApp.setUIperLang("en", getResources());
                break;
            case 2:
                MethodsForApp.setUIperLang("ru", getResources());
                break;
        }
        changeUIbylanguage();
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

}