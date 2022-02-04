package com.biswa1045.MemeOnly;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class PrivacyActivity extends AppCompatActivity {
    private WebView webview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);

        webview = (WebView) findViewById(R.id.webview2);
        webview.setWebViewClient(new WebViewClient());
        webview.loadUrl("file:///android_asset/privacypolicy.html");
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webview.getSettings().setBuiltInZoomControls(true);
        webview.getSettings().setDisplayZoomControls(false);
    }

    @Override
    public void onBackPressed() {
        Intent  in = new Intent(PrivacyActivity.this,MainActivity.class);
        startActivity(in);
        finish();
    }
}