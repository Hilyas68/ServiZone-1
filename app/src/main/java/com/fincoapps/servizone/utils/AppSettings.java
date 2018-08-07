package com.fincoapps.servizone.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.fincoapps.servizone.models.HomeModel;
import com.fincoapps.servizone.models.UserModel;
import com.google.gson.Gson;

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

    //first-run
    public void setFirstRun(boolean bool){
        editor.putBoolean("first-run", bool).apply();
    }

    public boolean getFirstRun(){
        return preferences.getBoolean("first-run", true);
    }

    //User
    public void setUser(UserModel user){
        editor.putString("user", gson.toJson(user, UserModel.class)).apply();
    }

    public String getUser(){
        return preferences.getString("user", null);
    }

    //Home
    public void setHome(HomeModel home){
        editor.putString("home", gson.toJson(home, HomeModel.class)).apply();
    }

    public String getHome(){
        return preferences.getString("home", null);
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


    //Clear All
    public void clear(){
        editor.clear().apply();
    }

}