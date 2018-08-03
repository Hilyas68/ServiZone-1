package com.fincoapps.servizone.utils;

import android.util.Log;

public class AppConstants {
    public static boolean production = false;
    private static final String DEFAULT_HOST = "http://test.oneflaretech.com/api/";
    private static final String TEST_HOST = "http://localhost:8000/api/";

    public static void log(String TAG, String MESSAGE){
        if(!production){
            Log.e(TAG, MESSAGE);
        }
    }

    public static String getHost(){
        return !production ? TEST_HOST : DEFAULT_HOST;
    }
}
