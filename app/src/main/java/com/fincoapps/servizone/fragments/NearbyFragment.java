package com.fincoapps.servizone.fragments;


import android.Manifest;
import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fincoapps.servizone.R;
import com.fincoapps.servizone.activities.MainActivity;
import com.fincoapps.servizone.models.BusinessModel;
import com.fincoapps.servizone.utils.AppConstants;
import com.fincoapps.servizone.utils.AppSettings;
import com.fincoapps.servizone.utils.Notification;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 */
public class NearbyFragment extends Fragment implements GoogleMap.OnMarkerClickListener{

    String TAG = "NearbyFragment";
    private AppSettings app;
    MapView mMapView;
    private GoogleMap googleMap;
    View v;
    RxPermissions rx;
    Notification notification;
    MainActivity main;
    LocationRequest mLocationRequest;
    private Map<Marker, BusinessModel> allMarkersMap = new HashMap<Marker, BusinessModel>();

    private FusedLocationProviderClient mFusedLocationClient;

    public NearbyFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public NearbyFragment(AppSettings app){
        this.app = app;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_nearby, container, false);
        main = (MainActivity)getActivity();
        mMapView = v.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        rx = new RxPermissions(getActivity());
        notification = new Notification(getActivity());

         mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @SuppressLint("CheckResult")
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                rx
                        .request(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                        .subscribe(granted -> {
                            if (granted) {
                                // Always true pre-M
                                getLocation();
                                populateMap();
                            } else {
                                Log.e(TAG, "error");
                            }
                        });
            }
        });

        return v;
    }

    @SuppressLint("MissingPermission")
    private void populateMap() {
        googleMap.setMyLocationEnabled(true);
        //Dummy Data
        double lat[] = new double[5];
        lat[0] = 7.970016092;
        lat[1] = 7.629959329;
        lat[2] = 7.160427265;
        lat[3] = 6.443261653;
        lat[4] = 8.490010192;
        double lng[] = new double[5];
        lng[0] = 3.590002806;
        lng[1] = 4.179992634;
        lng[2] = 3.350017455;
        lng[3] = 3.391531071;
        lng[4] = 4.549995889;
        BusinessModel bm;

        for (int i = 0; i < 5; i++){
            bm = new BusinessModel();
            bm.setId(i+1);
            bm.setName("Business " + i);
            bm.setLatitude(lat[i]);
            bm.setLongitude(lng[i]);

            Marker m2 = googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(bm.getLatitude(),bm.getLongitude()))
                    .anchor(0.5f, 0.5f)
                    .title(bm.getName())
                    .snippet(bm.getName())
                    .icon(BitmapDescriptorFactory.defaultMarker(new Random().nextInt(360))));

            allMarkersMap.put(m2,bm);

        }

        Marker m2 = googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(main.user.getLatitude(),main.user.getLongitude()))
                .anchor(0.5f, 0.5f)
                .title("User")
                .snippet("User")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        // For dropping a marker at a point on the Map
        AppConstants.log(TAG, main.app.getUser());
        AppConstants.log(TAG, main.user.getToken());
        AppConstants.log(TAG, String.valueOf(main.user.getLatitude()));
        AppConstants.log(TAG, String.valueOf(main.user.getLongitude()));

        // For zooming automatically to the location of the marker
        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(main.user.getLatitude(),main.user.getLongitude())).zoom(5).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    void createMarker(){

    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        BusinessModel bm = allMarkersMap.get(marker);
        Log.e(TAG, bm.getName());
        return false;
    }

    @SuppressLint("MissingPermission")
    public void getLocation(){
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(TimeUnit.MINUTES.toMillis(5))     // 10 seconds, in milliseconds
                .setFastestInterval(10000); // 1 second, in milliseconds
        if(mFusedLocationClient == null) {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                    locationCallback,
                    null /* Looper */);
        }

        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            System.out.println("------------------------------------- " + location.getLongitude());
                            System.out.println("------------------------------------- " + location.getLatitude());
                            AppConstants.log(TAG,String.valueOf(location.getLatitude()));
                            AppConstants.log(TAG,String.valueOf(location.getLongitude()));
//                            longitude = location.getLongitude();
//                            latitude = location.getLatitude();
                        }
                    }
                });



    }

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            Location location = locationResult.getLastLocation();
            if (location != null) {
                main.user.setLongitude(location.getLongitude());
                main.user.setLatitude(location.getLatitude());
                main.app.setUser(main.user);
                //Send Query to get nearest services again
                populateMap();
            }
        }
    };
}
