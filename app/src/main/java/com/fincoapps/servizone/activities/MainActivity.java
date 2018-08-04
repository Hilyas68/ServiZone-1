package com.fincoapps.servizone.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
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
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.bridge.Bridge;
import com.afollestad.bridge.BridgeException;
import com.afollestad.bridge.Response;
import com.afollestad.bridge.ResponseConvertCallback;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.fincoapps.servizone.About;
import com.fincoapps.servizone.ProfileActivity;
import com.fincoapps.servizone.QuickSearchPopup;
import com.fincoapps.servizone.R;
import com.fincoapps.servizone.RegisterExpertActivity;
import com.fincoapps.servizone.experts.ExpertDetailsActivity;
import com.fincoapps.servizone.fragments.HomeFragment;
import com.fincoapps.servizone.models.ExpertModel;
import com.fincoapps.servizone.utils.Notification;
import com.fincoapps.servizone.utils.Request;
import com.fincoapps.servizone.utils.User;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;

import java.util.List;

import butterknife.ButterKnife;

import static java.lang.System.out;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FusedLocationProviderClient mFusedLocationClient;
    private PopupWindow popupWindow;
    LinearLayout container;
    Toolbar relativeLayout;
    NavigationView navigationView;
    RelativeLayout Errorpage;
    SwipeRefreshLayout mSwipeRefreshLayout = null;

//    @BindView(R.id.btnSearch)
//    ImageView btnSearch;

//    @BindView(R.id.txtLogin)
//    TextView txtLogin;
//
//    @BindView(R.id.txtChange)
//    TextView txtChange;
//
//    @BindView(R.id.txtAbout)
//    TextView txtAbout;
//
//    @BindView(R.id.txtMyProfile)
//    TextView txtMyProfile;

//    @BindView(R.id.userAvatar)
//    ImageView userAvatar;
//
//    @BindView(R.id.userName)
//    TextView userName;
//
//    @BindView(R.id.txtBeAnExpert)
//    TextView txtBeAnExpert;
//
//    @BindView(R.id.sidePanel)
//    LinearLayout sidePanel;
    private boolean isOpened;

    private String savedHome;

    private TextView username;
    Notification notification;
    //Revamp

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

    private static String CURRENT_TAG = TAG_HOME;

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

        //container = findViewById(R.id.mainContent);
        //relativeLayout = container.findViewById(R.id.hometoolbar);
        //Errorpage = container.findViewById(R.id.Errorpage);
        //TextView reload = Errorpage.findViewById(R.id.reload);

        drawer = findViewById(R.id.drawer_layout);
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

        fragment = new HomeFragment();
        //Load Default Fragment
        loadFragment(fragment);
//
//
//        //========================= LOAD OFFLINE JSON ==========================
//        savedHome = app.getHome();
//        if (savedHome.length() > 3) {//Data is present
//            processHomeData(savedHome);
//            if (net.haveNetworkConnection()) {
//                loadExperts();
//                getProfessions();
//            }
//        }
//        else {
////            Errorpage.getVisibility();
////            Errorpage.setVisibility(View.VISIBLE);
////
////            reload.setOnClickListener(new View.OnClickListener() {
////                @Override
////                public void onClick(View view) {
////                    if (net.haveNetworkConnection()) {
////                        loadExperts();
////                        getProfessions();
////                        Errorpage.getVisibility();
////                        Errorpage.setVisibility(View.GONE);
////                    } else {
////                        Errorpage.getVisibility();
////                        Errorpage.setVisibility(View.GONE);
////                        Errorpage.setVisibility(View.VISIBLE);
////                    }
////                }
////            });
//
//        }

        //============= check============ LOAD USER DATA ==========================
//        if (user.isLoggedIn()) {
//            txtLogin.setText("Log out");
//            txtChange.setVisibility(View.VISIBLE);
//            Glide.with(userAvatar.getContext())
//                    .load("http://servizone.net/storage" + me.avatar)
//                    .placeholder(R.drawable.placeholder)
//                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
//                    .into(userAvatar);
//            String suffix = (me.type == "expert") ? " (Expert)" : " ";
//            try {
//                if (me.type.contains("expert")) {
//                    txtBeAnExpert.setVisibility(View.GONE);
//                }
//            }catch (Exception ex){}
//            userName.setText(me.name + suffix);
//        } else {
//            txtChange.setVisibility(View.GONE);
//            txtLogin.setText("Log in");
//            txtMyProfile.setVisibility(View.GONE);
//        }
//
//        mSwipeRefreshLayout = container.findViewById(R.id.swipe_refresh_layout);
//        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                if (!user.isLoggedIn()) {
//                    txtChange.setVisibility(View.GONE);
//                    txtLogin.setText("Log in");
//                    txtMyProfile.setVisibility(View.GONE);
//                    txtBeAnExpert.setVisibility(View.VISIBLE);
//                }
//                loadExperts();
//            }
//        });

    }

    private void loadFragment(final Fragment holder) {
        //setToolbarTitle();

        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();
            return;
        }

        //Closing drawer on item click
        drawer.closeDrawers();

        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment temp = holder;
                if (temp == null){
                    navItemIndex = 0;
                    CURRENT_TAG = TAG_HOME;
                    temp = new HomeFragment();

                }
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, temp, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
                // selecting appropriate nav menu item
                selectNavMenu();
            }
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
    }
    //==================CHECK IF THE DEVICE IS INTERNET ENABLE OR NOT

//==================CHECKING ENDS HERE

//    @OnClick(R.id.btnSearch)
//    public void onClick(View view) {
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(MainActivity.this,
//                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
//                    1);
//        }
//        getLocation();
//        QuickSearchPopup searchPopup = new QuickSearchPopup(this, longitude, latitude);
//        searchPopup.show();
//    }

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


//    @OnClick(R.id.btnMenu)
//    public void onMenuClick(View view) {
////        if (isOpened) {
////            closePanel();
////        } else {
////            openPanel();
////        }
//    }

//    @TargetApi(Build.VERSION_CODES.KITKAT)
//    @OnClick(R.id.txtChange)
//    public void onchangpass(View v) {
//        if (user.isLoggedIn()) {
//            Intent intent = new Intent(this, ChangePassword.class);
//            startActivity(intent);
//            overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
//        } else {
//            notification.setMessage("Not Logged In Reload This Page");
//            notification.setType(Notification.WARNING);
//            notification.setAnchor(relativeLayout);
//            notification.show();
//        }
//    }

//    public void closePanel() {
//        ViewAnimator.animate(sidePanel).dp().translationX(0, -310).duration(500).start();//Hide it
//        isOpened = false;
//    }
//
//    public void openPanel() {
//        ViewAnimator.animate(sidePanel).dp().translationX(-310, 0).duration(500).start();//Show it
//        isOpened = true;
//    }

//    private void loadExperts() {
//        container = findViewById(R.id.mainContent);
//        Errorpage = container.findViewById(R.id.Errorpage);
//        final TextView reload = Errorpage.findViewById(R.id.reload);
//
//        loader.show();
//        try {
//            Bridge.post(com.fincoapps.servizone.utils.Request.api + "/home")
//                    .asString(new ResponseConvertCallback<String>() {
//                        @TargetApi(Build.VERSION_CODES.KITKAT)
//                        @Override
//                        public void onResponse(@NonNull com.afollestad.bridge.Response response, @Nullable String object, @Nullable BridgeException e) {
//
//                            loader.hide();
//                            if (e != null) {
//                                int reason = e.reason();
//                                System.out.println("================== ERROR ==================== " + e + " && " + reason);
//
//                                switch (e.reason()) {
//                                    case BridgeException.REASON_REQUEST_CANCELLED: {
//                                        Errorpage.getVisibility();
//                                        Errorpage.setVisibility(View.VISIBLE);
//                                        notification.setMessage("Request was canceled \n could not load Experts");
//                                        notification.setAnchor(reload);
//                                        notification.show();
//                                        break;
//                                    }
//                                    case BridgeException.REASON_REQUEST_TIMEOUT: {
//                                        Errorpage.getVisibility();
//                                        Errorpage.setVisibility(View.VISIBLE);
//                                        notification.setMessage("Network timed out, try again");
//                                        notification.setAnchor(reload);
//                                        notification.show();
//                                        break;
//                                    }
//                                    case BridgeException.REASON_REQUEST_FAILED: {
//                                        Errorpage.getVisibility();
//                                        Errorpage.setVisibility(View.VISIBLE);
//                                        notification.setMessage("Network Error \n request failed, try again");
//                                        notification.setAnchor(reload);
//                                        notification.show();
//                                        break;
//                                    }
//                                }
//                            } else {
//                                System.out.println("================== S ==================== " + response.asString());
//                                app.put("home", response.asString());
//                                processHomeData(response.asString());
//                                mSwipeRefreshLayout.setRefreshing(false);
//                            }
//                        }
//                    });
//
//        } catch (Exception ex) {
//            out.println("Hereeeeeeeeeeeeeeeeeeeeeeeeeeee");
//        }
//    }

//    public void processHomeData(String jsonData) {
//        try {
//            HomeModel homeModel = gson.fromJson(jsonData, HomeModel.class);
//            populateGrids(homeModel.top_experts, R.id.topExpertsLayout);
//            populateGrids(homeModel.closest_experts, R.id.closestExpertsLayout);
//            populateGrids(homeModel.featured_experts, R.id.featuredExpertsLayout);
//        } catch (Exception ex) {
//
//        }
//    }


    public void populateGrids(List list, int parent) {

        ViewGroup layout = findViewById(parent);
        layout.removeAllViews();

        final LayoutInflater inflater = LayoutInflater.from(this);
        for (int i = 0; i < list.size(); i++) {
            final ExpertModel model = (ExpertModel) list.get(i);

            View expertItem = inflater.inflate(R.layout.item_expert, container, false);
            TextView name = expertItem.findViewById(R.id.txtExpertName);
            TextView profession = expertItem.findViewById(R.id.txtExpertProfession);
            final ImageView expertImage = expertItem.findViewById(R.id.imageViewExpertImage);
            SimpleRatingBar ratingBar = expertItem.findViewById(R.id.expertRating);

            //============== SET VALUES ==============
            name.setText(model.name);
            profession.setText(model.profession);
            ratingBar.setRating(model.averageRating);
            Glide.with(expertImage.getContext())
                    .load("http://servizone.net/storage" + model.avatar)
                    .placeholder(R.drawable.placeholder)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(expertImage);

            //=================== IMAGE LISTENER ===================
            expertImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), ExpertDetailsActivity.class);
                    gson = new Gson();
                    String expertJson = gson.toJson(model);
                    goToExpert(expertJson);
                }
            });
            layout.addView(expertItem);
        }
    }

    public void populatelocation() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            System.out.println("------------------------------------- " + location.getLongitude());
                            System.out.println("------------------------------------- " + location.getLatitude());
                            longitude = location.getLongitude();
                            latitude = location.getLatitude();
                        }
                    }
                });
    }

    public void showPopup(View v) {
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View popupView = layoutInflater.inflate(R.layout.home_menu, null);
        popupWindow = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(true);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {

            }
        });

        TextView becomexpert = popupView.findViewById(R.id.txtBeAnExpert);
        becomexpert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RegisterExpertActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
            }
        });

        TextView txtLogin = (TextView) popupView.findViewById(R.id.txtLogin);
        if (user.isLoggedIn()) {
            txtLogin.setText("Log out");
        } else {
            popupView.findViewById(R.id.txtMyProfile).setVisibility(View.GONE);
            txtLogin.setText("Log in");
        }
        popupWindow.showAsDropDown(v, 0, 0);
    }


    //==================== ABOUT ONCLICK ======================
//    @OnClick(R.id.txtAbout)
//    public void onclickAbout(View v) {
//        Intent intent = new Intent(MainActivity.this, About.class);
//        startActivity(intent);
//        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
//    }


    //==================== BE AN EXPERT ONCLICK ======================
//    @TargetApi(Build.VERSION_CODES.KITKAT)
//    @OnClick(R.id.txtBeAnExpert)
//    public void onclickBeAnExpert(View v) {
//        Intent intent = new Intent(MainActivity.this, RegisterExpertActivity.class);
//        startActivity(intent);
//    }

    //==================== MY PROFILE ONCLICK ======================
//    @TargetApi(Build.VERSION_CODES.KITKAT)
//    @OnClick(R.id.txtMyProfile)
//    public void onclickProfile(View v) {
//        if (user.isLoggedIn()) {
//            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
//            startActivity(intent);
//        } else {
//            notification.setMessage("Not Logged In Reload This Page");
//            notification.setType(Notification.WARNING);
//            notification.setAnchor(relativeLayout);
//            notification.show();
//        }
//    }


    //==================== LOGIN/LOGOUT ONCLICK ======================
//    @OnClick(R.id.txtLogin)
//    public void txtLogin() {
//        out.println("+====================== CLICKED ====================== ");
//        if (user.isLoggedIn())//Logout
//        {
//            SharedPreferences user = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//            SharedPreferences.Editor editor = user.edit();
//            editor.clear();
//            editor.apply();
//            Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
//            startActivity(intent);
//            overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
//        } else {
//            Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
//            startActivity(intent);
//            overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
//        }
//    }

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
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
        if (loader.isShowing()){
            loader.hide();
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        navItemIndex = 0;
        if (id == R.id.nav_home) {
//            if(navigationView.getMenu().getItem(0).isChecked()){
//                return true;
//
//            }else {
//                Intent intent = new Intent(this, MainActivity.class);
//                startActivity(intent);
//            }
            CURRENT_TAG = TAG_HOME;
            navItemIndex = 0;
            fragment = new HomeFragment();

        }

        if (id == R.id.nav_nearByServices) {
            CURRENT_TAG = TAG_NEARBY_SERVICES;
            fragment = new HomeFragment();
            navItemIndex = 1;
        }

        if (id == R.id.nav_profile) {
            startActivity(new Intent(this, DrawerActivity.class));
        }

        if (id == R.id.nav_register_a_service) {
            CURRENT_TAG = TAG_REGISTER_SERVICE;
            navItemIndex = 3;
            fragment = new HomeFragment();
        }

        if (id == R.id.services) {
            CURRENT_TAG = TAG_VIEW_REGISTERED_SERVICES;
            navItemIndex = 4;
            fragment = new HomeFragment();
        }

        if (id == R.id.contactUs) {
            CURRENT_TAG = TAG_CONTACT_US;
            navItemIndex = 5;
            fragment = new HomeFragment();
        }

        if (id == R.id.logout) {
            app.clear();
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            overridePendingTransition(R.anim.trans_right_out, R.anim.trans_right_in);

        }


        if (item.isChecked()) {
            item.setChecked(false);
        } else {
            item.setChecked(true);
        }
        item.setChecked(true);

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

        }else if(id == R.id.search){
            search();
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
}