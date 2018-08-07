package com.fincoapps.servizone.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.fincoapps.servizone.ForgotPassword;
import com.fincoapps.servizone.R;
import com.fincoapps.servizone.Registration;
import com.fincoapps.servizone.https.RetrofitClient;
import com.fincoapps.servizone.models.ResponseObjectModel;
import com.fincoapps.servizone.utils.AppConstants;
import com.fincoapps.servizone.utils.Notification;
import com.fincoapps.servizone.utils.User;

import java.net.SocketException;

import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SignInActivity extends BaseActivity {
    EditText email, password;
    AppCompatButton login;
    TextView signup, forgotpass;
    ImageButton btnBack;
    ImageView logo;
    Notification notification;
    String url = "http://servizone.net/api/login";
    ProgressDialog pd;
    RetrofitClient retrofitClient;
    String TAG = "SignInActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        //relativeLayout = findViewById(R.id.headertoolbar);
        email = findViewById(R.id.input_email);
        password = findViewById(R.id.input_password);
        login = findViewById(R.id.btn_login);
        logo = findViewById(R.id.logo);
        signup = findViewById(R.id.link_signup);
        forgotpass = findViewById(R.id.forgotpass);
        btnBack = findViewById(R.id.btnBack);
        pd = new ProgressDialog(this);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage("Signing In...");
        notification = new Notification(this);

        btnBack.setOnClickListener(view -> onBackPressed());

        forgotpass.setOnClickListener(view -> {
            Intent intent = new Intent(SignInActivity.this, ForgotPassword.class);
            startActivity(intent);
            overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
        });

        signup.setOnClickListener(view -> {
            Intent intent = new Intent(SignInActivity.this, Registration.class);
            startActivity(intent);
            overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
        });

        login.setOnClickListener(view -> {
            notification.setAnchor(logo);
            if (email.getText().toString().equals("") && password.getText().toString().equals("")) {
                notification.setMessage("Email or password is empty");
                notification.show();
            } else if (!email.getText().toString().contains("@")) {
                notification.setMessage("Email is too short");
                notification.show();
            } else if (password.getText().toString().length() < 4) {
                notification.setMessage("Password is too short");
                notification.show();
            } else {
                pd.show();
                postLogin(email.getText().toString(),password.getText().toString());
            }
        });

    }

    private void postLogin(String email, String password) {
        if(net.haveNetworkConnection()){
            retrofitClient = new RetrofitClient(this, AppConstants.getHost());
            retrofitClient.getApiService().login(email, password)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<ResponseObjectModel> () {
                        @Override
                        public void onCompleted() {
                            pd.dismiss();
                        }

                        @Override
                        public void onError(Throwable e) {
                            String msg = "An Error Occurred. Please Try Again";
                            if(e instanceof HttpException)
                                msg = "A Server Error occurred. Please Try Again";
                            if(e instanceof SocketException)
                                msg = "No Internet Connection";

                            notification.setMessage(msg);
                            notification.show();
                            AppConstants.log(TAG, e.toString());
                            pd.dismiss();
                        }

                        @Override
                        public void onNext(ResponseObjectModel responseModel) {
                            if(responseModel.getStatus().equals(AppConstants.STATUS_SUCCESS)){
                                AppConstants.log(TAG, responseModel.toString());
                                User user = gson.fromJson(responseModel.getData().toString(), User.class);
                                AppConstants.log(TAG, user.toString());
                                app.setUser(user);
                                startActivity(new Intent(SignInActivity.this, MainActivity.class));
                                finish();
                                overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                            }else{
                                notification.setMessage(responseModel.getMessage());
                                notification.show();
                                pd.dismiss();
                            }


                        }
                    });
        }else {
            notification.setMessage("No Internet Connection");
            notification.show();
        }
    }

}
