package com.fincoapps.servizone.experts;

import android.Manifest;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.bridge.Bridge;
import com.afollestad.bridge.BridgeException;
import com.afollestad.bridge.Form;
import com.afollestad.bridge.ResponseConvertCallback;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.fincoapps.servizone.BaseActivity;
import com.fincoapps.servizone.QuickSearchPopup;
import com.fincoapps.servizone.ProfilePictureActivity;
import com.fincoapps.servizone.R;
import com.fincoapps.servizone.ReviewsActivity;
import com.fincoapps.servizone.ServizoneShared;
import com.fincoapps.servizone.models.ExpertModel;
import com.fincoapps.servizone.models.UserModel;
import com.fincoapps.servizone.databinding.ActivityExpertDetailsBinding;
import com.fincoapps.servizone.utils.CustomLoadingDialog;
import com.fincoapps.servizone.utils.Notification;
import com.fincoapps.servizone.utils.User;
import com.google.gson.Gson;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static java.lang.System.out;

public class ExpertDetailsActivity extends BaseActivity {

    RelativeLayout toolbar;

    @BindView(R.id.btnMenu)
    ImageButton btnMenu;

    @BindView(R.id.floatingActionButton)
    FloatingActionButton floatingActionButton;

    @BindView(R.id.toolbarTitle)
    TextView toolbarTitle;
    private PopupWindow popupWindow;
    private User user;

    SimpleRatingBar ratingView;
    private UserModel me;
    private ExpertModel expertModel;
    private EditText editTextReview;
    private CustomLoadingDialog loader;
    private Notification notification;
    private Dialog dialog;
    private ImageView expertImage;

    private String savedHome;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i = getIntent();
        String expertJson = i.getStringExtra("expert");
        Gson gson = new Gson();
        expertModel = gson.fromJson(expertJson, ExpertModel.class);
        out.println("======== E JS ============== " + expertJson);


        ActivityExpertDetailsBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_expert_details);
        binding.setExpert(expertModel); // generated setter based on the data in the layout file
        ButterKnife.bind(this);
        toolbarTitle.setText(expertModel.name);
        toolbar = findViewById(R.id.toolbar);


        //======================== INIT APP CLASSES ======================
        user = new User(this);
        me = user.getUserModel();
        notification = new Notification(this);
        loader = new CustomLoadingDialog(this);
        notification.setAnchor(toolbarTitle);


        //===================== EXPERT IMAGE =====================
        expertImage = findViewById(R.id.expertImage);
        Glide.with(expertImage.getContext())
                .load("http://servizone.net/storage" + expertModel.avatar)
                .placeholder(R.drawable.placeholder)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(expertImage);


    }


    @OnClick(R.id.cardView)
    public void expertImageOnClick(View view) {
        Intent intent = new Intent(ExpertDetailsActivity.this, ProfilePictureActivity.class);
        intent.putExtra("expertImageUrl", expertModel.avatar);
        startActivity(intent);
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }

//    }

    @OnClick(R.id.floatingActionButton)
    public void onFloatingActionButtonClick(View view) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", expertModel.mobile, null));
        startActivity(intent);
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }


    @OnClick(R.id.btnMenu)
    public void onMenuClick(View view) {
        showPopup(btnMenu);
    }

    //========================== MENU ================================
    public void showPopup(View v) {
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View popupView = layoutInflater.inflate(R.layout.expert_details_menu, null);

        popupWindow = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(true);

        TextView txtCallExpert = popupView.findViewById(R.id.txtCallExpert);
        txtCallExpert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = expertModel.mobile;
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                startActivity(intent);
            }
        });

        //========================== SEND SMS =============================
//        TextView txtSendSMS = popupView.findViewById(R.id.txtSendSMS);
//        txtSendSMS.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (expertModel.name.isEmpty() || expertModel.name.toString() == null) {
//                            ////
//                } else {
//                    String smsText = "Hi " + expertModel.name + ", " +
//                            "I'm " + me.name + " and I would be requiring your services As Soon As Possible." +
//                            "Hope to hear from you soon.";
//                    List<String> number = new ArrayList();
//                    number.add(expertModel.mobile);
//                    sendSms(ExpertDetailsActivity.this, smsText, number);
//                }
//            }
//        });

        TextView txtWriteReview = popupView.findViewById(R.id.txtWriteReview);
        TextView viewreview = popupView.findViewById(R.id.txtViewReviews);
        viewreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ExpertDetailsActivity.this, ReviewsActivity.class);
                intent.putExtra("expert_id", String.valueOf(expertModel.id));

                startActivity(intent);
                overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
            }
        });


        //============================= REPORT =============================
        dialog = new Dialog(ExpertDetailsActivity.this);
        final TextView txtReport = popupView.findViewById(R.id.txtReport);
        txtReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.setContentView(R.layout.popup_report);
                dialog.setCanceledOnTouchOutside(true);
                dialog.setCancelable(true);
                dialog.getWindow().setDimAmount(0.0f);
                int width = (int) (ExpertDetailsActivity.this.getResources().getDisplayMetrics().widthPixels * 0.95);
                int height = (int) (ExpertDetailsActivity.this.getResources().getDisplayMetrics().heightPixels * 0.65);
                dialog.getWindow().setLayout(width, height);
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
                popupWindow.dismiss();

                final RelativeLayout dialogparent = dialog.findViewById(R.id.dialogparent);
//                final TextView customerror = dialogparent.findViewById(R.id.customerror);
//                dialogparent.removeView(customerror);

                final EditText reporttext = dialog.findViewById(R.id.reporttext);
//                final TextView errorview = customerror;
                final RelativeLayout dialogview = dialogparent;

                Button btnSubmit = dialog.findViewById(R.id.btnreport);
                btnSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!user.isLoggedIn()) {
                            notification.setMessage("You can only report an expert when you're logged in.");
                            notification.setType(Notification.WARNING);
                            notification.setAnchor(view);
                            notification.show();
                        } else if (reporttext.getText().toString().isEmpty()) {
                            notification.setMessage("Please state your complain.");
                            notification.setType(Notification.WARNING);
                            notification.setAnchor(reporttext);
                            notification.show();
                        } else {
                            final String report = String.valueOf(reporttext.getText());
                            sendreporttoServer(report, dialogview);
                        }
                    }
                });
            }
        });

        popupWindow.showAsDropDown(v, -5, -60);
    }

    public boolean sendSms(Context context, String text, List<String> numbers) {

        String numbersStr = TextUtils.join(",", numbers);

        Uri uri = Uri.parse("sms:" + numbersStr);

        Intent intent = new Intent();
        intent.setData(uri);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        intent.putExtra("sms_body", text);
        intent.putExtra("address", numbersStr);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.setAction(Intent.ACTION_SENDTO);
            String defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(context);
            if (defaultSmsPackageName != null) {
                intent.setPackage(defaultSmsPackageName);
            }
        } else {
            intent.setAction(Intent.ACTION_VIEW);
            intent.setType("vnd.android-dir/mms-sms");
        }

        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }


    public void callExpert() {

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void writeReview(View v) {
        popupWindow.dismiss();
        if (!user.isLoggedIn()) {
            notification.setMessage("Please login to write a review");
            notification.setType(Notification.WARNING);
            notification.show();
            return;
        }
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.popup_rate);

        //====================== Rating View ==========================
        ratingView = (SimpleRatingBar) dialog.findViewById(R.id.rating);

        //====================== TextBox View ==========================
        editTextReview = (EditText) dialog.findViewById(R.id.editTextReview);


        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.getWindow().setDimAmount(0.0f);
        int width = (int) (this.getResources().getDisplayMetrics().widthPixels * 0.95);
        int height = (int) (this.getResources().getDisplayMetrics().heightPixels * 0.75);
        dialog.getWindow().setLayout(width, height);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        popupWindow.dismiss();

        Button btnSubmit = dialog.findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitReview(dialog);
            }
        });
    }

    private void submitReview(final Dialog dialog) {
        loader.show();
        Form form = new Form()
                .add("user_id", me.id)
                .add("token", me.token)
                .add("expert_id", expertModel.id)
                .add("rating", ratingView.getRating())
                .add("message", editTextReview.getText().toString());

        Bridge.post(com.fincoapps.servizone.utils.Request.api + "/reviews/add")
                .body(form)
                .asString(new ResponseConvertCallback<String>() {
                    @Override
                    public void onResponse(@NonNull com.afollestad.bridge.Response response, @Nullable String object, @Nullable BridgeException e) {
                        loader.hide();
                        System.out.println("================== " + response.asString());
                        if (e != null) {
                            int reason = e.reason();
                            System.out.println("====================== ERROR ========================" + e);
                        } else {
                            notification.setMessage("Your review has been successfully submitted");
                            notification.setType(Notification.SUCCESS);
                            dialog.hide();
                            notification.show();
                        }
                    }
                });
    }


    public void viewReviews() {

    }


    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.pop_enter, R.anim.pop_exit);
    }

    public void sendreporttoServer(final String text, final RelativeLayout dialogview) {
        loader.show();
        Form form = new Form()
                .add("user_id", me.id)
                .add("token", me.token)
                .add("expert_id", expertModel.id)
                .add("message", text);

        Bridge.post(com.fincoapps.servizone.utils.Request.api + "/experts/report")
                .body(form)
                .asString(new ResponseConvertCallback<String>() {
                    @Override
                    public void onResponse(@NonNull com.afollestad.bridge.Response response, @Nullable String object, @Nullable BridgeException e) {
                        loader.hide();
                        System.out.println("================== " + response.asString());
                        if (e != null) {
                            int reason = e.reason();
                            System.out.println("====================== ERROR ========================" + e);
                        } else {
                            System.out.println(response);
                            if (String.valueOf(response).contains("error")) {
                                notification.setMessage("You're Not Authorised, Please Login First");
                            } else {
                                notification.setMessage("Report Submitted");
                                notification.setType(Notification.SUCCESS);
                                popupWindow.dismiss();
                                dialog.dismiss();
                            }
                            notification.show();
                        }
                    }
                });

    }
}
