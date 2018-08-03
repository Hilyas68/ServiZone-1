package com.fincoapps.servizone;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.OnClick;

import static java.lang.System.out;

public class About extends BaseActivity implements View.OnClickListener {
    String fb = "https://www.facebook.com/Adewale.1992";
    String twt = "https://www.twitter.com/Adewale.1992";
    String inst = "https://www.instagram.com/Adewale.1992";
    String finco = "http://www.fincoapps.com";
    ImageView fbimage, twtimage, instimage, fincoimg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
//        fbimage = findViewById(R.id.facebook);
//        fbimage.setOnClickListener(this);
//
//        twtimage = findViewById(R.id.twitter);
//        twtimage.setOnClickListener(this);
//
//        instimage = findViewById(R.id.instagram);
//        instimage.setOnClickListener(this);

        fincoimg = findViewById(R.id.fincologo);
        fincoimg.setOnClickListener(this);
    }

    public void onClick(View v) {
        switch(v.getId()){

//                case R.id.facebook:
//                    Intent i = new Intent(this, SocialNetworks.class);
//                    Bundle bundlei = new Bundle();
//                    bundlei.putString("url", fb);
//                    i.putExtras(bundlei);
//                    this.startActivity(i);
//                    break;
//
//                case R.id.twitter:
//                    Intent j = new Intent(this, SocialNetworks.class);
//                    Bundle bundlej = new Bundle();
//                    bundlej.putString("url", twt);
//                    j.putExtras(bundlej);
//                    this.startActivity(j);
//                    break;
//
//                case R.id.instagram:
//                    Intent k = new Intent(this, SocialNetworks.class);
//                    Bundle bundlek = new Bundle();
//                    bundlek.putString("url", inst);
//                    k.putExtras(bundlek);
//                    this.startActivity(k);
//                    break;

            case R.id.fincologo:
                Intent intent = new Intent(this, SocialNetworks.class);
                Bundle bundle = new Bundle();
                bundle.putString("url", finco);
                intent.putExtras(bundle);
                this.startActivity(intent);
                break;
        }
    }

    @OnClick(R.id.btnBack)
    public void onBackClick(View view) {
        out.println("=================");
        onBackPressed();
    }
}
