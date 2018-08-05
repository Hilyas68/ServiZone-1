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
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.afollestad.bridge.Bridge;
import com.afollestad.bridge.BridgeException;
import com.afollestad.bridge.Form;
import com.afollestad.bridge.ResponseConvertCallback;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.fincoapps.servizone.activities.BaseActivity;
import com.fincoapps.servizone.interfaces.Startpage;
import com.fincoapps.servizone.utils.CustomLoadingDialog;
import com.fincoapps.servizone.utils.Notification;
import com.fincoapps.servizone.utils.User;

import static java.lang.System.out;

public class ChangePassword extends BaseActivity {
    Notification notification;
    CustomLoadingDialog loader;
    EditText password, confirm;
    LinearLayout errorsuccesspage;
    AppCompatButton btn_changepass;
    ImageButton btnBack;
    public User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        loader = new CustomLoadingDialog(ChangePassword.this);
        notification = new Notification(ChangePassword.this);

        errorsuccesspage = findViewById(R.id.errorsuccesspage);
        btn_changepass = findViewById(R.id.btn_changepass);

        password = findViewById(R.id.password);
        confirm = findViewById(R.id.confirm);

        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(view-> onBackPressed());

        btn_changepass.setOnClickListener(view -> {
            if (password.getText().toString().isEmpty() || password.getText().toString().length() < 6) {
                password.getError();
                password.setError("Too Short");
            } else if (!confirm.getText().toString().equals(password.getText().toString())) {
                confirm.getError();
                confirm.setError("Password Do Not Match");
            } else {
                loader.show();
                Form form = new Form()
                        .add("password", password.getText().toString())
                        .add("confirmpassword", confirm.getText().toString())
                        .add("email", user.getEmail());

                Bridge.post(com.fincoapps.servizone.utils.Request.api + "/changepassword")
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
                                    notification.setAnchor(errorsuccesspage);
                                    notification.show();
                                } else {
                                    System.out.println(response.asString() + " THISSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS");
                                    if (response.isSuccess()) {
                                        if (response.asString().contains("User Does Not Exist")) {
                                            notification.setMessage("User Does Not Exist");
                                            notification.setType(Notification.FAILURE);
                                            notification.setAnchor(errorsuccesspage);
                                            notification.show();
                                        } else if (response.asString().contains("Passwords Not Match")) {
                                            notification.setMessage("Password Do Not Match");
                                            notification.setType(Notification.FAILURE);
                                            notification.setAnchor(errorsuccesspage);
                                            notification.show();
                                        } else {
                                            notification.setMessage("Password Changed Successfully");
                                            notification.setType(Notification.SUCCESS);
                                            notification.setAnchor(errorsuccesspage);
                                            notification.show();
                                        }
                                    } else {
                                        out.println("Something Else Is Wrong From The Server and response is not success....");
                                    }
                                }
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
