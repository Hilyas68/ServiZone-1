package com.fincoapps.servizone.utils;

import android.util.Log;

public class AppConstants {
    public static boolean production = true;

    //Hosts
    private static final String DEFAULT_HOST = "http://test.oneflaretech.com/";
    private static final String TEST_HOST = "http://192.168.8.101:8000/";

    //Status
    public static final String STATUS_SUCCESS = "success";
    public static final String STATUS_ERROR = "error";
    public static final String STATUS_UNAUTHORIZED_USER = "unauthorised_user";


    public static void log(String TAG, String MESSAGE){
        if(!production){
            if(MESSAGE != null)
                Log.e(TAG, MESSAGE);
            else
                Log.e(TAG, "No Message");
        }
    }

    public static String getHost(){
        return (!production ? TEST_HOST : DEFAULT_HOST) + "api/v1/";
    }


    public static String getFileHost(){return (!production ? TEST_HOST : DEFAULT_HOST) + "uploads/";}

}
