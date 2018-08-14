package com.oneflaretech.kiakia.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.oneflaretech.kiakia.R;
import com.oneflaretech.kiakia.models.ResponseObjectModel;
import com.oneflaretech.kiakia.utils.AppConstants;
import com.oneflaretech.kiakia.utils.Notification;

import java.net.SocketException;
import java.net.SocketTimeoutException;

import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ContactUsActivity extends BaseActivity {

    private String TAG = ContactUsActivity.class.getSimpleName();
    Notification notification;
     EditText mMessage, mSubject;
     AppCompatButton mSendBtn;
     LinearLayout layout;
     Toolbar toolbar;
     ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        layout = findViewById(R.id.layout);
        pd = new ProgressDialog(this);
        toolbar = findViewById(R.id.cont_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(view ->{
            onBackPressed();
            overridePendingTransition(R.anim.trans_right_out, R.anim.trans_right_in);
        });

        notification = new Notification(this);
        notification.setAnchor(toolbar);
        mMessage = findViewById(R.id.message);
        mSubject = findViewById(R.id.subject);
        mSendBtn = findViewById(R.id.btn_send);
        mSendBtn.setOnClickListener(view -> {
            String message = mMessage.getText().toString();
            String subject = mSubject.getText().toString();
            if(message.isEmpty()){
                notification.setMessage("Can't send empty message");
                notification.show();
            }else if(subject.isEmpty()){
                notification.setMessage("Subject field is empty");
                notification.show();
            }else {
                sendMessage(subject, message);
            }
        });
    }

    private void sendMessage(String subject, String message) {
        pd.setMessage("Processing...");
        pd.show();

        if(net.haveNetworkConnection())
            retrofit.getApiService().contactus(user.getToken(), subject, message)
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
                            if(e instanceof SocketException || e instanceof SocketTimeoutException)
                                msg = "No Internet Connection";

                            notification.setMessage(msg);
                            notification.show();
                            AppConstants.log(TAG, e.toString());
                            pd.dismiss();
                        }

                        @Override
                        public void onNext(ResponseObjectModel responseModel) {
                            checkResponse(responseModel);
                            AppConstants.log(TAG, responseModel.toString());
                            if(responseModel.getStatus().equals(AppConstants.STATUS_ERROR)){
                                Snackbar.make(layout, responseModel.getMessage(), Snackbar.LENGTH_SHORT).show();
                                notification.setMessage(responseModel.getData());
                                notification.show();
                            }else{
                                notification.setMessage(responseModel.getMessage());
                                notification.show();
                                mMessage.getText().clear();
                                mSubject.getText().clear();
                                Toast.makeText(getApplicationContext(), responseModel.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            pd.dismiss();

                        }
                    });
        else{
            notification.setMessage("No Internet Connection");
            notification.show();
        }
    }


}
