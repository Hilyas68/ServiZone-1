package com.oneflaretech.kiakia.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.oneflaretech.kiakia.R;
import com.oneflaretech.kiakia.https.RetrofitClient;
import com.oneflaretech.kiakia.models.ResponseObjectModel;
import com.oneflaretech.kiakia.models.UserModel;
import com.oneflaretech.kiakia.utils.AppConstants;
import com.oneflaretech.kiakia.utils.CustomLoadingDialog;
import com.oneflaretech.kiakia.utils.Notification;

import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static java.util.Calendar.DATE;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

public class RegistrationActivity extends BaseActivity {
    EditText name, age, email, password, phone;
    Button register;
    String gender;
    Calendar myCalendar;
    ImageView logo;
    private RadioGroup radioGroupGender;
    private RadioButton radioButtonMale;
    private RadioButton radioButtonFemale;
    ImageButton btnBack;
    RetrofitClient retrofitClient;
    private final String TAG = RegistrationActivity.class.getSimpleName();
    int delay = 2000;
    double latitude;
    double logitude;
    public String dateCollected;
    int ageCollected;
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
        logo = findViewById(R.id.logo_image);
        loader = new CustomLoadingDialog(RegistrationActivity.this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        logitude = location.getLongitude();
                        latitude = location.getLatitude();
                        AppConstants.log(TAG, String.valueOf(longitude));
                        AppConstants.log(TAG, String.valueOf(latitude));
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
        btnBack.setOnClickListener( view -> onBackPressed());

        myCalendar = Calendar.getInstance();

        DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
            // TODO Auto-generated method stub
            myCalendar.set(YEAR, year);
            myCalendar.set(MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            AppConstants.log(TAG, myCalendar.toString());
            updateLabel();
        };
        age.setOnClickListener(view -> new DatePickerDialog(RegistrationActivity.this, date, myCalendar
                .get(YEAR), myCalendar.get(MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show());

        register.setOnClickListener(view -> {
            notification.setAnchor(logo);
            if (name.getText().toString().isEmpty() || name.getText().toString().length() < 3) {
                notification.setMessage("Invalid name");
                notification.show();
            } else if (age.getText().toString().isEmpty() || ageCollected < 18) {
                notification.setMessage("You Must Be 18 and Above to Use ServiZone");
                notification.show();
            } else if (email.getText().toString().isEmpty() || email.getText().toString().length() < 3 || !email.getText().toString().contains("@")) {
                notification.setMessage("Invalid Email");
                notification.show();
            } else if (phone.getText().toString().isEmpty() || phone.getText().toString().length() < 9) {
                notification.setMessage("Enter a valid Phone Number");
                notification.show();
            }else if (password.getText().toString().isEmpty() || password.getText().toString().length() < 6) {
                notification.setMessage("Password Must Be More Than Six Characters");
                notification.show();
            } else {
                loader.show();
                if (radioButtonMale.isChecked()) {
                    gender = "Male";
                } else {
                    gender = "Female";
                }
                register(name.getText().toString(), email.getText().toString(), phone.getText().toString(), gender, age.getText().toString() , password.getText().toString(), latitude, longitude);
            }
        });
    }

    private void register(String name, String email, String phoneNumber, String gender, String dob, String password, double latitude, double longitude) {
        if (net.haveNetworkConnection()) {
            retrofitClient = new RetrofitClient(this, AppConstants.getHost());
            retrofitClient.getApiService().register(name, email,dob,phoneNumber, gender , password, latitude, longitude)
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
                                user = gson.fromJson(responseModel.getData(), UserModel.class);
                                AppConstants.log(TAG, user.toString());
                                app.setUser(user);
                                notification.setMessage("Successfully registered");
                                notification.setType(Notification.SUCCESS);
                                notification.setAnchor(scrollView);
                                notification.show();
                                startActivity(new Intent(RegistrationActivity.this, ProfileActivity.class));
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
        dateCollected = sdf.format(myCalendar.getTime());
        age.setText(dateCollected);
        getAge();
        AppConstants.log(TAG, age.getText().toString());
    }

    void getAge(){
        Calendar today = getCalendar(new Date());
        Calendar providedDate = getCalendar(myCalendar.getTime());
        int diff = today.get(YEAR) - providedDate.get(YEAR);
        if (today.get(MONTH) > providedDate.get(MONTH) ||
                (today.get(MONTH) == providedDate.get(MONTH) && today.get(DATE) > providedDate.get(DATE))) {
            diff--;
        }
        ageCollected = diff;
    }

    public static Calendar getCalendar(Date date) {
        Calendar cal = Calendar.getInstance(Locale.US);
        cal.setTime(date);
        return cal;
    }

}