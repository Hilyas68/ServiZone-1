package com.fincoapps.servizone.utils;

import android.util.Log;

public class AppConstants {
    public static boolean production = false;

    public static void log(String TAG, String MESSAGE){
        if(!production){
            Log.e(TAG, MESSAGE);
        }
    }
}
