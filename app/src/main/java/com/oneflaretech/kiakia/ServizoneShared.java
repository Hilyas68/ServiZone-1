package com.oneflaretech.kiakia;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kolawoleadewale on 9/30/17.
 */

public class ServizoneShared {
    private static ServizoneShared mInstance= null;

    protected ServizoneShared(){}

    public static synchronized ServizoneShared getInstance(){
        if(null == mInstance){
            mInstance = new ServizoneShared();
        }
        return mInstance;
    }


    public static Map<String,String> processParams(Context context) {
        Map<String,String> params = new HashMap<String, String>();
        params.put("user_id", ServizoneShared.getUserID(context));
        params.put("token", ServizoneShared.getUserToken(context) );
        return params;

    }

    public static String getUserID(Context context) {
        String user_id = "0";
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String user = preferences.getString("user", "");
        JSONObject userObj = null;
        try {
            userObj = new JSONObject(user);
            JSONObject u = userObj.getJSONObject("user");
            user_id = u.getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return user_id;
    }

    public static String getUserName(Context context) {
        String user_name = "";
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String user = preferences.getString("user", "");
        JSONObject userObj = null;
        try {
            userObj = new JSONObject(user);
            JSONObject u = userObj.getJSONObject("user");
            user_name = u.getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return user_name;
    }


    public static boolean isLoggedIn(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String user = preferences.getString("user", "");
        return user != "";
    }

    public static String getUserToken(Context context) {
        String token = "";
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String user = preferences.getString("user", "");

        JSONObject userObj = null;
        try {
            userObj = new JSONObject(user);
            JSONObject u = userObj.getJSONObject("user");
            token = u.getString("token");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return token;
    }

    public static String getUserImage(Context context) {
        String image = "";
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String user = preferences.getString("user", "");
        JSONObject userObj = null;
        try {
            userObj = new JSONObject(user);
            JSONObject u = userObj.getJSONObject("user");
            image = u.getString("image");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return image;
    }
}
