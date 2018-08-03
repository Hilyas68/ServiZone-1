package com.fincoapps.servizone.activities;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.bridge.Bridge;
import com.afollestad.bridge.BridgeException;
import com.afollestad.bridge.Form;
import com.afollestad.bridge.ResponseConvertCallback;
import com.fincoapps.servizone.ForgotPassword;
import com.fincoapps.servizone.MainActivity;
import com.fincoapps.servizone.R;
import com.fincoapps.servizone.Registration;
import com.fincoapps.servizone.utils.CustomLoadingDialog;
import com.fincoapps.servizone.utils.Notification;
import com.fincoapps.servizone.utils.ResponseUtility;
import com.fincoapps.servizone.utils.User;

import org.jetbrains.annotations.Nullable;

public class SignInActivity extends BaseActivity {
    EditText email, password;
    AppCompatButton login;
    TextView signup, forgotpass;
    RelativeLayout relativeLayout;
    ImageButton btnBack;
    CustomLoadingDialog loadingDialog;
    Notification notification;
    String url = "http://servizone.net/api/login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        relativeLayout = findViewById(R.id.headertoolbar);

        email = findViewById(R.id.input_email);
        password = findViewById(R.id.input_password);
        login = findViewById(R.id.btn_login);
        signup = findViewById(R.id.link_signup);
        forgotpass = findViewById(R.id.forgotpass);
        btnBack = findViewById(R.id.btnBack);

        loadingDialog = new CustomLoadingDialog(this);
        notification = new Notification(this);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        forgotpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignInActivity.this, ForgotPassword.class);
                startActivity(intent);
                overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignInActivity.this, Registration.class);
                startActivity(intent);
                overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {
                notification.setAnchor(login);
                if (email.getText().toString().equals("") && password.getText().toString().equals("")) {
                    notification.setMessage("Email or password is empty");
                    notification.show();
                } else if (email.getText().toString().length() < 7) {
                    notification.setMessage("Email is too short");
                    notification.show();
                } else if (password.getText().toString().length() < 4) {
                    notification.setMessage("Password is too short");
                    notification.show();
                } else {
                    loadingDialog.show();
                    Form form = new Form()
                            .add("email", email.getText().toString())
                            .add("password", password.getText().toString());
                    Bridge.post(url)
                            .body(form)
                            .readTimeout(15000)
                            .asString(new ResponseConvertCallback<String>() {
                                @Override
                                public void onResponse(@Nullable com.afollestad.bridge.Response response, @Nullable String s, @Nullable BridgeException e) {
                                    loadingDialog.hide();
                                    if (e != null) {
                                        switch (e.reason()) {
                                            case BridgeException.REASON_REQUEST_CANCELLED: {
                                                notification.setMessage("Request was canceled \n Retry or Check your Connection");
                                                notification.show();
                                                break;
                                            }
                                            case BridgeException.REASON_REQUEST_TIMEOUT: {
                                                notification.setMessage("Network timed out, try again");
                                                notification.show();
                                                break;
                                            }
                                            case BridgeException.REASON_REQUEST_FAILED: {
                                                notification.setMessage("Network Error \n request failed, try again");
                                                notification.show();
                                                break;
                                            }
                                            case BridgeException.REASON_RESPONSE_UNSUCCESSFUL: {
                                                notification.setMessage("Email or password is not valid!");
                                                notification.show();
                                                break;
                                            }
                                            case BridgeException.REASON_RESPONSE_UNPARSEABLE: {
                                                notification.setMessage("Server error, try again later");
                                                notification.show();
                                                break;
                                            }
                                            case BridgeException.REASON_RESPONSE_IOERROR: {
                                                notification.setMessage("Server error try again later");
                                                notification.show();
                                                break;
                                            }
                                            case BridgeException.REASON_RESPONSE_VALIDATOR_FALSE:
                                            case BridgeException.REASON_RESPONSE_VALIDATOR_ERROR:
                                                String validatorId = e.validatorId();
                                                notification.setMessage("Invalid details, check your details");
                                                notification.show();
                                                break;
                                        }

                                    } else {
                                        if(response.asString().contains("verified yet")){
                                            notification.setMessage("Your Servizone expert account has not been verified yet. \n You'll get an Email with your login details as soon as you have been verified.");
                                            notification.show();
                                        }else if(response.asString().contains("Invalid login details")){
                                            notification.setMessage("Invalid Email Or Password");
                                            notification.show();
                                        }
                                        else{
                                            User user = new User(SignInActivity.this);
                                            String data = ResponseUtility.getData(s);
                                            System.out.println("RES DATA"+data);
                                            System.out.println("RESPONSE: "+response.asString());
                                            user.storeUser(data);
                                            Intent i = new Intent(SignInActivity.this, MainActivity.class);
                                            startActivity(i);
                                            overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                                        }
                                    }
                                }
                            });
//                    String url = "http://servizone.net/api/login";
//                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
//                            new Response.Listener() {
//                                @TargetApi(Build.VERSION_CODES.KITKAT)
//                                @Override
//                                public void onResponse(Object response) {
//                                    loadingDialog.hide();
//                                    System.out.println(response);
//                                    try {
//                                        JSONObject res = new JSONObject(response.toString());
//
//                                        if (res.has("error")) {
//                                            notification.setType(Notification.FAILURE);
//                                            notification.setMessage(res.getString("error"));
//                                            notification.show();
//                                        } else {
//                                            User user = new User(SignInActivity.this);
//                                            user.storeUser(response.toString());
//                                            Intent i = new Intent(SignInActivity.this, MainActivity.class);
//                                            startActivity(i);
//                                            overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
//                                        }
//
//                                    } catch (Exception ex) {
//                                        notification.setMessage(ex.getMessage());
//                                        notification.show();
//                                    }
//
//                                }
//                            },
//                            new Response.ErrorListener() {
//                                @TargetApi(Build.VERSION_CODES.KITKAT)
//                                @Override
//                                public void onErrorResponse(VolleyError error) {
//                                    loadingDialog.hide();
//                                    notification.setMessage(error.toString());
//                                    notification.show();
//                                }
//                            }) {
//                        @Override
//                        protected Map<String, String> getParams() {
//                            Map<String, String> params = new HashMap<String, String>();
//                            params.put("email", email.getText().toString());
//                            params.put("password", password.getText().toString());
//                            return params;
//                        }
//                    };
//                    RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
//                    queue.add(stringRequest);
                }
            }
        });

    }

}
