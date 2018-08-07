package com.fincoapps.servizone.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.internal.NavigationMenuView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.afollestad.bridge.Bridge;
import com.afollestad.bridge.BridgeException;
import com.afollestad.bridge.Response;
import com.afollestad.bridge.ResponseConvertCallback;
import com.fincoapps.servizone.About;
import com.fincoapps.servizone.QuickSearchPopup;
import com.fincoapps.servizone.R;
import com.fincoapps.servizone.fragments.HomeFragment;
import com.fincoapps.servizone.fragments.NearbyFragment;
import com.fincoapps.servizone.https.RetrofitClient;
import com.fincoapps.servizone.models.ResponseObjectModel;
import com.fincoapps.servizone.utils.AppConstants;
import com.fincoapps.servizone.utils.Notification;
import com.fincoapps.servizone.utils.Request;
import com.fincoapps.servizone.utils.User;

import java.net.SocketException;

import butterknife.ButterKnife;
import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static java.lang.System.out;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    Toolbar relativeLayout;
    NavigationView navigationView;

    private TextView username;
    Notification notification;
    //Revamp

    RetrofitClient retrofitClient;
    DrawerLayout drawer;
    Fragment fragment;
    private Handler mHandler;
    public static final String TAG_HOME = "Home";
    public static final String TAG_NEARBY_SERVICES = "Nearby Services";
    public static final String TAG_PROFILE = "Contacts";
    private static final String TAG_VIEW_REGISTERED_SERVICES = "My Services";
    private static final String TAG_REGISTER_SERVICE = "Register New Service";
    private static final String TAG_CONTACT_US = "Contact Us";
    private static final String TAG_SETTINGS = "Settings";
    // index to identify current nav menu item
    public static int navItemIndex = 0;

    public static String CURRENT_HOME_VIEW = TAG_NEARBY_SERVICES;

    private static String CURRENT_TAG = TAG_HOME;


    String TAG = "MainActivity";


    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        notification = new Notification(this);
        if(app.getUser() == null){
            startActivity(new Intent(this, SignInActivity.class));
            finish();
        }
        User user = gson.fromJson(app.getUser(), User.class);
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
        navMenu.addItemDecoration(new DividerItemDecoration(MainActivity.this, DividerItemDecoration.VERTICAL));

        //Nav Drawer Header
        View view = navigationView.getHeaderView(0);
        username = view.findViewById(R.id.username);
        username.setText(user.getName());

        getLocation();
        //Load Default Fragment
        loadFragment(fragment);
    }

    private void loadFragment(final Fragment holder) {
        //setToolbarTitle();
        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer


        //Closing drawer on item click
        drawer.closeDrawers();

        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        Runnable mPendingRunnable = () -> {
            Fragment innerFrag = holder;
            // update the main content by replacing fragments
            if (innerFrag == null){
                navItemIndex = 0;
                CURRENT_TAG = TAG_NEARBY_SERVICES;
                innerFrag = getNearbyFragment();
            }
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
            fragmentTransaction.replace(R.id.frame, innerFrag, CURRENT_TAG);
            fragmentTransaction.commitAllowingStateLoss();
            // selecting appropriate nav menu item
            selectNavMenu();
        };

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }


        // refresh toolbar menu
        invalidateOptionsMenu();
    }

    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setCheckable(true).setChecked(true);
        getSupportActionBar().setTitle(CURRENT_TAG);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults) {
        out.println("PERMISSION RESULT + + + + + + + + + + + + + + + + + " + permsRequestCode);
        switch (permsRequestCode) {
            case 1:
                boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                if (locationAccepted) {
                    getLocation();
                }
                break;
        }
    }

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
        selectNavMenu();
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
                fragment = new HomeFragment();
            }
        }

        if (id == R.id.nav_profile) {
            //go to profile
            startActivity(new Intent(this, DrawerActivity.class));
        }

        if (id == R.id.nav_register_a_service) {
            CURRENT_TAG = TAG_REGISTER_SERVICE;
            navItemIndex = 2;
            fragment = new HomeFragment();
        }

        if (id == R.id.services) {
            CURRENT_TAG = TAG_VIEW_REGISTERED_SERVICES;
            navItemIndex = 3;
            fragment = new HomeFragment();
        }

        if (id == R.id.contactUs) {
            CURRENT_TAG = TAG_CONTACT_US;
            navItemIndex = 4;
            fragment = new HomeFragment();
        }

        if(id == R.id.changePassword){
            startActivity(new Intent(this, ChangePasswordActivity.class));
            overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);

        }

        if (id == R.id.logout) {
            //logOut();
            AppConstants.log(TAG, app.getUser());
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            app.clear();
        }
      
        if (item.isChecked()) {
            item.setChecked(false);
        } else {
            item.setChecked(true);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        loadFragment(fragment);
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
            Intent intent = new Intent(MainActivity.this, About.class);
            startActivity(intent);
            overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
        }

        if(id == R.id.search){
            search();
        }

        if(id == R.id.switch_home_view){
            CURRENT_HOME_VIEW = CURRENT_HOME_VIEW.equals(TAG_NEARBY_SERVICES) ? TAG_HOME : TAG_NEARBY_SERVICES;
            loadFragment(CURRENT_HOME_VIEW.equals(TAG_NEARBY_SERVICES) ? getNearbyFragment() : getHomeFragment());
            AppConstants.log(TAG, "View Changed - " + CURRENT_HOME_VIEW);
        }

        return super.onOptionsItemSelected(item);
    }

    public void search(){
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
        }
        getLocation();
        QuickSearchPopup searchPopup = new QuickSearchPopup(this, longitude, latitude);
        searchPopup.show();
    }


    private Fragment getHomeFragment(){
        return new HomeFragment(app);
    }
    private Fragment getNearbyFragment(){
        return new NearbyFragment(app);
    }

    void logOut(){
        AppConstants.log(TAG, user.getToken());
        AppConstants.log(TAG, app.getUser());
        retrofitClient.getApiService().logout(user.getToken())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResponseObjectModel>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        String msg = "An Error Occurred. Please Try Again";
                        if(e instanceof HttpException)
                            msg = "No Internet Connection";
                        if(e instanceof SocketException)
                            msg = "An Internet Error Occurred";

                        notification.setAnchor(relativeLayout);
                        notification.setMessage(msg);
                        notification.show();
                        AppConstants.log(TAG, e.toString());
                    }

                    @Override
                    public void onNext(ResponseObjectModel responseModel) {
                        AppConstants.log(TAG, responseModel.toString());
                        app.clear();
                        startActivity(new Intent(MainActivity.this, SignInActivity.class));
                        finish();
                        overridePendingTransition(R.anim.trans_right_out, R.anim.trans_right_in);
                    }
                });
    }
}