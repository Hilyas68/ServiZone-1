package com.oneflaretech.kiakia.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.bridge.Bridge;
import com.afollestad.bridge.BridgeException;
import com.afollestad.bridge.Response;
import com.afollestad.bridge.ResponseConvertCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.gson.Gson;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.oneflaretech.kiakia.R;
import com.oneflaretech.kiakia.experts.ExpertDetailsActivity;
import com.oneflaretech.kiakia.models.ExpertModel;
import com.oneflaretech.kiakia.models.HomeModel;
import com.oneflaretech.kiakia.models.ResponseObjectModel;
import com.oneflaretech.kiakia.utils.AppConstants;
import com.oneflaretech.kiakia.utils.Request;
import com.squareup.picasso.Picasso;

import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.List;

import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SecondaryActivity extends BaseActivity {

    String TAG = "Secondary View";
    View v;
    RelativeLayout Errorpage;
    SwipeRefreshLayout mSwipeRefreshLayout = null;
    LinearLayout container;
    private String savedHome;
    private FusedLocationProviderClient mFusedLocationClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secondary);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
        setSupportActionBar(toolbar);

        FloatingActionButton fab =  findViewById(R.id.switch_button);
        fab.setOnClickListener(view -> onBackPressed());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Home");

        //Imit


        container = findViewById(R.id.mainContent);
        Errorpage = container.findViewById(R.id.Errorpage);
        TextView reload = Errorpage.findViewById(R.id.reload);
        reload.setOnClickListener(view -> {
            if (net.haveNetworkConnection()) {
                loadExperts();
                getProfessions();
                Errorpage.getVisibility();
                Errorpage.setVisibility(View.GONE);
            } else {
                Errorpage.getVisibility();
                Errorpage.setVisibility(View.VISIBLE);
            }
        });
        notification.setAnchor(reload);
        //========================= LOAD OFFLINE JSON ==========================
        if(app.getFirstRun()){
            loadExperts();
            app.setFirstRun(false);
        }else{
            savedHome = app.getHome();
            if (app.getHome() != null && savedHome.length() > 3) {//Data is present
                AppConstants.log(TAG, savedHome);
                try{
                    processHomeData(gson.fromJson(savedHome, HomeModel.class));
                    if (net.haveNetworkConnection()) {
                        loadExperts();
                        //getProfessions();
                    }
                }catch (Exception ex){
                    AppConstants.log(TAG, ex.toString());
                    loadExperts();
                }
            }
            else {
                Errorpage.getVisibility();
                Errorpage.setVisibility(View.VISIBLE);
            }
        }

        mSwipeRefreshLayout = container.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadExperts();
            }
        });

    }


    private void loadExperts() {
        retrofit.getApiService().home(user.getToken())
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
                        if(e instanceof SocketException || e instanceof SocketTimeoutException)
                            msg = "An Internet Error Occurred";
                        if(e instanceof IllegalStateException)
                            msg = "Invalid Response";

                        notification.setMessage(msg);
                        notification.show();
                        Errorpage.getVisibility();
                        Errorpage.setVisibility(View.VISIBLE);
                        AppConstants.log(TAG, e.toString());
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(ResponseObjectModel responseModel) {
                        checkResponse(responseModel);
                        AppConstants.log(TAG, responseModel.toString());
                        HomeModel h = gson.fromJson(responseModel.getData(), HomeModel.class);
                        app.setHome(h);
                        processHomeData(h);
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });
    }

    public void processHomeData(HomeModel home) {
        try {
            populateGrids(home.top_experts, R.id.topExpertsLayout);
            populateGrids(home.closest_experts, R.id.closestExpertsLayout);
            populateGrids(home.featured_experts, R.id.featuredExpertsLayout);
        } catch (Exception ex) {
            AppConstants.log(TAG, ex.toString());
        }
    }

    public void getProfessions() {
        Bridge.post(Request.api + "/professions")
                .asString(new ResponseConvertCallback<String>() {
                    @Override
                    public void onResponse(@NonNull Response response, @Nullable String object, @Nullable BridgeException e) {
                        if (e != null) {
                            int reason = e.reason();
                        } else {
                            app.put("professions", response.asString());
                        }
                    }
                });
    }

    public void populateGrids(List list, int parent) {

        ViewGroup layout = findViewById(parent);
        layout.removeAllViews();

        final LayoutInflater inflater = LayoutInflater.from(this);
        for (int i = 0; i < list.size(); i++) {
            final ExpertModel model = (ExpertModel) list.get(i);

            View expertItem = inflater.inflate(R.layout.item_expert, container, false);
            TextView name = expertItem.findViewById(R.id.txtExpertName);
            TextView profession = expertItem.findViewById(R.id.txtExpertProfession);
            final ImageView expertImage = expertItem.findViewById(R.id.imageViewExpertImage);
            SimpleRatingBar ratingBar = expertItem.findViewById(R.id.expertRating);

            //============== SET VALUES ==============
            name.setText(model.name);
            profession.setText(model.profession);
            ratingBar.setRating(model.averageRating);
//            Glide.with(expertImage.getContext())
//                    .load("http://servizone.net/storage" + model.avatar)
//                    .placeholder(R.drawable.placeholder)
//                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
//                    .into(expertImage);

            Picasso.get()
                    .load(AppConstants.getFileHost() + model.avatar)
                    .fit()
                    .centerCrop()
                    .placeholder(R.drawable.placeholder)
                    .into(expertImage);


            //=================== IMAGE LISTENER ===================
            expertImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(SecondaryActivity.this, ExpertDetailsActivity.class);
                    gson = new Gson();
                    String expertJson = gson.toJson(model);
                    //goToExpert(expertJson);
                }
            });
            layout.addView(expertItem);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            Intent intent = new Intent(SecondaryActivity.this, AboutActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
        }

        if(id == R.id.search){
            Snackbar.make(container, "Not Implemented Yet", Snackbar.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}
