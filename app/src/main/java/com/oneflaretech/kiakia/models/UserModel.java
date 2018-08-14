package com.oneflaretech.kiakia.models;

import java.util.ArrayList;

public class UserModel {

    private Integer id;
    private Integer role_id;
    private String avatar;
    private String name;
    private String age;
    private String gender;
    private String mobile;
    private String dob;
    private String about;
    private String email;
    private String address;
    private double longitude;
    private double latitude;
    private String status;
    private String token;
    private ArrayList<ServiceModel> myServices;




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

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
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

    public void setMobile(String mobil) {
        this.mobile = mobile;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
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

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
    public ArrayList<ServiceModel> getMyServices() {
        return myServices;
    }

    public void setMyServices(ArrayList<ServiceModel> myServices) {
        this.myServices = myServices;
    }


    @Override
    public String toString() {
        return "UserModel{" +
                "id=" + id +
                ", role_id=" + role_id +
                ", avatar='" + avatar + '\'' +
                ", name='" + name + '\'' +
                ", age='" + age + '\'' +
                ", gender='" + gender + '\'' +
                ", mobile='" + mobile + '\'' +
                ", dob='" + dob + '\'' +
                ", about='" + about + '\'' +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", status='" + status + '\'' +
                ", token='" + token + '\'' +
                ", myServices=" + myServices +
                '}';
    }
}
