package com.oneflaretech.kiakia.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.oneflaretech.kiakia.R;
import com.oneflaretech.kiakia.experts.ExpertDetailsActivity;
import com.oneflaretech.kiakia.https.NetworkHelper;
import com.oneflaretech.kiakia.https.RetrofitClient;
import com.oneflaretech.kiakia.interfaces.ChooseProfession;
import com.oneflaretech.kiakia.models.ProfessionModel;
import com.oneflaretech.kiakia.models.ResponseObjectModel;
import com.oneflaretech.kiakia.models.UserModel;
import com.oneflaretech.kiakia.utils.AppConstants;
import com.oneflaretech.kiakia.utils.AppSettings;
import com.oneflaretech.kiakia.utils.CustomLoadingDialog;
import com.oneflaretech.kiakia.utils.Notification;
import com.tbruyelle.rxpermissions2.RxPermissions;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class BaseActivity extends AppCompatActivity implements ChooseProfession{

    public double longitude;
    public double latitude;
    public  UserModel user;
    public boolean isLocationOn = false, isLocationPermissionGranted = false;
    public Gson gson;
    public UserModel me;
    public AppSettings app;
    public Notification notification;
    public CustomLoadingDialog loader;
    TextView toolbarTitle;
    private FusedLocationProviderClient mFusedLocationClient;
    public NetworkHelper net;
    private String TAG = "BaseActivity";
    public RetrofitClient retrofit;
    public RxPermissions rx;
    //    @Override
    @SuppressLint("CheckResult")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        net = new NetworkHelper(this);
        app = new AppSettings(this);
        gson = new Gson();
        //===================================== INIT APP CLASSES ===============================
        user = gson.fromJson(app.getUser(), UserModel.class);
        rx = new RxPermissions(this);
        notification = new Notification(this);
        notification.setType(Notification.FAILURE);
        retrofit = new RetrofitClient(this, AppConstants.getHost());
        toolbarTitle = findViewById(com.oneflaretech.kiakia.R.id.toolbarTitle);
        rx
                .request(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                .subscribe(granted -> {
                    if (granted) {
                        isLocationPermissionGranted = true;
                        getLocation();
                    } else {
                        showToast("Location Permission Not Granted");
                    }
                });
    }

    private boolean isAirplaneModeOn() {
        return Settings.System.getInt(getContentResolver(),
                Settings.System.AIRPLANE_MODE_ON, 0) != 0;
    }

    @SuppressLint("MissingPermission")
    public void getLocation(){
        if(isAirplaneModeOn()){
            showToast("Location cannot be determined due to airplane mode being active");
            return;
        }
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        isLocationEnabled();
        if(!isLocationOn){
            showToast("Location Service is not enabled.");
            return;
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            AppConstants.log(TAG,String.valueOf(location.getLatitude()));
                            AppConstants.log(TAG,String.valueOf(location.getLongitude()));
                            longitude = location.getLongitude();
                            latitude = location.getLatitude();
                            if(user != null){
                                user.setLatitude(latitude);
                                user.setLongitude(longitude);
                                app.setUser(user);
                            }
                        }
                    }
                });

    }

    public void isLocationEnabled(){
        boolean gps_enabled = false, network_enabled =false;
        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }catch (Exception ex){AppConstants.log(TAG, ex.toString());}
        try{
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }catch (Exception ex){AppConstants.log(TAG, ex.toString());}

        if(!gps_enabled && !network_enabled){
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setCancelable(false);
            dialog.setMessage("Location service is not enabled")
            .setPositiveButton("Enable Location", (paramDialogInterface, paramInt) -> {
                isLocationOn = true;
                paramDialogInterface.dismiss();
                Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(myIntent);

            })
            .setNegativeButton("Cancel", (paramDialogInterface, paramInt) -> {
                isLocationOn = false;
                paramDialogInterface.dismiss();
            }).show();
        }else{
            isLocationOn = true;
        }
    }

    @Override
    public void selectProfession(ProfessionModel p) {

    }


    public void setTitle(String title){
        toolbarTitle.setText(title);
    }


    public void goToExpert(String expertJson){
        Intent i = new Intent(this, ExpertDetailsActivity.class);
        i.putExtra("expert", expertJson);
        startActivity(i);
        overridePendingTransition(com.oneflaretech.kiakia.R.anim.trans_right_in, com.oneflaretech.kiakia.R.anim.trans_right_out);
    }

    public void goToScreen(Activity activity){
        Intent i = new Intent(this, activity.getClass());
        startActivity(i);
        overridePendingTransition(com.oneflaretech.kiakia.R.anim.trans_right_in, com.oneflaretech.kiakia.R.anim.trans_right_out);
    }

    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(com.oneflaretech.kiakia.R.anim.pop_enter, com.oneflaretech.kiakia.R.anim.pop_exit);
    }

    public void checkResponse(ResponseObjectModel responseObjectModel){
        if(responseObjectModel.getStatus().equals(AppConstants.STATUS_UNAUTHORIZED_USER)){
            logOut();
        }
    }

    void logOut(){
        AppConstants.log(TAG, user.getToken());
        AppConstants.log(TAG, app.getUser());
        retrofit.getApiService().logout(user.getToken())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResponseObjectModel>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(ResponseObjectModel responseModel) {
                        AppConstants.log(TAG, responseModel.toString());
                    }
                });

        app.clear();
        startActivity(new Intent(BaseActivity.this, SignInActivity.class));
        finish();
        overridePendingTransition(R.anim.trans_right_out, R.anim.trans_right_in);
    }

    void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

}