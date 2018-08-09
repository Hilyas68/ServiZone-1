package com.fincoapps.servizone.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.fincoapps.servizone.ProfilePictureActivity;
import com.fincoapps.servizone.R;
import com.fincoapps.servizone.https.RetrofitClient;
import com.fincoapps.servizone.interfaces.ChooseProfession;
import com.fincoapps.servizone.models.ResponseObjectModel;
import com.fincoapps.servizone.models.UserModel;
import com.fincoapps.servizone.utils.AppConstants;
import com.fincoapps.servizone.utils.AppSettings;
import com.fincoapps.servizone.utils.CustomLoadingDialog;
import com.fincoapps.servizone.utils.Notification;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static java.util.Calendar.DATE;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

public class ProfileActivity extends BaseActivity implements ChooseProfession {

    EditText name, age;
    TextView gender;
    private Notification notification;
    private ImageView avatar;
    private EditText email;
    private EditText mobile;
    private RadioGroup radioGroupGender;
    private RadioButton radioButtonMale, radioButtonFemale;
    CollapsingToolbarLayout collapsingToolbarLayout;
    //New Var
    ProgressBar progressBar;
    String TAG = "ProfileActivity";
    Calendar myCalendar;
    AppCompatButton update;
    String genderString;
    public String dateCollected;
    int ageCollected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        loader = new CustomLoadingDialog(this);

        collapsingToolbarLayout = findViewById(R.id.toolbar_layout);
        collapsingToolbarLayout.setTitle("Profile");
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));

        progressBar = findViewById(R.id.progress);
        AppConstants.log(TAG, user.toString());
        collapsingToolbarLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, ProfilePictureActivity.class).putExtra("imageUrl", user.getAvatar()));
            }
        });

        //============================ INIT APP CLASSES ==================================
        app = new AppSettings(this);
        notification = new Notification(this);
        notification.setAnchor(collapsingToolbarLayout);
        loader = new CustomLoadingDialog(this);

        init();
    }

    private void init() {
        FloatingActionButton fab = findViewById(R.id.upload_btn);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent,
                    "Select Picture"), 1);
        });
        avatar = findViewById(R.id.userAvatar);
        getAvatar();
        name = findViewById(R.id.name);
        age = findViewById(R.id.age);
        myCalendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
            // TODO Auto-generated method stub
            myCalendar.set(YEAR, year);
            myCalendar.set(MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            AppConstants.log(TAG, myCalendar.toString());
            updateLabel();
        };
        age.setOnClickListener(view -> new DatePickerDialog(ProfileActivity.this, date, myCalendar
                .get(YEAR), myCalendar.get(MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show());


        email = findViewById(R.id.email);
        mobile = findViewById(R.id.phone);
        radioGroupGender = findViewById(R.id.radio_group_gender);
        radioButtonMale = findViewById(R.id.radioMale);
        radioButtonFemale = findViewById(R.id.radioFemale);
        update = findViewById(R.id.update);
        update.setOnClickListener(view -> doUpdate());
        getProfileDetails();
    }


    public void getProfileDetails() {
        name.setText(user.getName());
        age.setText(user.getAge());
        email.setText(user.getEmail());
        mobile.setText(user.getMobile());
        try{
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date date = sdf. parse(user.getAge());
            myCalendar.setTime(date);
            updateLabel();
        }catch (Exception e){AppConstants.log(TAG, e.getMessage());}
        if (user.getGender().equalsIgnoreCase("Male")) {
            radioButtonMale.setChecked(true);
        } else {
            radioButtonFemale.setChecked(true);
        }

    }

    private void doUpdate() {
        if (name.getText().toString().isEmpty() || name.getText().toString().length() < 3) {
            notification.setMessage("Invalid name");
            notification.show();
        } else if (age.getText().toString().isEmpty() || ageCollected < 18) {
            notification.setMessage("You Must Be 18 and Above to Use ServiZone");
            notification.show();
        } else if (email.getText().toString().isEmpty() || email.getText().toString().length() < 3 || !email.getText().toString().contains("@")) {
            notification.setMessage("Invalid Email");
            notification.show();
        } else if (mobile.getText().toString().isEmpty() || mobile.getText().toString().length() < 9) {
            notification.setMessage("Enter a valid Phone Number");
            notification.show();
        } else {
            loader.show();
            if (radioButtonMale.isChecked()) {
                genderString = "Male";
            } else {
                genderString = "Female";
            }
            postUpdate(name.getText().toString(), mobile.getText().toString(), genderString, age.getText().toString(), latitude, longitude);
        }
    }

    private void postUpdate(String name, String mobile, String gender, String dob, double latitude, double longitude) {
        if (net.haveNetworkConnection()) {
            retrofit = new RetrofitClient(this, AppConstants.getHost());
            retrofit.getApiService().profileUpdate(user.getToken(), name,dob,mobile, gender, latitude, longitude)
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
                            if (e instanceof SocketException || e instanceof SocketTimeoutException)
                                msg = "No Internet Connection";

                            notification.setMessage(msg);
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
                                notification.setMessage("Successfully Updated");
                                notification.setType(Notification.SUCCESS);
                                notification.show();
                            }else{
                                notification.setMessage(responseModel.getData());
                                notification.show();
                                loader.dismiss();
                            }
                        }


                    });
        } else {
            notification.setMessage("No Internet Connection");
            notification.show();
            loader.dismiss();
        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                Uri selectedImageUri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                    String filePath = getRealPathFromURIPath(selectedImageUri);
                    File file = new File(filePath);
                    AppConstants.log(TAG + "filename", file.getName());
                    RequestBody mFile = RequestBody.create(MediaType.parse("image/*"), file);
                    MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("avatar", file.getName(), mFile);
                    RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), file.getName());
                    avatar.setImageBitmap(bitmap);
                    uploadImage(filename, fileToUpload);
                } catch (IOException e) {
                    notification.setMessage("Unable to pick picture");
                    notification.show();
                }
            }
        }
    }

    private void uploadImage(RequestBody filename, MultipartBody.Part fileToUpload) {
        progressBar.setVisibility(View.VISIBLE);
        RequestBody token = RequestBody.create(MediaType.parse("text/plain"), user.getToken());
        Toast.makeText(ProfileActivity.this, "Uploading Image", Toast.LENGTH_SHORT).show();
        retrofit.getApiService().uploadImage(token, fileToUpload, filename)
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
                            msg = "A Server Error occurred. Please Try Again";
                        if(e instanceof SocketException || e instanceof SocketTimeoutException)
                            msg = "No Internet Connection";

                        notification.setMessage(msg);
                        notification.show();
                        progressBar.setVisibility(View.INVISIBLE);
                        AppConstants.log(TAG, e.toString());
                    }
                    @Override
                    public void onNext(ResponseObjectModel responseModel) {

                        if(responseModel.getStatus().equals(AppConstants.STATUS_SUCCESS)){
                            AppConstants.log(TAG, responseModel.toString());
                            user.setAvatar(responseModel.getData().replace('"', ' ').trim());
                            app.setUser(user);
                        }else{
                            notification.setMessage(responseModel.getMessage());
                            notification.show();
                        }
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
    }

    //===================== GET PROFILE DETAILS REQUEST ====================


    private String getRealPathFromURIPath(Uri contentURI) {
        Cursor cursor =  getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }

    void getAvatar(){
        progressBar.setVisibility(View.VISIBLE);
        Picasso.get()
                .load(AppConstants.getFileHost() + user.getAvatar())
                .fit()
                .centerCrop()
                .placeholder(R.drawable.noimage)
                .into(avatar, new Callback() {
                    @Override
                    public void onSuccess() {
                        AppConstants.log(TAG,"Image Loaded");
                        progressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onError(Exception e) {
                        AppConstants.log(TAG,e.getMessage());
                        e.printStackTrace();
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });

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
