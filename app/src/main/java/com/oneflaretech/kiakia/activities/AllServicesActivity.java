package com.oneflaretech.kiakia.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.oneflaretech.kiakia.R;
import com.oneflaretech.kiakia.adapters.RecyclerItemClickListener;
import com.oneflaretech.kiakia.adapters.ServiceAdapter;
import com.oneflaretech.kiakia.models.ResponseObjectModel;
import com.oneflaretech.kiakia.models.ServiceModel;
import com.oneflaretech.kiakia.utils.AppConstants;

import java.lang.reflect.Type;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class AllServicesActivity extends BaseActivity {

    ArrayList<ServiceModel> services;
    TextView error;
    RecyclerView rv;
    ServiceAdapter adapter;
    String TAG = "My Services";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_services);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Bundle extras = getIntent().getExtras();
        if(extras.getBoolean("myService")){
            services = app.getMyServices();
            getSupportActionBar().setTitle("My Services");
        }
        error = findViewById(R.id.no_services);
        rv = findViewById(R.id.my_services_list);
        notification.setAnchor(toolbar);
        AppConstants.log(TAG, services.toString());
        AppConstants.log(TAG, String.valueOf(services.size()));

        checkServices();
        loadMyServices();
        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show());
        fab.setOnClickListener(view -> loadMyServices());
    }

    private void checkServices() {
        if(services.size() == 0){
            error.setVisibility(View.VISIBLE);
            rv.setVisibility(View.GONE);
        }
        else{
            error.setVisibility(View.GONE);
            rv.setVisibility(View.VISIBLE);
            AppConstants.log(TAG, services.toString());
            initView();
        }
    }

    private void loadMyServices() {
        retrofit.getApiService().getMyService(user.getToken())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResponseObjectModel>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        String msg = "An Error Occurred. Please Try Again";
                        if(e instanceof HttpException)
                            msg = "No Internet Connection";
                        if(e instanceof SocketException ||
                                e instanceof SocketTimeoutException)
                            msg = "An Internet Error Occurred";
                        if(e instanceof IllegalStateException)
                            msg = "Invalid Response";

                        notification.setMessage(msg);
                        notification.setAnchor(rv);
                        notification.show();
                        AppConstants.log(TAG, e.toString());
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(ResponseObjectModel responseObjectModel) {
                        AppConstants.log(TAG, responseObjectModel.toString());
                        checkResponse(responseObjectModel);
                        if(responseObjectModel.getStatus().equals(AppConstants.STATUS_SUCCESS)){
                            if(responseObjectModel.getData() != null || !responseObjectModel.getData().equals("")){
                                try{
                                    Type listType = new TypeToken<ArrayList<ServiceModel>>() {}.getType();
                                    services = gson.fromJson(responseObjectModel.getData(), listType);
                                    app.setMyServices(services);
                                    checkServices();
                                }catch (Exception ex){
                                    notification.setMessage(ex.getMessage());
                                    notification.show();
                                    AppConstants.log(TAG, ex.getMessage());
                                }
                            }else {
                                notification.setMessage(responseObjectModel.getMessage());
                                notification.show();
                            }
                        }else {
                            notification.setMessage(responseObjectModel.getMessage());
                            notification.show();
                        }
                    }
                });
    }

    private void initView() {
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.addItemDecoration(new DividerItemDecoration(rv.getContext(),llm.getOrientation()));
        adapter = new ServiceAdapter(this, services);
        rv.addOnItemTouchListener(new RecyclerItemClickListener(this, rv, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                showToast("Clicked on " + (position +1));
            }

            @Override
            public void onLongItemClick(View view, int position) {
                showToast("Long Clicked on " + (position +1));
            }
        }));
        rv.setAdapter(adapter);

    }

}
