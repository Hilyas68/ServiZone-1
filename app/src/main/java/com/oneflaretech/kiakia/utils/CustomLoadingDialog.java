package com.oneflaretech.kiakia.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import com.oneflaretech.kiakia.R;
import com.victor.loading.newton.NewtonCradleLoading;

/**
 * Created by finco on 10/8/17.
 */
public class CustomLoadingDialog extends Dialog {

    // constructor where we start the animation
    public CustomLoadingDialog(Context context) {
        super(context);
        setTitle(null);
        setCancelable(false);
        setOnCancelListener(null);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.custom_progress);

        NewtonCradleLoading newtonCradleLoading = findViewById(R.id.loading);
        newtonCradleLoading.start();
//        newtonCradleLoading.setLoadingColor(R.color.colorPrimary);
    }
}
