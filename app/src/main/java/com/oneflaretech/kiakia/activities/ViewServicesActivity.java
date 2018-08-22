package com.oneflaretech.kiakia.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.oneflaretech.kiakia.R;
import com.oneflaretech.kiakia.models.ServiceModel;
import com.oneflaretech.kiakia.utils.AppConstants;

public class ViewServicesActivity extends BaseActivity {

    String TAG = "View Services";
    ServiceModel service;

    //Components
    FloatingActionButton uploadBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_services);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        service = gson.fromJson(getIntent().getStringExtra("service"),ServiceModel.class);
        AppConstants.log(TAG, service.toString());
        init();
        checkHomeServices();
    }

    private void init() {
        uploadBtn = findViewById(R.id.upload_btn);
    }

    public void checkHomeServices(){
        if(service.getUser_id() != user.getId()){
            AppConstants.log(TAG, "Not User Service");
            uploadBtn.setVisibility(View.GONE);
        }
    }

}
