package com.oneflaretech.kiakia;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

public class Imageview extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        ImageView fullimage = findViewById(R.id.imagefullview);

//        Picasso .load()
//                .placeholder(R.drawable.placeholder)
//                .into(fullimage);
    }
}
