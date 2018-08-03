package com.fincoapps.servizone.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.fincoapps.servizone.R;

import static java.lang.System.out;

public class Notification {
    private final PopupWindow popupWindow;
    private final View popupView;
    public Dialog dialog;
    Context context;
    TextView message;
    View anchor;
    String type;

    public static final int  SUCCESS = 100;
    public static final int  WARNING = 50;
    public static final int  FAILURE = 0;


    public Notification(Context context){
        this.context = context;
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        popupView = layoutInflater.inflate(R.layout.notification, null);

        popupWindow = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(true);
        message = popupView.findViewById(R.id.notificationText);
    }

    public void setMessage(String msg){
        message.setText(msg);
    }

    public void setType(int type){
        if(type == Notification.SUCCESS){
            message.setBackgroundColor(Color.parseColor("#00FF00"));
        }
        else if(type == Notification.FAILURE){
            message.setBackgroundColor(Color.parseColor("#FF0000"));
        }

        else if(type == Notification.WARNING){
            message.setBackgroundColor(Color.parseColor("#FFA500"));
        }
    }


    public void setAnchor(View v){
        this.anchor = v;
    }



    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void show(){
        Activity activity = (Activity) this.context;
        final View parent = activity.getWindow().getDecorView().getRootView();
        int Y = (int)(context.getResources().getDisplayMetrics().ydpi/2);
        out.println(Y);
        popupWindow.showAsDropDown(anchor, 0, Gravity.CENTER_VERTICAL);
    }
}