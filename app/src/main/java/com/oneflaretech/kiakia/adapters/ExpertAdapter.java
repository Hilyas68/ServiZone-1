package com.oneflaretech.kiakia.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.oneflaretech.kiakia.Popup;
import com.oneflaretech.kiakia.R;
import com.oneflaretech.kiakia.experts.ExpertDetailsActivity;
import com.oneflaretech.kiakia.models.ExpertModel;

import java.util.ArrayList;


public class ExpertAdapter extends ArrayAdapter<ExpertModel> {

    private final ArrayList<ExpertModel> data;
    private final Context context;
    public Popup popup;
    Fragment fragment;


    public ExpertAdapter(ArrayList<ExpertModel> data, Context context, Fragment fragment, Popup popup) {
        super(context, R.layout.row_recent_experts, data);
        this.data = data;
        this.context = context;
        this.popup = popup;
        this.fragment = fragment;
    }

    public ExpertAdapter(ArrayList<ExpertModel> data, Context context, Fragment fragment) {
        super(context, R.layout.row_recent_experts, data);
        this.data = data;
        this.context = context;
        this.fragment = fragment;
    }

    public ExpertAdapter(ArrayList<ExpertModel> data, Context context) {
        super(context, R.layout.row_recent_experts, data);
        this.data = data;
        this.context = context;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder; // view lookup cache stored in tag

        //================ GET REFERENCE TO ROW VIEWS ================
        viewHolder = new ViewHolder();
        LayoutInflater inflater = LayoutInflater.from(getContext());
        convertView = inflater.inflate(R.layout.row_recent_experts, parent, false);

        viewHolder.name = convertView.findViewById(R.id.txtName);
        viewHolder.about = convertView.findViewById(R.id.txtAbout);
        viewHolder.ratingBar = convertView.findViewById(R.id.ratingBar);
        viewHolder.expertAvatar = convertView.findViewById(R.id.expertAvatar);
        viewHolder.linearRow = convertView.findViewById(R.id.mainExpertRow);
        viewHolder.profession = convertView.findViewById(R.id.txtProfession);

        convertView.setTag(viewHolder);
        ExpertModel expert = data.get(position);


        viewHolder.name.setText(expert.name);
        viewHolder.ratingBar.setRating(expert.averageRating);
        viewHolder.profession.setText(expert.profession);
        viewHolder.about.setText(expert.about);
//        Glide.with(viewHolder.expertAvatar.getContext())
//                .load("http://servizone.net/storage" + expert.avatar)
//                .placeholder(R.drawable.placeholder)
//                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
//                .into(viewHolder.expertAvatar);

        //------------------------------- ROW CLICK LISTENER -------------------------------------
        viewHolder.linearRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("////////////////////////// LISTEN //////////////////////////");
                Gson gson = new Gson();
                String expertJson = gson.toJson(data.get(position));
                Intent i = new Intent(context, ExpertDetailsActivity.class);
                i.putExtra("expert", expertJson);
                context.startActivity(i);

                ((Activity)context).overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);

                if(fragment != null)
                    fragment.getActivity().overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
            }
        });

        return convertView;
    }

    private static class ViewHolder {
        TextView name;
        TextView about;
        TextView profession;
        RelativeLayout linearRow;
        public RatingBar ratingBar;
        public ImageView expertAvatar;
    }
}