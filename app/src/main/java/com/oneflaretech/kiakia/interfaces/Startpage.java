package com.oneflaretech.kiakia.interfaces;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.oneflaretech.kiakia.R;
import com.oneflaretech.kiakia.activities.MainActivity;

public class Startpage extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startpage);

//        if(isFinishing()){
//            FrameLayout frameLayout = findViewById(R.id.fff);
//            LinearLayout layout = frameLayout.findViewById(R.id.ff);
//            Notification notification = new Notification(Startpage.this);
//
//            haveNetworkConnection();
//        if(haveNetworkConnection()){
//            out.println("It has an Active Connection");
//        }else if(!haveNetworkConnection()){
//            notification.setMessage("Network Timed Out");
//            notification.setType(Notification.WARNING);
//            notification.setAnchor(layout);
//            notification.show();
//        }
//        }else{
//            out.println("Still Loading Page!!!");
//        }


//        Bundle bundle = getIntent().getExtras();
//        String acctype = bundle.getString("acctype");
//
//        TextView textView = findViewById(R.id.welcometext);
//        if(acctype.contains("user")) {
//            textView.setText("Welcome your account have been successfully created, kindly continue to hire an expert");
//        }else if (acctype.contains("expert")) {
//            textView.setText("Welcome your account have been successfully created, we will go through your details before approval which will only take 24hrs. Thank You.");
//        }
        Button button = findViewById(R.id.continuebtn);
        button.setOnClickListener(view ->{
                Intent i = new Intent(Startpage.this, MainActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
        });
    }

    //==================CHECK IF THE DEVICE IS INTERNET ENABLE OR NOT
//    private boolean haveNetworkConnection() {
//        boolean haveConnectedWifi = false;
//        boolean haveConnectedMobile = false;
//
//        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
//        for (NetworkInfo ni : netInfo) {
//            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
//                if (ni.isConnected())
//                    haveConnectedWifi = true;
//            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
//                if (ni.isConnected())
//                    haveConnectedMobile = true;
//        }
//        return haveConnectedWifi || haveConnectedMobile;
//    }
//==================CHECKING ENDS HERE
}