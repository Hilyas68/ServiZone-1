package com.oneflaretech.kiakia.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.oneflaretech.kiakia.R;
import com.oneflaretech.kiakia.models.ReviewModel;

import java.util.ArrayList;

/**
 * Created by kolawoleadewale on 10/7/17.
 */

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {

    private final ArrayList<ReviewModel> data;
    private final Context context;

    public ReviewsAdapter(ArrayList<ReviewModel> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }


    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView rImage;
        RatingBar rRatingBar;
        TextView rName, rMessage, rDate;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rImage = itemView.findViewById(R.id.item_image);
            rRatingBar = itemView.findViewById(R.id.ratingBar);
            rName = itemView.findViewById(R.id.item_name);
            rDate = itemView.findViewById(R.id.date_created);
            rMessage = itemView.findViewById(R.id.item_content);
        }
    }

    private Drawable getDrawable(String name){
        return TextDrawable.builder().beginConfig()
                .textColor(Color.WHITE)
                .useFont(Typeface.DEFAULT)
                .toUpperCase()
                .endConfig()
                .buildRect(String.valueOf(name.charAt(0)),  ColorGenerator.MATERIAL.getRandomColor());
    }
}