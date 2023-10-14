package com.olgag.mngclients.activities;

import android.content.Intent;
import android.gesture.GestureOverlayView;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.olgag.mngclients.R;
import com.olgag.mngclients.controler.Alert;
import com.olgag.mngclients.methods.MethodsForApp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class SignatureActivity extends AppCompatActivity implements View.OnClickListener {
    private Button  btnCleaSignature, btnOkSignature;
    private GestureOverlayView gestureOverlayView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signature);

        Intent getDataIntent = getIntent();
        String lng= getDataIntent.getStringExtra("lang");
        if(lng!=null && !lng.isEmpty()) {
            MethodsForApp.setUIperLang(lng, getResources());
        }

        init();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_clear_signature:
                gestureOverlayView.clear(false);
                break;
            case R.id.btn_ok_signature:
                 createSignature();
                break;
        }
    }

    private void createSignature() {
        gestureOverlayView.destroyDrawingCache();
        gestureOverlayView.setDrawingCacheEnabled(true);

        try {
            Bitmap drawingCacheBitmap = gestureOverlayView.getDrawingCache();
            Bitmap bitmap = Bitmap.createBitmap(drawingCacheBitmap);
            File imageFile = new File(this.getCacheDir(),   getString(R.string.sig_file_name));
            FileOutputStream outputStream = null;
            try {
                outputStream = new FileOutputStream(imageFile);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                outputStream.flush();
                outputStream.close();

                WorkCard.setSignature(this);
                finish();

            } catch (FileNotFoundException e) {
                new Alert(this, e.toString(), Gravity.CENTER, Toast.LENGTH_LONG, ContextCompat.getColor(this, R.color.error_color));
            } catch (IOException e) {
                new Alert(this, e.toString(), Gravity.CENTER, Toast.LENGTH_LONG, ContextCompat.getColor(this, R.color.error_color));
            }
        }
        catch(Exception e) {
            new Alert(this, e.toString(), Gravity.CENTER, Toast.LENGTH_LONG, ContextCompat.getColor(this, R.color.error_color));
        }
    }

    private void init(){
        btnCleaSignature = findViewById(R.id.btn_clear_signature);
        btnCleaSignature.setText(getString(R.string.clear));
        btnOkSignature = findViewById(R.id.btn_ok_signature);
        btnOkSignature.setText(getString(R.string.ok));
        btnCleaSignature.setOnClickListener(this);
        btnOkSignature.setOnClickListener(this);
        gestureOverlayView = findViewById(R.id.gest_sign_pad);
        gestureOverlayView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (v.getId() == R.id.gest_sign_pad) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_UP:
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }
                }

                return false;
            }
        });

    }
}