package com.fincoapps.servizone;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.afollestad.bridge.Bridge;
import com.afollestad.bridge.BridgeException;
import com.afollestad.bridge.Form;
import com.afollestad.bridge.ResponseConvertCallback;
import com.fincoapps.servizone.activities.BaseActivity;
import com.fincoapps.servizone.interfaces.Startpage;
import com.fincoapps.servizone.utils.CustomLoadingDialog;
import com.fincoapps.servizone.utils.Notification;
import com.fincoapps.servizone.utils.User;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import static java.lang.System.out;

public class Registration extends BaseActivity {
    EditText name, age, email, password;
    Button register;
    String gender;
    private RadioGroup radioGroupGender;
    private RadioButton radioButtonMale;
    private RadioButton radioButtonFemale;
    ImageButton btnBack;

    int delay = 2000;
    double latitude;
    double logitude;
    User user;
    String accountType = "user";
    TextView registertype;
    Notification notification;
    CustomLoadingDialog loader;
    ScrollView scrollView;
    private FusedLocationProviderClient mFusedLocationClient;

    //    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        scrollView = findViewById(R.id.regdetails);
        loader = new CustomLoadingDialog(Registration.this);
        user = new User(this);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            System.out.println("------------------------------------- " + location.getLongitude());
                            System.out.println("------------------------------------- " + location.getLatitude());
                            logitude = location.getLongitude();
                            latitude = location.getLatitude();
                        }
                    }
                });

        notification = new Notification(Registration.this);
        registertype = findViewById(R.id.registertype);
        name = findViewById(R.id.registername);
        age = findViewById(R.id.registerage);
        email = findViewById(R.id.registeremail);
        password = findViewById(R.id.registerpassword);
        register = findViewById(R.id.registerbtnn);
        radioGroupGender = findViewById(R.id.radio_group_gender);
        radioButtonMale = findViewById(R.id.radioMale);
        radioButtonFemale = findViewById(R.id.radioFemale);
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String realage = age.getText().toString();
//                out.println(Integer.parseInt(realage) + " That is It AgGGGGGGGGGGGGGGGGGGGG");

                if (name.getText().toString().isEmpty() || name.getText().toString().length() < 3) {
                    {
                        name.getError();
                        name.setError("Invalid name");
                    }
                } else if (age.getText().toString().isEmpty() || Integer.parseInt(realage) < 17 || age.getText().length() > 2) {
                    age.getError();
                    age.setError("Age too Young Or Invalid");
                } else if (email.getText().toString().isEmpty() || email.getText().toString().length() < 3 || !email.getText().toString().contains("@")) {
                    email.getError();
                    email.setError("Invalid Email");
                } else if (password.getText().toString().isEmpty() || password.getText().toString().length() < 6) {
                    password.getError();
                    password.setError("Password Must Be More Than Six Characters");
                } else {
                    loader.show();
                    if (radioButtonMale.isChecked()) {
                        gender = "Male";
                    } else {
                        gender = "Female";
                    }
                    Form form = new Form()
                            .add("name", name.getText().toString())
                            .add("age", age.getText().toString())
                            .add("gender", gender)
                            .add("email", email.getText().toString())
                            .add("password", password.getText().toString())
                            .add("longitude", String.valueOf(logitude))
                            .add("latitude", String.valueOf(latitude))
                            .add("type", "user");

                    Bridge.post(com.fincoapps.servizone.utils.Request.api + "/register")
                            .body(form)
                            .asString(new ResponseConvertCallback<String>() {
                                @TargetApi(Build.VERSION_CODES.KITKAT)
                                @Override
                                public void onResponse(@NonNull com.afollestad.bridge.Response response, @Nullable String object, @Nullable BridgeException e) {
                                    loader.hide();
                                    if (e != null) {
                                        int reason = e.reason();
                                        switch (e.reason()) {
                                            case BridgeException.REASON_REQUEST_CANCELLED: {
                                                notification.setMessage("Request was canceled \n could not load Experts");
                                                notification.setAnchor(scrollView);
                                                notification.show();
                                                break;
                                            }
                                            case BridgeException.REASON_REQUEST_TIMEOUT: {
                                                notification.setMessage("Network timed out, try again");
                                                notification.setAnchor(scrollView);
                                                notification.show();
                                                break;
                                            }
                                            case BridgeException.REASON_REQUEST_FAILED: {
                                                notification.setMessage("Network Error \n request failed, try again");
                                                notification.setAnchor(scrollView);
                                                notification.show();
                                                break;
                                            }
                                        }
                                    } else {
                                        System.out.println(response + " THISSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS");
                                        if (response.isSuccess()) {
                                            if (logitude == 0.0 || latitude == 0.0) {
                                                delay = 4000;
                                                notification.setMessage("Your Location Is Disable!!! \n" + "However You can Still Get Through To An Expert");
                                                notification.setType(Notification.WARNING);
                                                notification.setAnchor(scrollView);
                                                notification.show();
                                                final Handler handler = new Handler();
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Intent i = new Intent(Registration.this, Startpage.class);
                                                        startActivity(i);
                                                        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                                                    }
                                                }, delay);

                                            } else {
                                            delay = 2000;
                                            user.storeUser(response.asString());
                                            notification.setMessage("Successfully registered");
                                            notification.setType(Notification.SUCCESS);
                                            notification.setAnchor(scrollView);
                                            notification.show();
                                            final Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Intent i = new Intent(Registration.this, Startpage.class);
                                                    startActivity(i);
                                                    overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                                                }
                                            }, delay);
                                            }
                                        } else {
                                            notification.setMessage("Email has already been taken");
                                            notification.setType(Notification.FAILURE);
                                            notification.setAnchor(scrollView);
                                            notification.show();
                                            out.println("Something Else Is Wrong From The Server and response is not success...." + response.asString());
                                        }
                                    }
                                }
                            });
                }
            }
        });
    }
}