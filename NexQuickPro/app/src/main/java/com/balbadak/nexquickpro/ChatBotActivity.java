package com.balbadak.nexquickpro;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.WindowManager;
import android.webkit.WebView;


public class ChatBotActivity extends AppCompatActivity  {

    private String mainUrl;
    private WebView chatBotWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);
        mainUrl = getResources().getString(R.string.main_url);
        chatBotWebView = findViewById(R.id.chatBotWebView);


        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        int width = (int) (display.getWidth() * 0.90); //Display 사이즈의 70%
        int height = (int) (display.getHeight() * 0.67);  //Display 사이즈의 80%

        getWindow().getAttributes().width = width;
        getWindow().getAttributes().height = height;


        chatBotWebView.getSettings().setJavaScriptEnabled(true);
        chatBotWebView.loadUrl(mainUrl+"payTest.jsp");




    }



}
