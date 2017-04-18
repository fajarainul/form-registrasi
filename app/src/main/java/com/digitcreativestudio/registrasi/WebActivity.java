package com.digitcreativestudio.registrasi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

public class WebActivity extends AppCompatActivity {
    WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        webView = (WebView)findViewById(R.id.webView);
        String url = "http://ternatekota.sicantik.layanan.go.id/perizinan_online";

        Document doc;
        try {
            doc = Jsoup.connect(url).get();
            Elements ele = doc.select("..block-content");

            String html = ele.toString();
            String mime = "text/html";
            String encoding = "utf-8";
            webView.loadData(html, mime, encoding);

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "ERROR TERJADI", Toast.LENGTH_SHORT).show();
        }


    }
}
