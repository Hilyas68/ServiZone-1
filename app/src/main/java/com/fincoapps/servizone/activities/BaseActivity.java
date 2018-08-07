package com.fincoapps.servizone.activities;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.afollestad.bridge.Bridge;
import com.afollestad.bridge.BridgeException;
import com.afollestad.bridge.MultipartForm;
import com.afollestad.bridge.ResponseConvertCallback;
import com.fincoapps.servizone.adapters.ProfessionsAdapter;
import com.fincoapps.servizone.experts.ExpertDetailsActivity;
import com.fincoapps.servizone.https.NetworkHelper;
import com.fincoapps.servizone.https.RetrofitClient;
import com.fincoapps.servizone.interfaces.ChooseProfession;
import com.fincoapps.servizone.models.ProfessionModel;
import com.fincoapps.servizone.models.UserModel;
import com.fincoapps.servizone.utils.AppConstants;
import com.fincoapps.servizone.utils.AppSettings;
import com.fincoapps.servizone.utils.CustomLoadingDialog;
import com.fincoapps.servizone.utils.Notification;
import com.fincoapps.servizone.utils.User;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.OnClick;
import butterknife.Optional;

import static android.support.v4.app.ActivityCompat.requestPermissions;
import static java.lang.System.out;

public class BaseActivity extends AppCompatActivity implements ChooseProfession{

    public double longitude;
    public double latitude;
    public  User user;
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
    //    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        net = new NetworkHelper(this);
        app = new AppSettings(this);
        gson = new Gson();
        //===================================== INIT APP CLASSES ===============================
        user = gson.fromJson(app.getUser(), User.class);
        //try{me = user.getUserModel();}catch (Exception ex){user.logOut();}
        notification = new Notification(this);
        retrofit = new RetrofitClient(this, AppConstants.getHost());
        toolbarTitle = (TextView)findViewById(com.fincoapps.servizone.R.id.toolbarTitle);
    }

    public void getLocation(){
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            out.println("NO PERMISSION");
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
                            user.setLatitude(latitude);
                            user.setLongitude(longitude);
                            app.setUser(user);
                        }
                    }
                });

    }

    @Override
    public void selectProfession(ProfessionModel p) {

    }


    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults){
        switch(permsRequestCode){
            case 200:
                boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
    }


    public void setTitle(String title){
        toolbarTitle.setText(title);
    }

    public void goToExpert(String expertJson){
        Intent i = new Intent(this, ExpertDetailsActivity.class);
        i.putExtra("expert", expertJson);
        startActivity(i);
        overridePendingTransition(com.fincoapps.servizone.R.anim.trans_right_in, com.fincoapps.servizone.R.anim.trans_right_out);
    }

    public void goToScreen(Activity activity){
        Intent i = new Intent(this, activity.getClass());
        startActivity(i);
        overridePendingTransition(com.fincoapps.servizone.R.anim.trans_right_in, com.fincoapps.servizone.R.anim.trans_right_out);
    }

    @Optional
    @OnClick(com.fincoapps.servizone.R.id.btnBack)
    public void onClick(View view) {
        out.println("=================");
        onBackPressed();
    }


    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(com.fincoapps.servizone.R.anim.pop_enter, com.fincoapps.servizone.R.anim.pop_exit);
    }
}