package com.oneflaretech.kiakia.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oneflaretech.kiakia.R;
import com.oneflaretech.kiakia.adapters.ProfessionsAdapter;
import com.oneflaretech.kiakia.https.NetworkHelper;
import com.oneflaretech.kiakia.https.RetrofitClient;
import com.oneflaretech.kiakia.interfaces.ChooseProfession;
import com.oneflaretech.kiakia.models.ProfessionModel;
import com.oneflaretech.kiakia.models.ResponseObjectModel;
import com.oneflaretech.kiakia.models.UserModel;
import com.oneflaretech.kiakia.utils.AppConstants;
import com.oneflaretech.kiakia.utils.AppSettings;
import com.oneflaretech.kiakia.utils.Notification;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import ivb.com.materialstepper.stepperFragment;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

public class ServiceDetailFragment extends stepperFragment  implements ChooseProfession {

    EditText mName, mEmail, mMobile, mAbout;
    CircleImageView mAvatar;
    ImageButton mBack;
    CardView imgCardView;
    TextView mProfession;
    private Dialog dialog;
    String professionId;
    ArrayList<ProfessionModel> professionList = new ArrayList<>();
    AppSettings appSettings;
    NetworkHelper net;
    UserModel user;
    ProfessionModel professions;
    RetrofitClient retrofitClient;
    Notification notification;
    String TAG = "ServiceDetailFragment";
    private Gson gson;

    public ServiceDetailFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.servicedetail_fragment, container, false);
        notification = new Notification(getContext());
        net = new NetworkHelper(getContext());
        appSettings = new AppSettings(getContext());
        gson = new Gson();

        user = gson.fromJson(appSettings.getUser(), UserModel.class);


        mName = view.findViewById(R.id.txtName);
        mEmail = view.findViewById(R.id.txtEmail);
        mMobile = view.findViewById(R.id.txtMobile);
        mAbout = view.findViewById(R.id.txtDescription);
        mAvatar = view.findViewById(R.id.avatar);
        mProfession = view.findViewById(R.id.txtProfessions);
        mBack = view.findViewById(R.id.btnBack);
        imgCardView = view.findViewById(R.id.cardView);

        mBack.setOnClickListener(view12 -> {
            getActivity().onBackPressed();
        });

        mProfession.setOnClickListener(view1 -> {
            getProfessions();
            showPopupprofession(mProfession);
        });

        imgCardView.setOnClickListener(view13 -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent,
                    "Select Picture"), 1);
        });

        return view;
    }


    public void showPopupprofession(View v) {
        ListView listViewUsers;
        ArrayAdapter adapter;

        dialog = new Dialog(getContext());
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
        String p = appSettings.getProfessions();
        try {
            professionList.addAll(gson.fromJson(p, collectionType));
        }
        catch (Exception ex){
            notification.setMessage("An Error occurred.   Please restart Servizone");
            notification.setAnchor(mProfession);
            notification.show();
            return;
        }
        List<ProfessionModel> pList1 = professionList.subList(0, professionList.size() / 2);
        List<ProfessionModel> pList2 = professionList.subList((professionList.size() / 2), professionList.size());
        listViewUsers = dialog.findViewById(R.id.professionslist);
        adapter = new ProfessionsAdapter(pList1, getContext(), this);
        listViewUsers.setAdapter(adapter);

        ListView listView2 = dialog.findViewById(R.id.listViewHistory);
        ProfessionsAdapter adapter2 = new ProfessionsAdapter(pList2, getContext(), this);
        listView2.setAdapter(adapter2);
    }

    private void getProfessions() {
        notification.setAnchor(mProfession);
        if(net.haveNetworkConnection()){
            retrofitClient = new RetrofitClient(getContext(), AppConstants.getHost());
            retrofitClient.getApiService().professions()
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<ResponseObjectModel>() {
                        @Override
                        public void onCompleted(){
                        }

                        @Override
                        public void onError(Throwable e) {
                            String msg = "An Error Occurred. Please Try Again";
                            if(e instanceof HttpException)
                                msg = "A Server Error occurred. Please Try Again";
                            if(e instanceof SocketException || e instanceof SocketTimeoutException)
                                msg = "No Internet Connection";

                            notification.setMessage(msg);
                            notification.show();
                            AppConstants.log(TAG, e.toString());
                        }

                        @Override
                        public void onNext(ResponseObjectModel responseModel) {
                            if(responseModel.getStatus().equals(AppConstants.STATUS_SUCCESS)){

                                Log.e(TAG,  responseModel.getData().toString());
                                appSettings.put("professions", responseModel.getData());
                                Log.e(TAG, appSettings.getProfessions().toString());
                            }else{
                                notification.setMessage(responseModel.getMessage());
                                notification.show();
                            }
                        }
                    });
        }else {
            notification.setMessage("No Internet Connection");
            notification.show();

        }
    }


    @Override
    public boolean onNextButtonHandler() {
        if (mName.getText().toString().isEmpty()) {
            mName.getError();
            mName.setError("Invalid name");
        } else if (mEmail.getText().toString().isEmpty() || mEmail.getText().toString().length() < 3 || !mEmail.getText().toString().contains("@")) {
            mEmail.getError();
            mEmail.setError("Invalid Email");
        } else if (mMobile.getText().toString().isEmpty()) {
            mMobile.getError();
            mMobile.setError("Invalid Mobile Number");
        }  else if (mAbout.getText().toString().isEmpty()) {
            mAbout.getError();
            mAbout.setError("please tell us about your service");
        }else if (mProfession.getText().toString().isEmpty()) {
                mProfession.getError();
                mProfession.setError("Select your profession");
        }else {

            SharedPreferences.Editor editor = getActivity().getSharedPreferences(getActivity().getPackageName(), MODE_PRIVATE).edit();
            editor.putString("name", mName.getText().toString());
            editor.putString("email", mEmail.getText().toString());
            editor.putString("mobile", mMobile.getText().toString());
            editor.putString("about", mAbout.getText().toString());
            editor.putString("professionId", professionId);
            editor.apply();

            SharedPreferences prefs = getActivity().getSharedPreferences(getActivity().getPackageName(), MODE_PRIVATE);
            Log.e("ServiceDetailFragment" ,"PrfessionId = " +  prefs.getString("professionId", null));

        return true;

    }
        return false;
    }

    @Override
    public void selectProfession(ProfessionModel p) {
        dialog.hide();
        mProfession.setText(p.profession);
        professionId = String.valueOf(p.id);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                Uri selectedImageUri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImageUri);
                    String filePath = getRealPathFromURIPath(selectedImageUri);
                    File file = new File(filePath);
                    AppConstants.log(TAG + "filename", file.getName());
                    RequestBody mFile = RequestBody.create(MediaType.parse("image/*"), file);
                    MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("avatar", file.getName(), mFile);
                    RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), file.getName());
                    mAvatar.setImageBitmap(bitmap);
                    uploadImage(filename, fileToUpload);
                } catch (IOException e) {
                    notification.setMessage("Unable to pick picture");
                    notification.show();
                }
            }
        }
    }

    private String getRealPathFromURIPath(Uri contentURI) {
        Cursor cursor = getActivity().getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }

    private void uploadImage(RequestBody filename, MultipartBody.Part fileToUpload) {
        //progressBar.setVisibility(View.VISIBLE);
        RequestBody token = RequestBody.create(MediaType.parse("text/plain"), user.getToken());
        Toast.makeText(getContext(), "Uploading Image", Toast.LENGTH_SHORT).show();
        retrofitClient = new RetrofitClient(getContext(), AppConstants.getHost());
        retrofitClient.getApiService().uploadImage(token, fileToUpload, filename)
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
                            msg = "A Server Error occurred. Please Try Again";
                        if(e instanceof SocketException || e instanceof SocketTimeoutException)
                            msg = "No Internet Connection";

                        notification.setMessage(msg);
                        notification.setAnchor(mProfession);
                        notification.show();
                        //progressBar.setVisibility(View.INVISIBLE);
                        AppConstants.log(TAG, e.toString());
                    }
                    @Override
                    public void onNext(ResponseObjectModel responseModel) {

                        if(responseModel.getStatus().equals(AppConstants.STATUS_SUCCESS)){
                            AppConstants.log(TAG, responseModel.toString());
//                            user.setAvatar(responseModel.getData().replace('"', ' ').trim());
                            Log.e(TAG, filename.toString());
//                            app.setUser(user);
                        }else{
                            notification.setMessage(responseModel.getMessage());
                            notification.setAnchor(mProfession);
                            notification.show();
                        }
                        //progressBar.setVisibility(View.INVISIBLE);
                    }
                });
    }

}
