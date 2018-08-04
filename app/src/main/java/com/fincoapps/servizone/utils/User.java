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


    //New User
    private Integer id;
    private Integer role_id;
    private String avatar;
    private String name;
    private Integer age;
    private String gender;
    private String mobile;
    private String about;
    private String email;
    private String address;
    private Float longitude;
    private Float latitude;
    private String status;
    private boolean is_blocked;
    private String token;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRole_id() {
        return role_id;
    }

    public void setRole_id(Integer role_id) {
        this.role_id = role_id;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Float getLongitude() {
        return longitude;
    }

    public void setLongitude(Float longitude) {
        this.longitude = longitude;
    }

    public Float getLatitude() {
        return latitude;
    }

    public void setLatitude(Float latitude) {
        this.latitude = latitude;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isIs_blocked() {
        return is_blocked;
    }

    public void setIs_blocked(boolean is_blocked) {
        this.is_blocked = is_blocked;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", role_id=" + role_id +
                ", avatar='" + avatar + '\'' +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", gender='" + gender + '\'' +
                ", mobile='" + mobile + '\'' +
                ", about='" + about + '\'' +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", status='" + status + '\'' +
                ", is_blocked=" + is_blocked +
                ", token='" + token + '\'' +
                '}';
    }
}