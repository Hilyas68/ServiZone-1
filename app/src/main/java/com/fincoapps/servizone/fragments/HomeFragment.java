package com.fincoapps.servizone.fragments;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.fincoapps.servizone.R;
import com.fincoapps.servizone.experts.ExpertDetailsActivity;
import com.fincoapps.servizone.https.NetworkHelper;
import com.fincoapps.servizone.https.RetrofitClient;
import com.fincoapps.servizone.models.ExpertModel;
import com.fincoapps.servizone.models.HomeModel;
import com.fincoapps.servizone.utils.AppConstants;
import com.fincoapps.servizone.utils.AppSettings;
import com.fincoapps.servizone.utils.Notification;
import com.fincoapps.servizone.utils.Request;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;

import org.jetbrains.annotations.NotNull;

import java.net.SocketException;
import java.util.List;

import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    String TAG = "HomeFragment";
    View v;
    RelativeLayout Errorpage;
    SwipeRefreshLayout mSwipeRefreshLayout = null;
    AppSettings app;
    LinearLayout container;
    private String savedHome;
    NetworkHelper net;
    Gson gson;
    Notification notification;
    RetrofitClient retrofitClient;
    private FusedLocationProviderClient mFusedLocationClient;

    public HomeFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public HomeFragment(AppSettings appSettings) {
        this.app = appSettings;
        gson = new Gson();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull  LayoutInflater inflater, ViewGroup viewGroup,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_home, viewGroup, false);
        AppConstants.log(TAG, app.getUser());
        net = new NetworkHelper(getActivity());
        container = v.findViewById(R.id.mainContent);
        notification = new Notification(getActivity());
        Errorpage = container.findViewById(R.id.Errorpage);
        TextView reload = Errorpage.findViewById(R.id.reload);
        retrofitClient = new RetrofitClient(getContext(), AppConstants.getHost());
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

                reload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (net.haveNetworkConnection()) {
                            loadExperts();
                            getProfessions();
                            Errorpage.getVisibility();
                            Errorpage.setVisibility(View.GONE);
                        } else {
                            Errorpage.getVisibility();
                            Errorpage.setVisibility(View.GONE);
                            Errorpage.setVisibility(View.VISIBLE);
                        }
                    }
                });

            }
        }

        mSwipeRefreshLayout = container.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadExperts();
            }
        });


        return v;
    }



    private void loadExperts() {
        container = v.findViewById(R.id.mainContent);
        Errorpage = container.findViewById(R.id.Errorpage);
        final TextView reload = Errorpage.findViewById(R.id.reload);
        retrofitClient.getApiService().home()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<HomeModel>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        String msg = "An Error Occurred. Please Try Again";
                        if(e instanceof HttpException)
                            msg = "No Internet Connection";
                        if(e instanceof SocketException)
                            msg = "An Internet Error Occurred";

                        notification.setMessage(msg);
                        notification.show();
                        Errorpage.getVisibility();
                        Errorpage.setVisibility(View.VISIBLE);
                        AppConstants.log(TAG, e.toString());
                    }

                    @Override
                    public void onNext(HomeModel responseModel) {
                        app.setHome(responseModel);
                        processHomeData(responseModel);
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

        ViewGroup layout = v.findViewById(parent);
        layout.removeAllViews();

        final LayoutInflater inflater = LayoutInflater.from(getActivity());
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
            Glide.with(expertImage.getContext())
                    .load("http://servizone.net/storage" + model.avatar)
                    .placeholder(R.drawable.placeholder)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(expertImage);

            //=================== IMAGE LISTENER ===================
            expertImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), ExpertDetailsActivity.class);
                    gson = new Gson();
                    String expertJson = gson.toJson(model);
                    //goToExpert(expertJson);
                }
            });
            layout.addView(expertItem);
        }
    }

    public void populatelocation() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            AppConstants.log(TAG, String.valueOf(location.getLongitude()));
                            AppConstants.log(TAG, String.valueOf(location.getLatitude()));
                            //longitude = location.getLongitude();
//                            latitude = location.getLatitude();
                        }
                    }
                });
    }
}
