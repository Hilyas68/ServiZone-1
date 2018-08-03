package com.fincoapps.servizone;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.transition.Visibility;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.bridge.Bridge;
import com.afollestad.bridge.BridgeException;
import com.afollestad.bridge.MultipartForm;
import com.afollestad.bridge.Response;
import com.afollestad.bridge.ResponseConvertCallback;
import com.fincoapps.servizone.interfaces.ChooseProfession;
import com.fincoapps.servizone.models.ProfessionModel;
import com.fincoapps.servizone.adapters.ProfessionsAdapter;
import com.fincoapps.servizone.utils.AppSettings;
import com.fincoapps.servizone.utils.CurvedLayout;
import com.fincoapps.servizone.utils.CustomLoadingDialog;
import com.fincoapps.servizone.utils.Notification;
import com.fincoapps.servizone.utils.Request;
import com.fincoapps.servizone.utils.ResponseUtility;
import com.fincoapps.servizone.utils.User;
import com.github.florent37.viewanimator.ViewAnimator;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.PendingIntent.getActivity;
import static java.lang.System.out;

public class RegisterExpertActivity extends BaseActivity implements ChooseProfession {

    EditText name, age, email, mobile, password, comfirmpassword, address, about;
    ImageView avatar;
    String gender;
    Button register;
    User user;
    AppSettings app;
    String accountType = "user";
    TextView registertype;
    TextView profession;
    PopupWindow popupWindow;
    private TextView txtUser;
    private TextView txtExpert;
    Notification notification;
    CustomLoadingDialog loader;
    LinearLayout linearLayoutAccountType;
    private Dialog dialog;
    String professionId;
    private String selectedImagePath;
    private String encodedAvatar;
    private RadioGroup radioGroupGender;
    private RadioButton radioButtonMale;
    private RadioButton radioButtonFemale;
    private int currentRootViewHeight = 0;
    private int maxRootViewHeight = 0;
    ArrayList<ProfessionModel> professionList = new ArrayList<ProfessionModel>();

    @BindView(R.id.toolbar)
    RelativeLayout toolbar;

    @BindView(R.id.rootView)
    RelativeLayout rootView;

    @BindView(R.id.cardView)
    CardView cardView;



    //    @Override
    @TargetApi(Build.VERSION_CODES.M)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regiser_experts);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(RegisterExpertActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
        }
        ButterKnife.bind(this);

        getLocation();
        app = new AppSettings(this);
        loader = new CustomLoadingDialog(this);
        user = new User(this);

        notification = new Notification(this);
        registertype = (TextView) findViewById(R.id.registertype);
        address = (EditText) findViewById(R.id.txtAddress);
        about = (EditText) findViewById(R.id.txtDescription);
        profession = (TextView) findViewById(R.id.txtProfessions);
        avatar = (ImageView) findViewById(R.id.avatar);
        name = (EditText) findViewById(R.id.txtName);
        age = (EditText) findViewById(R.id.txtAge);
        email = (EditText) findViewById(R.id.txtEmail);
        mobile = (EditText) findViewById(R.id.txtMobile);
        register = (Button) findViewById(R.id.registerbtn);
        radioGroupGender = (RadioGroup) findViewById(R.id.radio_group_gender);
        radioButtonMale = (RadioButton) findViewById(R.id.radioMale);
        radioButtonFemale = (RadioButton) findViewById(R.id.radioFemale);
        notification.setAnchor(name);

        //======================= LISTENER FOR PROFESSIONS SELECTOR =====================
        profession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getProfessions();
                showPopupprofession(profession);
            }
        });


        //======================= LISTENER FOR IMAGE SELECTOR =====================
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,
                        "Select Picture"), 1);
            }
        });
        avatar.requestFocus();



        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                currentRootViewHeight = rootView.getHeight();
                if (currentRootViewHeight > maxRootViewHeight) {
                    maxRootViewHeight = currentRootViewHeight;

                }else{

                }
            }
        });


        KeyboardVisibilityEvent.setEventListener(
                RegisterExpertActivity.this,
                new KeyboardVisibilityEventListener() {
                    @Override
                    public void onVisibilityChanged(boolean isOpen) {
                        // some code depending on keyboard visiblity status
                        if(isOpen){
                            ViewAnimator.animate(toolbar).dp().translationY(-1000).duration(500).start();
                            toolbar.setVisibility(View.GONE);
                            ViewAnimator.animate(cardView).dp().translationY(-1000).duration(500).start();
                        }
                        else{
                            toolbar.setVisibility(View.VISIBLE);
                            ViewAnimator.animate(toolbar).dp().translationY(0).duration(500).start();
                            ViewAnimator.animate(cardView).dp().translationY(0).duration(500).start();
                        }

                    }
                });


        register.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {

                getLocation();
                if (name.getText().toString().isEmpty()) {
                    name.getError();
                    name.setError("Invalid name");
                } else if (age.getText().toString().isEmpty()) {
                    age.getError();
                    age.setError("Invalid age");
                } else if (email.getText().toString().isEmpty()) {
                    email.getError();
                    email.setError("Invalid Email Type");
                } else if (mobile.getText().toString().isEmpty()) {
                    mobile.getError();
                    mobile.setError("Invalid Mobile Type");
                } else if (address.getText().toString().isEmpty()) {
                    address.getError();
                    address.setError("Invalid Address Type");
                } else if (profession.getText().toString().isEmpty()) {
                    profession.getError();
                    profession.setError("Select your profession");
//                } else if (encodedAvatar.isEmpty()) {
//                    notification.setMessage("Your photo can not be blank");
//                    notification.setType(Notification.WARNING);
//                    notification.show();
                }
//                else if (longitude <= 0 && latitude <= 0) {
//                    notification.setMessage("We could not get your location \n set your location permission and try again");
//                    notification.setType(Notification.WARNING);
//                    notification.show();
//                }
                else {
                    MultipartForm form = null;
                    loader.show();
                    loader.setCanceledOnTouchOutside(true);
                    try {
                        if (radioButtonMale.isChecked()) {
                            gender = "Male";
                        } else {
                            gender = "Female";
                        }
                        form = new MultipartForm()
                                .add("name", name.getText().toString())
                                .add("age", age.getText().toString())
                                .add("gender", gender)
                                .add("email", email.getText().toString())
                                .add("mobile", mobile.getText().toString())
                                .add("profession_id", professionId)
                                .add("address", address.getText().toString())
                                .add("about", about.getText().toString())
                                .add("longitude", String.valueOf(longitude))
                                .add("latitude", String.valueOf(latitude))
                                .add("avatar", encodedAvatar)
                                .add("type", "expert");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        Bridge.post(com.fincoapps.servizone.utils.Request.api + "/register")
                                .body(form)
                                .asString(new ResponseConvertCallback<String>() {
                                    @Override
                                    public void onResponse(@NonNull com.afollestad.bridge.Response response, @Nullable String object, @Nullable BridgeException e) {
                                        loader.hide();
                                        if (e != null) {
                                            int reason = e.reason();
                                            switch (e.reason()) {
                                                case BridgeException.REASON_REQUEST_CANCELLED: {
                                                    notification.setMessage("Request was canceled \n could not load Experts");
                                                    notification.setAnchor(register);
                                                    notification.show();
                                                    break;
                                                }
                                                case BridgeException.REASON_REQUEST_TIMEOUT: {
                                                    notification.setMessage("Network timed out, try again");
                                                    notification.setAnchor(register);
                                                    notification.show();
                                                    break;
                                                }
                                                case BridgeException.REASON_REQUEST_FAILED: {
                                                    notification.setMessage("Network Error \n request failed, try again");
                                                    notification.setAnchor(register);
                                                    notification.show();
                                                    break;
                                                }
                                            }

                                        } else {
                                            System.out.println(response.asString());
                                            if (ResponseUtility.isSuccessful(response.asString())) {//if response has no error
                                                user.storeUser(response.asString());
//                                            Intent i = new Intent(RegisterExpertActivity.this, MainActivity.class);
                                                notification.setAnchor(avatar);
                                                notification.setType(Notification.WARNING);
                                                notification.setMessage("Your details has been sent for review.\n" +
                                                        "You will be notified via Email as soon as your account has been verified.\n" +
                                                        "The Email would also contain your login details");
                                                notification.show();
//                                            startActivity(i);
//                                            overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                                            } else {
                                                String errorMessage = ResponseUtility.getMessage(response.asString());
                                                notification.setAnchor(avatar);
                                                notification.setType(Notification.FAILURE);
                                                notification.setMessage(errorMessage);
                                                notification.show();
                                            }
                                        }
                                    }
                                });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    public String toBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                Uri selectedImageUri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                    avatar.setImageBitmap(bitmap);
                    encodedAvatar = toBase64(bitmap);
                    System.out.println(encodedAvatar);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                selectedImagePath = getPath(selectedImageUri);
            }
        }
    }

    @OnClick(R.id.btnBack)
    public void onBackClick(View view) {
        out.println("=================");
        onBackPressed();
    }


    /**
     * helper to retrieve the path of an image URI
     */
    public String getPath(Uri uri) {
        // just some safety built in 
        if (uri == null) {
            // TODO perform some logging or show user feedback
            return null;
        }
        // try to retrieve the image from the media store first
        // this will only work for images selected from gallery
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);
            cursor.close();
            return path;
        }
        // this is our fallback here
        return uri.getPath();
    }




    public void showPopupprofession(View v) {
        ListView listViewUsers;
        ArrayAdapter adapter;

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.popup_professions);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.getWindow().setDimAmount(0.0f);
        int width = (int) (this.getResources().getDisplayMetrics().widthPixels * 0.95);
        int height = (int) (this.getResources().getDisplayMetrics().heightPixels * 0.80);
        dialog.getWindow().setLayout(width, height);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        //================= PROFESSION LISTVIEW ====================
        out.println(professionList.size() + " 111111111111111111111");
        Gson gson = new Gson();
        Type collectionType = new TypeToken<List<ProfessionModel>>() {
        }.getType();
        String p = app.getProfessions();
        try {
            professionList.addAll((ArrayList) gson.fromJson(p, collectionType));
        }
        catch (Exception ex){
            notification.setMessage("An Error occurred.   Please restart Servizone");
            notification.setAnchor(profession);
            notification.show();
            return;
        }
        List<ProfessionModel> pList1 = professionList.subList(0, professionList.size() / 2);
        List<ProfessionModel> pList2 = professionList.subList((professionList.size() / 2), professionList.size());
        listViewUsers = dialog.findViewById(R.id.professionslist);
        adapter = new ProfessionsAdapter(pList1, this, RegisterExpertActivity.this);
        listViewUsers.setAdapter(adapter);

        ListView listView2 = dialog.findViewById(R.id.listViewHistory);
        ProfessionsAdapter adapter2 = new ProfessionsAdapter(pList2, this, RegisterExpertActivity.this);
        listView2.setAdapter(adapter2);
    }

    public void selectProfession(ProfessionModel p) {
        dialog.hide();
        profession.setText(p.profession);
        professionId = String.valueOf(p.id);
    }
    public void getProfessions() {
        Bridge.post(Request.api + "/professions")
                .asString(new ResponseConvertCallback<String>() {
                    @Override
                    public void onResponse(@NonNull Response response, @Nullable String object, @Nullable BridgeException e) {
                        if (e != null) {
                            int reason = e.reason();

                            switch (e.reason()) {
                                case BridgeException.REASON_REQUEST_CANCELLED: {
                                    notification.setMessage("Request was canceled \n could not load Experts");
                                    notification.setAnchor(profession);
                                    notification.show();
                                    break;
                                }
                                case BridgeException.REASON_REQUEST_TIMEOUT: {
                                    notification.setMessage("Network timed out, try again");
                                    notification.setAnchor(profession);
                                    notification.show();
                                    break;
                                }
                                case BridgeException.REASON_REQUEST_FAILED: {
                                    notification.setMessage("Network Error \n request failed, try again");
                                    notification.setAnchor(profession);
                                    notification.show();
                                    break;
                                }
                            }

                        } else {
                            app.put("professions", response.asString());
                        }
                    }
                });
    }
}