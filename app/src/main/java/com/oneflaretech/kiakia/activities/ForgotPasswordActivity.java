package com.oneflaretech.kiakia.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.oneflaretech.kiakia.R;
import com.oneflaretech.kiakia.https.RetrofitClient;
import com.oneflaretech.kiakia.models.ResponseObjectModel;
import com.oneflaretech.kiakia.utils.AppConstants;
import com.oneflaretech.kiakia.utils.CustomLoadingDialog;
import com.oneflaretech.kiakia.utils.Notification;

import java.net.SocketException;
import java.net.SocketTimeoutException;

import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ForgotPasswordActivity extends AppCompatActivity {
    EditText emailmobile, codeText;
    Button forgotbtn, codebtn;
    Notification notification;
    CustomLoadingDialog loader;
    TextView signin, linksinup, resend_code;
    ImageButton btnBack;
    RelativeLayout toolbar;
    LinearLayout emailLayout, codeLayout, mainLayout;
    boolean email_sent = false;
    RetrofitClient retrofit;
    String email, code;
    String TAG = "Forgot Password";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        //Back Button
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(view -> onBackPressed());
        //all Layout
        mainLayout = findViewById(R.id.main_layout);
        emailLayout = findViewById(R.id.email_layout);
        codeLayout = findViewById(R.id.code_layout);

        toolbar = findViewById(R.id.toolbar);
        notification = new Notification(ForgotPasswordActivity.this);
        loader = new CustomLoadingDialog(ForgotPasswordActivity.this);

        //Email Entry
        emailmobile = findViewById(R.id.input_email);
        forgotbtn = findViewById(R.id.forgotbtn);

        //Code Entry
        codeText = findViewById(R.id.codemobile);
        codebtn =  findViewById(R.id.codebutton);


        signin = findViewById(R.id.forgotpass);
        linksinup = findViewById(R.id.link_signup);
        resend_code = findViewById(R.id.resend_code);
        notification.setAnchor(toolbar);
        retrofit = new RetrofitClient(this, AppConstants.getHost());


        //Onclick Listeners
        signin.setOnClickListener(view -> {
            Intent i = new Intent(ForgotPasswordActivity.this, SignInActivity.class);
            startActivity(i);
        });
        linksinup.setOnClickListener(view -> {
            Intent i = new Intent(ForgotPasswordActivity.this, RegistrationActivity.class);
            startActivity(i);
        });
        forgotbtn.setOnClickListener(view -> {
            if (emailmobile.getText().toString().isEmpty()) {

            } else {
                //loader.show();
                email = emailmobile.getText().toString();
                postForgotPassword();
            }
        });
        resend_code.setOnClickListener(view -> postForgotPassword());
        codebtn.setOnClickListener(view -> {
            if(codeText.getText().toString().isEmpty()){
                notification.setMessage("Token Field Is Empty");
                notification.show();
            }else{
                code = codeText.getText().toString();
                verifyToken();
            }
        });
    }

    private void postForgotPassword() {
        loader.show();
//        retrofit.getApiService().forgotPassword(email)
//                .subscribeOn(Schedulers.newThread())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Subscriber<ResponseObjectModel>() {
//                    @Override
//                    public void onCompleted() {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        loader.dismiss();
//                        AppConstants.log(TAG, e.getMessage());
//                        String msg = "An Error Occurred. Please Try Again";
//                        if(e instanceof HttpException)
//                            msg = "A Server Error occurred. Please Try Again";
//                        if(e instanceof SocketException || e instanceof SocketTimeoutException)
//                            msg = "No Internet Connection";
//
//                        notification.setMessage(msg);
//                        notification.show();
//                        AppConstants.log(TAG, e.toString());
//                    }
//
//                    @Override
//                    public void onNext(ResponseObjectModel responseObjectModel) {
//                        AppConstants.log(TAG, responseObjectModel.toString());
//                        loader.dismiss();
//                        if(responseObjectModel.getStatus().equals(AppConstants.STATUS_SUCCESS)){
//                            notification.setType(Notification.SUCCESS);
//                            notification.setMessage(responseObjectModel.getMessage());
//                            notification.show();
//                            showCode();
//                        }else{
//                            notification.setMessage(responseObjectModel.getData());
//                            notification.show();
//                        }
//                    }
//                });
        loader.dismiss();
        showCode();
    }

    private void showCode() {
        codeLayout.setVisibility(View.VISIBLE);
        emailLayout.setVisibility(View.GONE);
    }

    private void verifyToken() {
        retrofit.getApiService().verifyToken(email, code)
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
                        notification.show();
                        AppConstants.log(TAG, e.toString());
                    }

                    @Override
                    public void onNext(ResponseObjectModel responseObjectModel) {
                        AppConstants.log(TAG, responseObjectModel.toString());
                        if(responseObjectModel.getStatus().equals(AppConstants.STATUS_SUCCESS)){
                            AppConstants.log(TAG, responseObjectModel.toString());
                            startActivity((new Intent(ForgotPasswordActivity.this, ChangePasswordActivity.class)).putExtra("token", responseObjectModel.getMessage()));
                            //finish();
                        }else{
                            notification.setMessage(responseObjectModel.getData());
                            notification.show();
                            Toast.makeText(ForgotPasswordActivity.this, responseObjectModel.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        if(codeLayout.getVisibility() == View.VISIBLE){
            codeLayout.setVisibility(View.GONE);
            emailLayout.setVisibility(View.VISIBLE);
        }else{
            super.onBackPressed();
        }
    }
}
