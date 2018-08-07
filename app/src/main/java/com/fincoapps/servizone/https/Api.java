package com.fincoapps.servizone.https;
import com.afollestad.bridge.annotations.ContentType;
import com.fincoapps.servizone.models.HomeModel;
import com.fincoapps.servizone.models.ResponseModel;
import com.fincoapps.servizone.models.UserModel;

import org.androidannotations.annotations.App;
import org.androidannotations.annotations.rest.Post;

import java.util.ArrayList;
import java.util.Date;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Sanat.Shukla on 05/01/17.
 */

public interface Api {
    @FormUrlEncoded
    @POST("login")
    Observable<ResponseModel> login(@Field("email") String email, @Field("password") String password);

    @POST("home")
    Observable<HomeModel> home();

    @FormUrlEncoded
    @POST("logout")
    Observable<ResponseModel> logout(@Field("token") String token);

    @FormUrlEncoded
    @POST("register")
    Observable<ResponseModel> register(@Field("name") String name , @Field("email") String email, @Field("dob") String dob, @Field("phone_number") String phoneNumber, @Field("gender") String gender, @Field("password") String password);

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
