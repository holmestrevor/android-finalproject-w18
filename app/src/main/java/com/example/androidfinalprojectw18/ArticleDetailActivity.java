package com.example.androidfinalprojectw18;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

public class ArticleDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.article_detail);

        Bundle data = getIntent().getExtras();

        String url = data.getString("link");

        WebView web = (WebView)findViewById(R.id.webView);
        web.loadUrl(url);
    }
}
