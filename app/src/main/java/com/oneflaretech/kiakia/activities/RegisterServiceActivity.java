package com.oneflaretech.kiakia.activities;

import android.content.SharedPreferences;
import android.util.Log;
import android.widget.EditText;

import com.google.gson.Gson;
import com.oneflaretech.kiakia.R;
import com.oneflaretech.kiakia.fragment.FnalFragment;
import com.oneflaretech.kiakia.fragment.ServiceAddressFragment;
import com.oneflaretech.kiakia.fragment.ServiceDetailFragment;
import com.oneflaretech.kiakia.https.NetworkHelper;
import com.oneflaretech.kiakia.https.RetrofitClient;
import com.oneflaretech.kiakia.models.ResponseObjectModel;
import com.oneflaretech.kiakia.models.ServiceModel;
import com.oneflaretech.kiakia.models.UserModel;
import com.oneflaretech.kiakia.utils.AppConstants;
import com.oneflaretech.kiakia.utils.AppSettings;
import com.oneflaretech.kiakia.utils.CustomLoadingDialog;
import com.oneflaretech.kiakia.utils.Notification;

import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import ivb.com.materialstepper.progressMobileStepper;
import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RegisterServiceActivity extends progressMobileStepper {

    List<Class> stepperFragmentList = new ArrayList<>();
    final String TAG = RegisterServiceActivity.class.getSimpleName();
    public ServiceModel serviceModels = new ServiceModel();
    EditText mAddress;
    public ArrayList<ServiceModel> serviceModelArrayList = new ArrayList<>();

    @Override
    public void onStepperCompleted() {
        showCompletedDialog();
    }

    @Override
    public List<Class> init() {
        stepperFragmentList.add(ServiceAddressFragment.class);
        stepperFragmentList.add(ServiceDetailFragment.class);
        stepperFragmentList.add(FnalFragment.class);

        return stepperFragmentList;
    }


    protected void showCompletedDialog(){
        android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(
                RegisterServiceActivity.this);

        // set title
        alertDialogBuilder.setTitle("Register Service");
        alertDialogBuilder
                .setMessage("We've completed the Registration")
                .setCancelable(true)
                .setPositiveButton("Yes", (dialog, id) -> registerService());

        // create alert dialog
        android.support.v7.app.AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

    }

    public void registerService(){
        SharedPreferences prefs = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        String name =   prefs.getString("name",null);
        String email =   prefs.getString("email",null);
        String mobile =   prefs.getString("mobile",null);
        String about =   prefs.getString("about",null);
        String address =   prefs.getString("address",null);
        Double lat = Double.valueOf(prefs.getString("lat",null));
        Double lng = Double.valueOf(prefs.getString("lng",null));
        String professionId =   prefs.getString("professionId",null);

        NetworkHelper net = new NetworkHelper(getApplicationContext());
        RetrofitClient retrofit;
        CustomLoadingDialog loader = new CustomLoadingDialog(this);
        AppSettings app = new AppSettings(this);
        Gson gson = new Gson();
        UserModel user = gson.fromJson(app.getUser(), UserModel.class);
        Notification notification = new Notification(this);
        mAddress = findViewById(R.id.address_edit);


        if (net.haveNetworkConnection()) {
            loader.show();
            retrofit = new RetrofitClient(this, AppConstants.getHost());
            retrofit.getApiService().registerService(user.getToken(), name,email, address, mobile, professionId, about, lat, lng)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<ResponseObjectModel>() {
                        @Override
                        public void onCompleted() {
                            loader.dismiss();
                        }

                        @Override
                        public void onError(Throwable e) {
                            String msg = "An Error Occurred. Please Try Again";
                            if (e instanceof HttpException)
                                msg = "A Server Error occurred. Please Try Again";
                            if (e instanceof SocketException || e instanceof SocketTimeoutException)
                                msg = "No Internet Connection";

                            notification.setMessage(msg);
                            notification.setAnchor(mAddress);
                            notification.show();
                            AppConstants.log(TAG, e.toString());
                            loader.dismiss();
                        }

                        @Override
                        public void onNext(ResponseObjectModel responseModel) {
                            if(responseModel.getStatus().equals(AppConstants.STATUS_SUCCESS)) {

                                serviceModels.setName(name);
                                serviceModels.setEmail(email);
                                serviceModels.setMobile(mobile);
                                serviceModels.setAbout(about);
                                serviceModels.setAddress(address);
                                serviceModels.setProfessionId(Integer.parseInt(professionId));
                                serviceModels.setLatitude(lat);
                                serviceModels.setLongitude(lng);

                                serviceModelArrayList = new ArrayList<>();
                                serviceModelArrayList.add(serviceModels);
                                app.setMyServices(serviceModelArrayList);

                               Log.e(TAG, app.getMyServices().toString());

                                notification.setMessage("Successfully Registered");
                                notification.setAnchor(mAddress);
                                notification.setType(Notification.SUCCESS);
                                notification.show();
                            }else{
                                notification.setMessage(responseModel.getData());
                                notification.show();
                                loader.dismiss();
                            }
                        }
                    });
        } else {
            notification.setMessage("No Internet Connection");
            notification.show();
            loader.dismiss();
        }

    }
}
