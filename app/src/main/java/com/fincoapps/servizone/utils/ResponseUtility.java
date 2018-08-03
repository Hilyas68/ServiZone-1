package com.fincoapps.servizone.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.android.volley.RequestQueue;
import com.fincoapps.servizone.models.UserModel;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

public class ResponseUtility {
    public static boolean isSuccessful(String response){
        try {
            JSONObject resObj = new JSONObject(response);
            String status = resObj.getString("status");
            if(status.contains("success")){
                System.out.println("========================= " + status);
                return true;
            }
            else{
                return false;
            }
        } catch (JSONException ex) {
            System.out.println("========================= ERROR =========================");
            ex.printStackTrace();
            return false;
        }
    }


    public static String getMessage(String response){
        try {
            JSONObject resObj = new JSONObject(response);
            String message = resObj.getString("message");
            return message;
        } catch (JSONException e) {
            return "A temporary server error occurred.   Be rest assured Our Engineers are springing into action.   Please try again soon";
        }
    }

    public static String getData(String response){
        try {
            JSONObject resObj = new JSONObject(response);
            String data = resObj.getString("data");
            return data;
        } catch (JSONException e) {
            return "A server error occurred.   Our Engineers are springing into action";
        }
    }

}