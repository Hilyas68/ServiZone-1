package com.oneflaretech.kiakia.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.internal.NavigationMenuView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.bridge.Bridge;
import com.afollestad.bridge.BridgeException;
import com.afollestad.bridge.Response;
import com.afollestad.bridge.ResponseConvertCallback;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.oneflaretech.kiakia.R;
import com.oneflaretech.kiakia.https.RetrofitClient;
import com.oneflaretech.kiakia.models.ServiceModel;
import com.oneflaretech.kiakia.models.UserModel;
import com.oneflaretech.kiakia.utils.AppConstants;
import com.oneflaretech.kiakia.utils.Notification;
import com.oneflaretech.kiakia.utils.Request;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;


    Toolbar relativeLayout;
    NavigationView navigationView;

    private TextView username, useremail;
    Notification notification;
    private Map<Marker, ServiceModel> allMarkersMap = new HashMap<Marker, ServiceModel>();

    //Revamp

    RetrofitClient retrofitClient;
    DrawerLayout drawer;
    Fragment fragment;
    private Handler mHandler;
    public static final String TAG_HOME = "Home";
    public static final String TAG_NEARBY_SERVICES = "Nearby Services";
    // index to identify current nav menu item
    public static int navItemIndex = 0;

    public static String CURRENT_HOME_VIEW = TAG_NEARBY_SERVICES;

    private static String CURRENT_TAG = TAG_HOME;
    ImageView userAvatar;


    String TAG = "MainActivity";


    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        notification = new Notification(this);
        if(app.getUser() == null){
            startActivity(new Intent(this, SignInActivity.class));
            finish();
        }
        //user = gson.fromJson(app.getUser(), UserModel.class);
        relativeLayout = findViewById(R.id.hometoolbar);
        setSupportActionBar(relativeLayout);
        mHandler = new Handler();
        drawer = findViewById(R.id.drawer_layout);
        retrofitClient = new RetrofitClient(this, AppConstants.getHost());
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, relativeLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //NavDrawer
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);

        //Nav Drawer Menu
        NavigationMenuView navMenu = (NavigationMenuView) navigationView.getChildAt(0);

        //Nav Drawer Header
        View view = navigationView.getHeaderView(0);
        username = view.findViewById(R.id.username);
        username.setText(user.getName());
        useremail = view.findViewById(R.id.useremail);
        useremail.setText(user.getEmail());
        userAvatar = view.findViewById(R.id.profile_image);
        userAvatar.setOnClickListener(v ->
            startActivity(new Intent(MainActivity.this, ShowImageActivity.class).putExtra("imageUrl", user.getAvatar()))
        );
        Picasso.get()
                .load(AppConstants.getFileHost() + user.getAvatar())
                .fit()
                .centerCrop()
                .placeholder(R.drawable.noimage)
                .into(userAvatar, new Callback() {
                    @Override
                    public void onSuccess() {
                        AppConstants.log(TAG,"Image Loaded");
                    }

                    @Override
                    public void onError(Exception e) {
                        AppConstants.log(TAG,e.getMessage());
                        e.printStackTrace();
                    }
                });


        //Switch Button
        FloatingActionButton fab = findViewById(R.id.switch_button);
        fab.setOnClickListener(v -> {
            startActivity(new Intent(this,SecondaryActivity.class));
            overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
        });
    }


    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setCheckable(true).setChecked(true);
        getSupportActionBar().setTitle(CURRENT_TAG);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)

    public void getProfessions() {
        Bridge.post(Request.api + "/professions")
                .asString(new ResponseConvertCallback<String>() {
                    @Override
                    public void onResponse(@NonNull Response response, @Nullable String object, @Nullable BridgeException e) {
                        if (e != null) {
                            int reason = e.reason();
                        } else {
                            app.put("professions", response.asString());
                        }
                    }
                });
    }

    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.trans_right_out, R.anim.trans_right_in);
    }

    public void onResume() {
        super.onResume();
        user = gson.fromJson(app.getUser(), UserModel.class);
        selectNavMenu();
        updateImage();


    }

    private void updateImage() {
        Picasso.get()
                .load(AppConstants.getFileHost() + user.getAvatar())
                .fit()
                .centerCrop()
                .placeholder(R.drawable.noimage)
                .into(userAvatar, new Callback() {
                    @Override
                    public void onSuccess() {
                        AppConstants.log(TAG,"Image Loaded");
                    }

                    @Override
                    public void onError(Exception e) {
                        AppConstants.log(TAG,e.getMessage());
                        e.printStackTrace();
                    }
                });
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        navItemIndex = 0;
        if (id == R.id.nav_home) {
            if(navigationView.getMenu().getItem(0).isChecked()){
                return true;
            }else {
                CURRENT_TAG = TAG_HOME;
                navItemIndex = 0;
            }
        }

        if (id == R.id.nav_profile) {
            startActivity(new Intent(this, ProfileActivity.class));
        }

        if (id == R.id.nav_register_a_service) {
            startActivity(new Intent(this, RegisterServiceActivity.class));
        }

        if (id == R.id.services) {
            startActivity((new Intent(MainActivity.this, AllServicesActivity.class)).putExtra("myService",true));
        }

        if (id == R.id.contactUs) {
            startActivity(new Intent(this, ContactUsActivity.class));
        }

        if(id == R.id.changePassword){
            startActivity(new Intent(this, ChangePasswordActivity.class));
            overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
        }

        if (id == R.id.logout) {
            logOut();
        }
      
        if (item.isChecked()) {
            item.setChecked(false);
        } else {
            item.setChecked(true);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            Intent intent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
        }

        if(id == R.id.search){
            Snackbar.make(relativeLayout, "Not Implemented Yet", Snackbar.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

//    public void search(){
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(MainActivity.this,
//                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
//                    1);
//        }
//        getLocation();
//        QuickSearchPopup searchPopup = new QuickSearchPopup(this, longitude, latitude);
//        searchPopup.show();
//    }


    @SuppressLint("CheckResult")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        boolean success = googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        this, R.raw.style_json));

        if(!success)
            AppConstants.log(TAG, String.valueOf(success));


        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        if(isLocationPermissionGranted){
            populateMap();
        }
    }

    @SuppressLint("MissingPermission")
    private void populateMap() {
       // mMap.setMyLocationEnabled(true);
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
        ServiceModel bm;

        for (int i = 0; i < 5; i++){
            bm = new ServiceModel();
            bm.setId(i+1);
            bm.setName("Business " + i);
            bm.setLatitude(lat[i]);
            bm.setLongitude(lng[i]);

            Marker m2 = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(bm.getLatitude(),bm.getLongitude()))
                    .anchor(0.5f, 0.5f)
                    .title(bm.getName())
                    .snippet(bm.getName())
                    .icon(BitmapDescriptorFactory.defaultMarker(new Random().nextInt(360))));

            allMarkersMap.put(m2,bm);

        }

        Marker m2 = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(user.getLatitude(),user.getLongitude()))
                .anchor(0.5f, 0.5f)
                .title("User")
                .snippet("User")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_user)));
        // For dropping a marker at a point on the Map
        AppConstants.log(TAG, app.getUser());
        AppConstants.log(TAG, user.getToken());
        AppConstants.log(TAG, String.valueOf(user.getLatitude()));
        AppConstants.log(TAG, String.valueOf(user.getLongitude()));

        // For zooming automatically to the location of the marker
        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(user.getLatitude(),user.getLongitude())).zoom(5).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        ServiceModel bm = allMarkersMap.get(marker);
        Log.e(TAG, bm.getName());
        return false;
    }
}