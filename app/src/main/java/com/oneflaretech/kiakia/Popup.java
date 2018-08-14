package com.oneflaretech.kiakia;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.android.volley.RequestQueue;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oneflaretech.kiakia.adapters.ProfessionsAdapter;
import com.oneflaretech.kiakia.models.ExpertModel;
import com.oneflaretech.kiakia.models.ProfessionModel;
import com.oneflaretech.kiakia.utils.AppSettings;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Popup {

    private final ListView listView2;
    private final ProfessionsAdapter adapter2;
    //    private final ListView listViewHistory;
//    private final ExpertAdapter historyAdapter;
    public Dialog dialog;
    Context context;
    RequestQueue queue;
    String title = "Users";
    String event_id = "1";
    ListView listViewUsers;
    ArrayList<ProfessionModel> professionList = new ArrayList<ProfessionModel>();
    List pList1;
    List pList2;
    EditText editTextSearch;
    ArrayList<ExpertModel> historyList = new ArrayList<ExpertModel>();
    ArrayAdapter adapter;
    QuickSearchPopup fragment;
    AppSettings app;

    public Popup(Context context, QuickSearchPopup fragment){
        this.context = context;
        this.fragment = fragment;
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.popup_professions);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
//        dialog.getWindow().setDimAmount(0.0f);
        int width = (int)(context.getResources().getDisplayMetrics().widthPixels*0.95);
        int height = (int)(context.getResources().getDisplayMetrics().heightPixels*0.80);
        dialog.getWindow().setLayout(width, height);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        app = new AppSettings(context);


        //================= PROFESSION LISTVIEW ====================
        Gson gson = new Gson();
        Type collectionType = new TypeToken<List<ProfessionModel>>(){}.getType();
        String p = app.getProfessions();
            professionList.addAll(gson.fromJson(p, collectionType));
            pList1 = professionList.subList(0, professionList.size() / 2);
            pList2 = professionList.subList((professionList.size() / 2), professionList.size());
            listViewUsers = dialog.findViewById(R.id.professionslist);
            adapter = new ProfessionsAdapter(pList1, this.context, fragment, this);
            listViewUsers.setAdapter(adapter);

            //================= EXPERTS LISTVIEW ====================
            listView2 = dialog.findViewById(R.id.listViewHistory);
            adapter2 = new ProfessionsAdapter(pList2, this.context, fragment, this);
            listView2.setAdapter(adapter2);



        //====================== SEARCH EDIT TEXT VIEW ======================
        editTextSearch = dialog.findViewById(R.id.searchEditTextView);
        editTextSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                adapter.getFilter().filter(cs);
//                adapter2.getFilter().filter(cs);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });
    }

    public void show(){
        dialog.show();
    }

}
