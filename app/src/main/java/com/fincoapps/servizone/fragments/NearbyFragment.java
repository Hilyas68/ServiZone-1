package com.fincoapps.servizone.fragments;


import android.Manifest;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fincoapps.servizone.R;
import com.fincoapps.servizone.utils.AppConstants;
import com.fincoapps.servizone.utils.AppSettings;
import com.fincoapps.servizone.utils.Notification;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.androidannotations.annotations.App;

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
        mMapView = (MapView) v.findViewById(R.id.mapView);
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
        // For dropping a marker at a point on the Map
        Marker m1 = googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(38.609556, -1.139637))
                .anchor(0.5f, 0.5f)
                .title("Title1")
                .snippet("Snippet1")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));


        Marker m2 = googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(40.4272414,-3.7020037))
                .anchor(0.5f, 0.5f)
                .title("Title2")
                .snippet("Snippet2")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));


        Marker m3 = googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(43.2568193,-2.9225534))
                .anchor(0.5f, 0.5f)
                .title("Title3")
                .snippet("Snippet3")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

        // For zooming automatically to the location of the marker
        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(43.2568193,-2.9225534)).zoom(12).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
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
        AppConstants.log(TAG, marker.getId());
        AppConstants.log(TAG, marker.getPosition().toString());
        AppConstants.log(TAG, marker.getTitle());
        return false;
    }
}
