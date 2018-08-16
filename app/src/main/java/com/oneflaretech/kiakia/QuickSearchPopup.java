package com.oneflaretech.kiakia;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.afollestad.bridge.Bridge;
import com.afollestad.bridge.BridgeException;
import com.afollestad.bridge.Form;
import com.afollestad.bridge.ResponseConvertCallback;
import com.android.volley.RequestQueue;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.gson.Gson;
import com.oneflaretech.kiakia.adapters.ProfessionsAdapter;
import com.oneflaretech.kiakia.experts.ExpertResultsActivity;
import com.oneflaretech.kiakia.models.ProfessionModel;
import com.oneflaretech.kiakia.utils.CustomLoadingDialog;
import com.oneflaretech.kiakia.utils.Notification;

import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.OnClick;
import pl.bclogic.pulsator4droid.library.PulsatorLayout;


public class QuickSearchPopup extends Dialog {

    Bundle savedInstanceState;
    Context context;
    RequestQueue queue;
    Notification notification;
    public double longitude;
    public double latitude;
    private ArrayList<ProfessionModel> Listname = new ArrayList<ProfessionModel>();
    private ListView listView;
    private ProfessionsAdapter adapter;
    private ImageButton leftMenuBtn;
    ImageView imageViewSearch;
    public CustomLoadingDialog loader;
    RelativeLayout professionslayout;
    FusedLocationProviderClient mFusedLocationClient;

    public QuickSearchPopup(final Context context, double longitude, double latitude) {
        super(context);
        setTitle(null);
        setCancelable(true);
        this.context = context;
        this.latitude = latitude;
        this.longitude = longitude;
        setOnCancelListener(null);
        setCanceledOnTouchOutside(true);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.popup_quick_search);
        getWindow().getAttributes().windowAnimations = R.style.QuickSearchDialogAnimation;

        loader = new CustomLoadingDialog(context);
        notification = new Notification(context);

        professionslayout = findViewById(R.id.professionslayout);
        professionslayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QuickSearchPopup.this.hide();
            }
        });

        //------------------------------- SEARCH  BUTTON -------------------------------------
        imageViewSearch = findViewById(R.id.imageViewSearch);
        imageViewSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (haveNetworkConnection()) {
                    Popup popup = new Popup(context, QuickSearchPopup.this);
                    popup.show();
                } else {
//                    if(Listname.size()<=0){
                    notification.setMessage("Connection Problem Or Reload");
                    notification.setType(Notification.WARNING);
                    notification.setAnchor(view);
                    notification.show();
                    if (loader.isShowing()) {
                        loader.setCanceledOnTouchOutside(true);
                        loader.hide();
                    }
//                }
                }
            }
        });
    }

    @OnClick(R.id.professionslayout)
    public void onClick(View view) {
        this.hide();
    }

    @Override
    public void onStart() {
        super.onStart();
    }


    public void startSearch(ProfessionModel profession) {
        //------------------------------- PULSATING ANIMATION -------------------------------------
        final PulsatorLayout pulseLayout = findViewById(R.id.searchAnimation);
        pulseLayout.start();
        final String professionId = String.valueOf(profession.id);

        Form postData = new Form();
        postData.add("profession_id", professionId)
                .add("longitude", longitude)
                .add("latitude", latitude);

        Bridge.post(com.oneflaretech.kiakia.utils.Request.api + "/search")
                .body(postData)
                .asString(new ResponseConvertCallback<String>() {
                    @Override
                    public void onResponse(@NonNull com.afollestad.bridge.Response response, @Nullable String object, @Nullable BridgeException e) {
                        System.out.println("++++++++++++++++++++++++++++++++++++++++"+response.asString());
                        if (e != null) {
                            int reason = e.reason();
                            System.out.println(reason + "================== ERROR ====================" + e);

                            switch (e.reason()) {
                                case BridgeException.REASON_REQUEST_CANCELLED: {
                                    notification.setMessage("Request was canceled \n could not load Experts");
                                    notification.setAnchor(imageViewSearch);
                                    notification.show();
                                    break;
                                }
                                case BridgeException.REASON_REQUEST_TIMEOUT: {
                                    notification.setMessage("Network timed out, try again");
                                    notification.setAnchor(imageViewSearch);
                                    notification.show();
                                    break;
                                }
                                case BridgeException.REASON_REQUEST_FAILED: {
                                    notification.setMessage("Network is unreachable, try again");
                                    notification.setAnchor(imageViewSearch);
                                    notification.show();
                                    break;
                                }
                            }

                        } else {
                            try {
                                if (response.asString().contains("error")) {
                                    Log.e("SERVIZONE", response.asString());
                                    JSONObject j = new JSONObject(response.asString());
                                    notification.setMessage(j.getString("error"));
                                    notification.setType(Notification.WARNING);
                                    notification.setAnchor(imageViewSearch);
                                    pulseLayout.stop();
                                    notification.show();
                                    return;
                                }
                                Gson gson = new Gson();
                                Intent i = new Intent(context, ExpertResultsActivity.class);
                                i.putExtra("results", response.asString());
                                context.startActivity(i);
                                Activity act = (Activity) context;
                                act.overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);

                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                });
    }

    //==================CHECK IF THE DEVICE IS INTERNET ENABLE OR NOT
    public boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }
//==================CHECKING ENDS HERE
}