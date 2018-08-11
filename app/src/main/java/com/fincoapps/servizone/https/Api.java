package com.fincoapps.servizone.https;

import com.fincoapps.servizone.models.ResponseObjectModel;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import rx.Observable;

/**
 * Created by Sanat.Shukla on 05/01/17.
 */

public interface Api {
    @FormUrlEncoded
    @POST("login")
    Observable<ResponseObjectModel> login(@Field("email") String email, @Field("password") String password);

    @FormUrlEncoded
    @POST("home")
    Observable<ResponseObjectModel> home(@Field("token") String token);

    @FormUrlEncoded
    @POST("logout")
    Observable<ResponseObjectModel> logout(@Field("token") String token);

    @FormUrlEncoded
    @POST("password/change")
    Observable<ResponseObjectModel> changePassword(@Field("token") String token, @Field("current_password") String currentPassword, @Field("new_password") String newPassword);

    @FormUrlEncoded
    @POST("register")
    Observable<ResponseObjectModel> register(@Field("name") String name , @Field("email") String email, @Field("dob") String dob, @Field("mobile") String phoneNumber, @Field("gender") String gender, @Field("password") String password, @Field("latitude") double latitude, @Field("longitude") double longitude);

    @Multipart
    @POST("upload/avatar")
    Observable<ResponseObjectModel> uploadImage(@Part("token") RequestBody token, @Part MultipartBody.Part image, @Part("avatar")RequestBody name);

    @FormUrlEncoded
    @POST("users/edit")
    Observable<ResponseObjectModel> profileUpdate(@Field("token") String token, @Field("name") String name , @Field("dob") String dob, @Field("mobile") String phoneNumber, @Field("gender") String gender, @Field("latitude") double latitude, @Field("longitude") double longitude);


    Observable<ResponseObjectModel> contactus(@Field("token") String token, @Field("") String message);
}
