package com.fincoapps.servizone;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfilePictureActivity extends BaseActivity {

    @BindView(R.id.imageView)
    ImageView expertImage;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_picture);
        ButterKnife.bind(this);

        Intent i = getIntent();
        String expertImageUrl = i.getStringExtra("expertImageUrl");
        System.out.println("============== "+expertImageUrl);
        Glide.with(expertImage.getContext())
                .load("http://servizone.net/storage"+expertImageUrl)
                .placeholder(R.drawable.placeholder)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(expertImage);

    }


    public void onBackPressed() {
        super.onBackPressed();
        finish();
        //overridePendingTransition(R.anim.trans_right_out, R.anim.trans_right_in);
    }
}