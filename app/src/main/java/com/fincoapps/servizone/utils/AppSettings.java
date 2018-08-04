package com.fincoapps.servizone.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.android.volley.RequestQueue;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by finco on 7/30/17.
 * This file contains Basic Methods needed in most Apps
 */

public class AppSettings {
    private Context context;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private static JSONObject user;
    private Gson gson;

    public AppSettings(Context context){
        this.context = context;
        preferences = context.getSharedPreferences(this.context.getPackageName(), Context.MODE_PRIVATE);
        this.gson = new Gson();
        editor = preferences.edit();
    }


    //User
    public void setUser(User user){
        editor.putString("user", gson.toJson(user, User.class)).apply();
    }

    public String getUser(){
        return preferences.getString("user", null);
    }

    public void put(String key, String value){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String getProfessions(){
        SharedPreferences.Editor editor = preferences.edit();
        String value = preferences.getString("professions", "");
        return value;
    }


    public String getHome(){
        SharedPreferences.Editor editor = preferences.edit();
        String value = preferences.getString("home", "");
        return value;
    }


    //Clear All
    public void clear(){
        editor.clear().apply();
    }

}