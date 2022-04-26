package com.example.tiktaktok;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class InstracitonAcivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instraciton_acivity);
        WebView myWebView = (WebView) findViewById(R.id.my_webview);
        myWebView.loadUrl("https://www.thesprucecrafts.com/tic-tac-toe-game-rules-412170");
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        myWebView.setWebViewClient(new WebViewClient());
    }
}