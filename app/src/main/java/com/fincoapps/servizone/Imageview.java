package com.fincoapps.servizone;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class Imageview extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        ImageView fullimage = (ImageView) findViewById(R.id.imagefullview);

//        Picasso .load()
//                .placeholder(R.drawable.placeholder)
//                .into(fullimage);
    }
}
