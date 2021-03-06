package com.oneflaretech.kiakia.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.oneflaretech.kiakia.R;
import com.oneflaretech.kiakia.utils.AppConstants;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShowImageActivity extends BaseActivity {

    @BindView(R.id.imageView)
    ImageView imageView;

    String TAG = "ImageActivity";
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_picture);
        ButterKnife.bind(this);

        Intent i = getIntent();
        String expertImageUrl = i.getStringExtra("imageUrl");
        Picasso.get()
                .load(AppConstants.getFileHost() + expertImageUrl)
                .placeholder(R.drawable.noimage)
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        AppConstants.log(TAG,"Image Loaded");
                    }

                    @Override
                    public void onError(Exception e) {
                        AppConstants.log(TAG,e.getMessage());
                        e.printStackTrace();
                    }
                });
    }


    public void onBackPressed() {
        super.onBackPressed();
        finish();
        //overridePendingTransition(R.anim.trans_right_out, R.anim.trans_right_in);
    }
}