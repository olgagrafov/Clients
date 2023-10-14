package com.olgag.mngclients.activities;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

import com.olgag.mngclients.R;

public class WebViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        Intent intent = getIntent();
        String clientAddress = intent.getStringExtra("clientAddress");
        WebView myWebView = (WebView) findViewById(R.id.webview);
        myWebView.loadUrl("https://waze.com/ul?navigate=yes&q=" + clientAddress);
    }
}