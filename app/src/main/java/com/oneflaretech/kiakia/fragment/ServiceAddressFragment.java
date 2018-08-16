package com.fincoapps.servizone.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.fincoapps.servizone.R;
import com.fincoapps.servizone.https.NetworkHelper;
import com.fincoapps.servizone.https.RetrofitClient;
import com.fincoapps.servizone.models.ResponseObjectModel;
import com.fincoapps.servizone.models.UserModel;
import com.fincoapps.servizone.models.googleAddress.MapAddressModel;
import com.fincoapps.servizone.utils.AppConstants;
import com.fincoapps.servizone.utils.CustomLoadingDialog;
import com.fincoapps.servizone.utils.Notification;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.SocketException;
import java.net.SocketTimeoutException;

import ivb.com.materialstepper.stepperFragment;
import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.content.Context.MODE_PRIVATE;
import static com.android.volley.VolleyLog.TAG;

public class ServiceAddressFragment extends stepperFragment implements OnMapReadyCallback {
    private GoogleMap mMap;
    EditText mAddress;
    private RetrofitClient retrofit;
    private Notification notification;
    private NetworkHelper net;
    SharedPreferences.Editor editor;
   final String TAG = ServiceAddressFragment.class.getSimpleName();

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

        editor = getActivity().getSharedPreferences(getActivity().getPackageName(), MODE_PRIVATE).edit();
        notification = new Notification(getContext());
        net = new NetworkHelper(getContext());
        mAddress = view.findViewById(R.id.address_edit);

        mAddress.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if(actionId == EditorInfo.IME_ACTION_SEARCH||
            actionId == EditorInfo.IME_ACTION_DONE || keyEvent.getAction() == KeyEvent.ACTION_DOWN ||
                    keyEvent.getAction() == KeyEvent.KEYCODE_ENTER){
                getLatLng();
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
            getLatLng();
            return true;
        }
        return false;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        SharedPreferences prefs = getActivity().getSharedPreferences(getActivity().getPackageName(), MODE_PRIVATE);
        Double lat = Double.valueOf(prefs.getString("lat",null));
        Double lng = Double.valueOf(prefs.getString("lng",null));
        String address = prefs.getString("formated_address", null);
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng((!lat.isNaN()? lat : -34), (!lat.isNaN()? lng : 151));
        mMap.addMarker(new MarkerOptions().position(sydney).title((address == null? "Marker in Sydney" : address)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    void getLatLng(){
        CustomLoadingDialog loader = new CustomLoadingDialog(getContext());
        loader.show();

        if (net.haveNetworkConnection()) {
            String address = mAddress.getText().toString();
            retrofit = new RetrofitClient(getContext(), "http://maps.googleapis.com/maps/api/geocode/");
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
                            notification.show();
                            notification.setAnchor(mAddress);
                            AppConstants.log(TAG, e.toString());
                            loader.dismiss();
                        }

                        @Override
                        public void onNext(MapAddressModel responseModel) {
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
            notification.setAnchor(mAddress);
            notification.show();
            loader.dismiss();
        }
    }
}
