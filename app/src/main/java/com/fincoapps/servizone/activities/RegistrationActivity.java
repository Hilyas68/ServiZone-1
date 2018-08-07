package com.fincoapps.servizone.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.fincoapps.servizone.R;
import com.fincoapps.servizone.https.RetrofitClient;
import com.fincoapps.servizone.interfaces.Startpage;
import com.fincoapps.servizone.models.ResponseObjectModel;
import com.fincoapps.servizone.utils.AppConstants;
import com.fincoapps.servizone.utils.CustomLoadingDialog;
import com.fincoapps.servizone.utils.Notification;
import com.fincoapps.servizone.utils.User;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RegistrationActivity extends BaseActivity {
    EditText name, age, email, password, phone;
    Button register;
    String gender;
    Calendar myCalendar;
    private RadioGroup radioGroupGender;
    private RadioButton radioButtonMale;
    private RadioButton radioButtonFemale;
    ImageButton btnBack;
    RetrofitClient retrofitClient;
    private final String TAG = RegistrationActivity.class.getSimpleName();
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
        loader = new CustomLoadingDialog(RegistrationActivity.this);
        myCalendar = Calendar.getInstance();

        DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            AppConstants.log(TAG, myCalendar.toString());
            updateLabel();
        };
        age.setOnClickListener(view -> {
            new DatePickerDialog(RegistrationActivity.this, date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
        });
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

        notification = new Notification(RegistrationActivity.this);
        registertype = findViewById(R.id.registertype);
        name = findViewById(R.id.registername);
        age = findViewById(R.id.registerage);
        email = findViewById(R.id.registeremail);
        phone = findViewById(R.id.registerphone);
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

                if (name.getText().toString().isEmpty() || name.getText().toString().length() < 3) {
                    {
                        name.getError();
                        name.setError("Invalid name");
                    }
                } else if (age.getText().toString().isEmpty() || Integer.parseInt(realage) < 18 || age.getText().length() > 2) {
                    age.getError();
                    age.setError("Age too Young Or Invalid");
                } else if (email.getText().toString().isEmpty() || email.getText().toString().length() < 3 || !email.getText().toString().contains("@")) {
                    email.getError();
                    email.setError("Invalid Email");
                } else if (phone.getText().toString().isEmpty() || phone.getText().toString().length() < 9) {
                    phone.getError();
                    phone.setError("Enter a valid Phone Number");
                }else if (password.getText().toString().isEmpty() || password.getText().toString().length() < 6) {
                    password.getError();
                    password.setError("Password Must Be More Than Six Characters");
                } else {
                    loader.show();
                    if (radioButtonMale.isChecked()) {
                        gender = "Male";
                    } else {
                        gender = "Female";
                    }
                    //register(name.getText().toString(), email.getText().toString(), phone.getText().toString(), gender, age.getText().toString() , password.getText().toString());
                }
            }
        });
    }

    private void register(String name, String email, String phoneNumber, String gender, String dob, String password) {
        if (net.haveNetworkConnection()) {
            retrofitClient = new RetrofitClient(this, AppConstants.getHost());
            retrofitClient.getApiService().register(name, email,dob,phoneNumber, gender , password)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<ResponseObjectModel>() {
                        @Override
                        public void onCompleted() {
                            loader.dismiss();

                        }

                        @Override
                        public void onError(Throwable e) {
                            String msg = "An Error Occurred. Please Try Again";
                            if (e instanceof HttpException)
                                msg = "A Server Error occurred. Please Try Again";
                            if (e instanceof SocketException)
                                msg = "No Internet Connection";

                            notification.setMessage(msg);
                            notification.setAnchor(scrollView);
                            notification.show();
                            AppConstants.log(TAG, e.toString());
                            loader.dismiss();
                        }

                        @Override
                        public void onNext(ResponseObjectModel responseModel) {
                            if(responseModel.getStatus().equals(AppConstants.STATUS_SUCCESS)) {
                                User user = gson.fromJson(responseModel.getData(), User.class);
                                AppConstants.log(TAG, user.toString());
                                app.setUser(user);
                                notification.setMessage("Successfully registered");
                                notification.setType(Notification.SUCCESS);
                                notification.setAnchor(scrollView);
                                notification.show();
                                startActivity(new Intent(RegistrationActivity.this, Startpage.class));
                                finish();
                                overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                            }else{
                                notification.setMessage(responseModel.getData());
                                notification.setAnchor(scrollView);
                                notification.show();
                                loader.dismiss();
                            }
                        }


                    });
        } else {
            notification.setMessage("No Internet Connection");
            notification.setAnchor(scrollView);
            notification.show();
            loader.dismiss();
        }
    }

    private void updateLabel() {
        String myFormat = "dd/MM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());

        age.setText(sdf.format(myCalendar.getTime()));
        AppConstants.log(TAG, age.getText().toString());
    }
}