package com.fincoapps.servizone.https;

import com.fincoapps.servizone.models.HomeModel;
import com.fincoapps.servizone.models.ResponseObjectModel;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by Sanat.Shukla on 05/01/17.
 */

public interface Api {
    @FormUrlEncoded
    @POST("login")
    Observable<ResponseObjectModel> login(@Field("email") String email, @Field("password") String password);

    @POST("home")
    Observable<HomeModel> home();

    @FormUrlEncoded
    @POST("logout")
    Observable<ResponseObjectModel> logout(@Field("token") String token);

    @FormUrlEncoded
    @POST("password/change")
    Observable<ResponseObjectModel> changePassword(@Field("token") String token, @Field("current_password") String currentPassword, @Field("new_password") String newPassword);

    @FormUrlEncoded
    @POST("register")
    Observable<ResponseObjectModel> register(@Field("name") String name , @Field("email") String email, @Field("dob") String dob, @Field("phone_number") String phoneNumber, @Field("gender") String gender, @Field("password") String password, @Field("latitude") double latitude, @Field("longitude") double longitude);

//    @POST("categories/listAllCategories")
//    Observable<CommonResponse<CommonListResult<CategoryModel>>> getAllCategory();
//
//    @FormUrlEncoded
//    @POST("services/getServices")
//    Observable<CommonResponse<CommonListResult<ServiceModel>>> getAllService(@Field("user_id") String user_id, @Field("latlong_range") String latlong_range, @Field("category_id") String category_id);
//
//    @GET("otp/?")
//    Observable<CognalysResponse> mobileVerification(@Query("app_id") String appId, @Query("access_token") String accessToken, @Query("mobile") String mobile);
//
//    @GET("otp/confirm/?")
//    Observable<CognalysConfirmResponse> confirmVerification(@Query("app_id") String appId, @Query("access_token") String accessToken, @Query("keymatch") String keymatch, @Query("otp") String otp);
//




}
