package com.oneflaretech.kiakia.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.oneflaretech.kiakia.R;
import com.oneflaretech.kiakia.models.ServiceModel;
import com.oneflaretech.kiakia.utils.AppConstants;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ViewHolder> {
    String TAG = "Service Adapter";
    ArrayList<ServiceModel> services;
    Context context;
    public ServiceAdapter(Context context, ArrayList<ServiceModel> temp) {
        this.services = temp;
        this.context = context;
    }


    @NonNull
    @Override
    public ServiceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.list_services_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceAdapter.ViewHolder holder, int position) {
        ServiceModel service = services.get(position);
        holder.sName.setText(service.getName());
        holder.sRatingBar.setNumStars(4);
        holder.sProfession.setText(service.getProfession());
        holder.sAbout.setText(service.getAbout());

        Picasso.get()
                .load(AppConstants.getFileHost() + service.getAvatar())
                .fit()
                .placeholder(getDrawable(service.getName()))
                .into(holder.sImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        AppConstants.log(TAG, "Image Loaded" + position);
                    }

                    @Override
                    public void onError(Exception e) {
                        holder.sImage.setImageDrawable(getDrawable(service.getName()));
                    }
                });

    }

    private Drawable getDrawable(String name){
        return TextDrawable.builder().beginConfig()
                .textColor(Color.WHITE)
                .useFont(Typeface.DEFAULT)
                .toUpperCase()
                .endConfig()
                .buildRect(String.valueOf(name.charAt(0)),  ColorGenerator.MATERIAL.getRandomColor());
    }

    @Override
    public int getItemCount() {
        return services.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView sImage;
        RatingBar sRatingBar;
        TextView sName, sProfession, sAbout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            sImage = itemView.findViewById(R.id.item_image);
            sRatingBar = itemView.findViewById(R.id.ratingBar);
            sName = itemView.findViewById(R.id.txtName);
            sProfession = itemView.findViewById(R.id.txtProfession);
            sAbout = itemView.findViewById(R.id.txtAbout);
        }
    }
}
