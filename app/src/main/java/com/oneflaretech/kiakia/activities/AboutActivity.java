package com.oneflaretech.kiakia.activities;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.oneflaretech.kiakia.R;


public class AboutActivity extends BaseActivity {
    String fb = "https://www.facebook.com/Adewale.1992";
    String twt = "https://www.twitter.com/Adewale.1992";
    String inst = "https://www.instagram.com/Adewale.1992";
    String finco = "http://www.fincoapps.com";
    ImageView fbimage, twtimage, instimage, fincoimg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Toolbar toolbar = findViewById(R.id.hometoolbar);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("About");
    }

    public void onClick(View v) {
        switch (v.getId()) {

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

//            case R.id.fincologo:
//                Intent intent = new Intent(this, SocialNetworks.class);
//                Bundle bundle = new Bundle();
//                bundle.putString("url", finco);
//                intent.putExtras(bundle);
//                this.startActivity(intent);
//                break;
        }
    }
}
