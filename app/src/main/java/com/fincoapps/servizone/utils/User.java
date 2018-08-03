package com.fincoapps.servizone.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.android.volley.RequestQueue;
import com.fincoapps.servizone.models.UserModel;
import com.google.gson.Gson;

public class User {
    public static Gson gson = new Gson();
    public static RequestQueue queue;
    public Context context;
    SharedPreferences preferences;
    public static String user;

    public User(Context context){
        this.context = context;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
user = preferences.getString("user", "");
    }


    public String getUser() {
        return preferences.getString("user", "");
    }

    public UserModel getUserModel() {
        String u = preferences.getString("user", "");
        System.out.println("================================"+u);
        return gson.fromJson(u, UserModel.class);
    }

    public void storeUser(String json) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("user", json);
        editor.commit();
    }



    public static boolean isLoggedIn() {
        return (user == "") ?  false : true;
    }

    public void logOut() {
        SharedPreferences user = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = user.edit();
        editor.clear();
        editor.apply();
//        Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
//        startActivity(intent);
//        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }
}