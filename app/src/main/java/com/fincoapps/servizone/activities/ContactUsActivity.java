package com.fincoapps.servizone.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.fincoapps.servizone.R;
import com.fincoapps.servizone.models.ResponseObjectModel;
import com.fincoapps.servizone.utils.AppConstants;
import com.fincoapps.servizone.utils.Notification;

import java.net.SocketException;

import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ContactUsActivity extends BaseActivity {

    private static String TAG = ContactUsActivity.class.getSimpleName();
    Notification notification;
     EditText mMessage;
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(view ->{
            onBackPressed();
            overridePendingTransition(R.anim.trans_right_out, R.anim.trans_right_in);
        });

        notification = new Notification(this);
        notification.setAnchor(layout);
        mMessage = findViewById(R.id.edt_message);
        mSendBtn = findViewById(R.id.btn_send);
        String message = mMessage.getText().toString();
        mSendBtn.setOnClickListener(view -> {
            if(message.isEmpty()){
                notification.setMessage("Can't send empty message");
                notification.show();
            }else if(message.length() < 20){
                notification.setMessage("Message too short");
                notification.show();
            }else {
                sendMessage(message);
            }
        });
    }

    private void sendMessage(String message) {
        pd.setMessage("Processing...");
        pd.show();

        retrofit.getApiService().contactus(user.getToken(), message)
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
                            notification.setMessage(responseModel.getData().toString());
                            notification.show();
                            Toast.makeText(getApplicationContext(), responseModel.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        pd.dismiss();

                    }
                });
    }


}
