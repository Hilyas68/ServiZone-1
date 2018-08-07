package com.fincoapps.servizone;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.transition.TransitionManager;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.afollestad.bridge.Bridge;
import com.afollestad.bridge.BridgeException;
import com.afollestad.bridge.MultipartForm;
import com.afollestad.bridge.ResponseConvertCallback;
import com.android.volley.RequestQueue;
import com.fincoapps.servizone.activities.BaseActivity;
import com.fincoapps.servizone.adapters.ProfessionsAdapter;
import com.fincoapps.servizone.interfaces.ChooseProfession;
import com.fincoapps.servizone.models.ProfessionModel;
import com.fincoapps.servizone.models.UserModel;
import com.fincoapps.servizone.utils.AppSettings;
import com.fincoapps.servizone.utils.CustomLoadingDialog;
import com.fincoapps.servizone.utils.Notification;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends BaseActivity implements ChooseProfession {

    private ProgressDialog pDialog;
    RequestQueue queue;

    ImageView imageView;
    EditText name;
    EditText age;
    TextView gender;
    String g;
    EditText address;
    EditText password;
    ImageButton editprofile, backBtn;
    private Notification notification;
    private TextView registertype;
    private TextView txtUser;
    private TextView txtExpert;
    private TextView about;
    private TextView profession;
    private ImageView avatar;
    private EditText email;
    private EditText mobile;
    private Button btnSave;
    private RadioGroup radioGroupGender;
    private RadioButton radioButtonMale;
    private RadioButton radioButtonFemale;
    private PopupWindow popupWindow;
    private Dialog dialog;
    private AppSettings app;
    private String professionId;
    private CustomLoadingDialog loader;
    private String encodedAvatar;
    private String selectedImagePath;
    UserModel userModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        //============================ INIT APP CLASSES ==================================
        app = new AppSettings(this);
        notification = new Notification(this);
        loader = new CustomLoadingDialog(this);
        //System.out.println("=================" + user.getUser());
        userModel = gson.fromJson(app.getUser(), UserModel.class);

//        ================================================= ANIMATION =================================================
        final ViewGroup transitionsContainer = findViewById(R.id.rootLayout);
        TransitionManager.beginDelayedTransition(transitionsContainer);


        address = findViewById(R.id.txtAddress);
        about = (EditText) findViewById(R.id.txtDescription);
        profession = findViewById(R.id.txtProfessions);
        avatar = findViewById(R.id.avatar);
        name = findViewById(R.id.txtName);
        age = findViewById(R.id.txtAge);
        email = findViewById(R.id.txtEmail);
        mobile = findViewById(R.id.txtMobile);
        btnSave = findViewById(R.id.btnSave);
        backBtn = findViewById(R.id.btnBack);
        radioGroupGender = findViewById(R.id.radio_group_gender);
        radioButtonMale = findViewById(R.id.radioMale);
        radioButtonFemale = findViewById(R.id.radioFemale);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               onBackPressed();
            }
        });

        profession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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


//=========================================== SAVE ==========================================================
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MultipartForm form = null;
                loader.show();
                try {
                    form = new MultipartForm()
                            .add("name", name.getText().toString())
                            .add("age", age.getText().toString())
                            .add("gender", g)
                            .add("email", email.getText().toString())
                            .add("mobile", mobile.getText().toString())
                            .add("profession_id", professionId)
                            .add("address", address.getText().toString())
                            .add("about", about.getText().toString())
                            .add("avatar", encodedAvatar)
                            .add("user_id", userModel.getId())
                            .add("token", userModel.getToken())
                            .add("type", "expert");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    loader.show();
                    Bridge.post(com.fincoapps.servizone.utils.Request.api + "/users/edit")
                            .body(form)
                            .asString(new ResponseConvertCallback<String>() {
                                @Override
                                public void onResponse(@NonNull com.afollestad.bridge.Response response, @Nullable String object, @Nullable BridgeException e) {
                                    loader.hide();
                                    System.out.println("================== " + response.asString());
                                    if (e != null) {
                                        int reason = e.reason();
                                        System.out.println(reason);
                                    } else {
//                                        user.storeUser(response.asString());
                                    }
                                }
                            });
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }

            }
        });
        getProfileDetails();
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
    //            cursor.close();
                return path;
            }
            // this is our fallback here
            return uri.getPath();
        }

    public void showPopupprofession(View v) {
        ArrayList<ProfessionModel> professionList = new ArrayList<ProfessionModel>();
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
        Gson gson = new Gson();
        Type collectionType = new TypeToken<List<ProfessionModel>>() {
        }.getType();
        String p = app.getProfessions();
        professionList.addAll(gson.fromJson(p, collectionType));
        listViewUsers = dialog.findViewById(R.id.professionslist);
        adapter = new ProfessionsAdapter(professionList, this, ProfileActivity.this);
        listViewUsers.setAdapter(adapter);
    }

    public void selectProfession(ProfessionModel p) {
        dialog.hide();
        profession.setText(p.profession);
        professionId = String.valueOf(p.id);
    }

    //===================== GET PROFILE DETAILS REQUEST ====================
    public void getProfileDetails() {
        Gson gson = new Gson();
        about.setText(userModel.getAbout());
        address.setText(userModel.getAddress());
        name.setText(userModel.getName());
        age.setText(userModel.getAge());
        email.setText(userModel.getEmail());
        mobile.setText(userModel.getPhone_number());

//        if (userModel.getAvatar().contains(".png") || userModel.getAvatar().contains(".jpg")) {
//            Glide.with(avatar.getContext())
//                    .load("http://servizone.net/storage" + userModel.getAvatar())
//                    .placeholder(R.drawable.placeholder)
//                    .into(avatar);
//        }
        if (userModel.getGender().equalsIgnoreCase("Male")) {
            radioButtonMale.setChecked(true);
        } else {
            radioButtonFemale.setChecked(true);
        }
        g = userModel.getGender();

    }


    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }


}
