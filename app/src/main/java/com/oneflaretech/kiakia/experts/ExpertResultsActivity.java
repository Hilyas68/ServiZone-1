package com.oneflaretech.kiakia.experts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.oneflaretech.kiakia.R;
import com.oneflaretech.kiakia.activities.MainActivity;
import com.oneflaretech.kiakia.adapters.ExpertAdapter;
import com.oneflaretech.kiakia.models.ExpertModel;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

import static java.lang.System.out;

public class ExpertResultsActivity extends Activity {

    private Type collectionType;
    private ArrayList<ExpertModel> expertsList = new ArrayList<ExpertModel>();
    private ListView listViewExperts;
    private ExpertAdapter expertsAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        out.println("Recent Contacted Professional Page");
        setContentView(R.layout.activity_expert_results);
        ButterKnife.bind(this);

        //================= EXPERTS RESULT LISTVIEW ====================
        Gson gson = new Gson();
        collectionType = new TypeToken<List<ExpertModel>>(){}.getType();
        Intent i = getIntent();
        String results = i.getStringExtra("results");
        out.println("================== " + results);
        expertsList.addAll(gson.fromJson(results, collectionType));
        listViewExperts = findViewById(R.id.listViewResults);
        expertsAdapter = new ExpertAdapter(expertsList, this);
        listViewExperts.setAdapter(expertsAdapter);

        ImageButton imageButton = findViewById(R.id.resultback);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ExpertResultsActivity.this, MainActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
            }
        });
    }
}