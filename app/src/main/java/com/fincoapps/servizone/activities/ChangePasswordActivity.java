package com.fincoapps.servizone.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.fincoapps.servizone.R;
import com.fincoapps.servizone.models.ResponseObjectModel;
import com.fincoapps.servizone.utils.AppConstants;
import com.fincoapps.servizone.utils.CustomLoadingDialog;
import com.fincoapps.servizone.utils.Notification;

import java.net.SocketException;

import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ChangePasswordActivity extends BaseActivity {
    Notification notification;
    CustomLoadingDialog loader;
    EditText current_password, new_password, confirm_new_password;
    LinearLayout errorsuccesspage;
    AppCompatButton btn_changepass;
    ProgressDialog pd;
    String TAG = "ChangePasswordActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        pd = new ProgressDialog(this);
        Toolbar toolbar = findViewById(R.id.newToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        toolbar.setNavigationOnClickListener(view ->{
            onBackPressed();
            overridePendingTransition(R.anim.trans_right_out, R.anim.trans_right_in);
        });

        notification = new Notification(ChangePasswordActivity.this);
        notification.setAnchor(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        errorsuccesspage = findViewById(R.id.errorsuccesspage);
        btn_changepass = findViewById(R.id.btn_changepass);
        current_password = findViewById(R.id.current_password);
        new_password = findViewById(R.id.new_password);
        confirm_new_password = findViewById(R.id.confirm_new_password);
        btn_changepass.setOnClickListener(view -> {
            if (current_password.getText().toString().isEmpty() || new_password.getText().toString().isEmpty() || confirm_new_password.getText().toString().isEmpty()) {
                notification.setMessage("Empty Field(s)");
                notification.show();
            }
            else if(new_password.getText().toString().length() < 6 || confirm_new_password.getText().toString().length() < 6){
                notification.setMessage("Minimum of 6 character is required!");
                notification.show();
            }
            else if (!confirm_new_password.getText().toString().equals(new_password.getText().toString())) {
                notification.setMessage("Password Mismatch");
                notification.show();
            } else {
                notification.setAnchor(toolbar);
                pd.setMessage("Processing...");
                pd.show();
                retrofit.getApiService().changePassword(user.getToken(),current_password.getText().toString().trim(), new_password.getText().toString().trim())
                        .subscribeOn(Schedulers.newThread())
                        .unsubscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<ResponseObjectModel>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                AppConstants.log(TAG, e.getMessage());
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
                                AppConstants.log(TAG, responseModel.toString());
                                if(responseModel.getStatus().equals(AppConstants.STATUS_ERROR)){
                                    notification.setMessage(responseModel.getData().toString());
                                    notification.show();
                                }else{
                                    Toast.makeText(ChangePasswordActivity.this, responseModel.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                                pd.dismiss();

                            }
                        });
            }
        });
    }

    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.pop_enter, R.anim.pop_exit);
    }
}
