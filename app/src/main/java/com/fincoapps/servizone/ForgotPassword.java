package com.fincoapps.servizone;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.bridge.Bridge;
import com.afollestad.bridge.BridgeException;
import com.afollestad.bridge.Form;
import com.afollestad.bridge.ResponseConvertCallback;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.fincoapps.servizone.interfaces.Startpage;
import com.fincoapps.servizone.utils.CustomLoadingDialog;
import com.fincoapps.servizone.utils.Notification;

import org.json.JSONException;

import static java.lang.System.out;

public class ForgotPassword extends AppCompatActivity {
    EditText emailmobile;
    Button forgotbtn;
    Notification notification;
    CustomLoadingDialog loader;
    LinearLayout errorandsuccess;
    TextView forgotpass, linksinup;
    ImageButton btnBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        notification = new Notification(ForgotPassword.this);
        loader = new CustomLoadingDialog(ForgotPassword.this);
        errorandsuccess = findViewById(R.id.errorandsuccess);

        emailmobile = findViewById(R.id.input_email);
        forgotbtn = findViewById(R.id.forgotbtn);
        forgotpass = findViewById(R.id.forgotpass);
        linksinup = findViewById(R.id.link_signup);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        forgotpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ForgotPassword.this, Signin.class);
                startActivity(i);
            }
        });

        linksinup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ForgotPassword.this, Registration.class);
                startActivity(i);
            }
        });

        forgotbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (emailmobile.getText().toString().isEmpty()) {
                    emailmobile.getError();
                    emailmobile.setError("Empty");
                } else {
                    loader.show();
                    Form form = new Form()
                            .add("email", emailmobile.getText().toString());

                    Bridge.post(com.fincoapps.servizone.utils.Request.api + "/forgot")
                            .body(form)
                            .asString(new ResponseConvertCallback<String>() {
                                @TargetApi(Build.VERSION_CODES.KITKAT)
                                @Override
                                public void onResponse(@NonNull com.afollestad.bridge.Response response, @Nullable String object, @Nullable BridgeException e) {
                                    loader.hide();
                                    if (e != null) {
                                        int reason = e.reason();
                                        System.out.println("================== ERROR ==================== " + e + " && " + reason);
                                        notification.setMessage("Network Timed Out");
                                        notification.setType(Notification.WARNING);
                                        notification.setAnchor(errorandsuccess);
                                        notification.show();
                                    } else {
                                        System.out.println(response + " THISSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS");
                                        if (response.isSuccess()) {
                                            if(response.asString().contains("Invalid Email")){
                                                notification.setMessage("Invalid Email Address");
                                                notification.setType(Notification.FAILURE);
                                                notification.setAnchor(errorandsuccess);
                                                notification.show();
                                            }else if(response.asString().contains("Successfully")){
                                            notification.setMessage("Successfully: Check Your Email For Instructions");
                                            notification.setType(Notification.SUCCESS);
                                            notification.setAnchor(errorandsuccess);
                                            notification.show();
                                            }else{System.out.println("What Else Could Be Wrong??? " + response.asString());}
                                        } else {
                                            out.println("Something Else Is Wrong From The Server and response is not success....");
                                            notification.setMessage("Server Error, Contact Servizone For Resolve");
                                            notification.setType(Notification.WARNING);
                                            notification.setAnchor(errorandsuccess);
                                            notification.show();
                                        }
                                    }
                                }
                            });
                }
            }
        });
    }
}
