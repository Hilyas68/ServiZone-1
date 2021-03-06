package com.oneflaretech.kiakia.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.oneflaretech.kiakia.R;
import com.oneflaretech.kiakia.googleAddress.MapAddressModel;
import com.oneflaretech.kiakia.https.NetworkHelper;
import com.oneflaretech.kiakia.https.RetrofitClient;
import com.oneflaretech.kiakia.utils.AppConstants;
import com.oneflaretech.kiakia.utils.CustomLoadingDialog;
import com.oneflaretech.kiakia.utils.Notification;

import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Random;

import ivb.com.materialstepper.stepperFragment;
import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.content.Context.MODE_PRIVATE;

public class ServiceAddressFragment extends stepperFragment implements OnMapReadyCallback {
    private GoogleMap mMap;
    EditText mAddress;
    private RetrofitClient retrofit;
    private Notification notification;
    private NetworkHelper net;
    SharedPreferences.Editor editor;
    AppCompatButton searchBtn;
    final String TAG = ServiceAddressFragment.class.getSimpleName();
    Double lat, lng;
    String formatedAddress;
    SharedPreferences prefs;
    public ServiceAddressFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.serviceaddress_fragment, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment)getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

         prefs = getActivity().getSharedPreferences(getActivity().getPackageName(), MODE_PRIVATE);

        editor = getActivity().getSharedPreferences(getActivity().getPackageName(), MODE_PRIVATE).edit();
        notification = new Notification(getContext());
        net = new NetworkHelper(getContext());
        mAddress = view.findViewById(R.id.address_edit);
        searchBtn = view.findViewById(R.id.search_map_btn);
        searchBtn.setOnClickListener(v -> {
            if(mAddress.getText().toString().isEmpty()){
                Snackbar.make(v, "Address Field is empty", Snackbar.LENGTH_SHORT).show();
                return;
            }

            getLatLng();
            setMarker();
        });
        mAddress.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if(actionId == EditorInfo.IME_ACTION_SEARCH ||
            actionId == EditorInfo.IME_ACTION_DONE || keyEvent.getAction() == KeyEvent.ACTION_DOWN ||
                    keyEvent.getAction() == KeyEvent.KEYCODE_ENTER){
            }
            return false;
        });
        return view;
    }

    @Override
    public boolean onNextButtonHandler() {
        if (mAddress.getText().toString().isEmpty()) {
            mAddress.getError();
            mAddress.setError("Enter Address");
        }else {
            editor.putString("address", mAddress.getText().toString());
            editor.apply();
            return true;
        }
        return false;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


         lat = Double.valueOf(prefs.getString("lat","0.0"));
         lng = Double.valueOf(prefs.getString("lng","0.0"));
        formatedAddress = prefs.getString("formated_address", null);
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng((!lat.isNaN()? lat : -34), (!lat.isNaN()? lng : 151));
        mMap.addMarker(new MarkerOptions().position(sydney).title((formatedAddress == null? "Marker in Sydney" : formatedAddress)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    public void setMarker(){
         lat = Double.valueOf(prefs.getString("lat",null));
         lng = Double.valueOf(prefs.getString("lng",null));
        formatedAddress = prefs.getString("formated_address",null);

        LatLng latLng = new LatLng(lat, lng);
        Marker addressMarker = mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .anchor(0.5f, 0.5f)
                .title(formatedAddress)
                .snippet(formatedAddress)
                .icon(BitmapDescriptorFactory.defaultMarker(new Random().nextInt(360))));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18 ));

        addressMarker.showInfoWindow();
    }

    void getLatLng(){
        CustomLoadingDialog loader = new CustomLoadingDialog(getContext());
        loader.show();
        AppConstants.log(TAG, getResources().getString(R.string.API_KEY));
        if (net.haveNetworkConnection()) {
            String address = mAddress.getText().toString();
            String API_KEY = getResources().getString(R.string.API_KEY);
            //                String full_url = "https://maps.googleapis.com/maps/api/geocode/json?address=" + URLEncoder.encode(address, "UTF-8") + "&key=" + URLEncoder.encode(API_KEY, "UTF-8");
//                AppConstants.log(TAG, full_url);
            retrofit = new RetrofitClient(getContext(), "https://maps.googleapis.com/maps/api/geocode/");
            retrofit.getApiService().getLotLng(address)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<MapAddressModel>() {
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
                            notification.setType(Notification.FAILURE);
                            notification.show();
                            notification.setAnchor(mAddress);
                            AppConstants.log(TAG, e.toString());
                            loader.dismiss();
                        }

                        @Override
                        public void onNext(MapAddressModel responseModel) {
                            AppConstants.log(TAG, responseModel.toString());
                            if(responseModel.getStatus().equals("OK")) {
                                Double lat = responseModel.getResults().get(0).getGeometry().getLocation().getLat();
                                Double lng = responseModel.getResults().get(0).getGeometry().getLocation().getLng();
                                String formated_address = responseModel.getResults().get(0).getFormattedAddress();

                                editor.putString("lat", String.valueOf(lat));
                                editor.putString("lng", String.valueOf(lng));
                                editor.putString("formated_address", formated_address);
                                editor.apply();

                                Log.e(TAG,"Lat = " + lat + "/ lng = " + lng );

                                notification.setMessage("Valid Address");
                                notification.setAnchor(mAddress);
                                notification.setType(Notification.SUCCESS);
                                notification.show();
                            }else if(responseModel.getStatus().equals("OVER_QUERY_LIMIT") || responseModel.getStatus().equals("REQUEST_DENIED")){
                                notification.setMessage("Please try again later");
                                notification.setAnchor(mAddress);
                                notification.setType(Notification.WARNING);
                                notification.show();
                            }else{
                                notification.setMessage("Invalid Address");
                                notification.setType(Notification.FAILURE);
                                notification.setAnchor(mAddress);
                                notification.show();
                                loader.dismiss();
                            }
                        }
                    });

        } else {
            notification.setMessage("No Internet Connection");
            notification.setType(Notification.FAILURE);
            notification.setAnchor(mAddress);
            notification.show();
            loader.dismiss();
        }
    }
}
