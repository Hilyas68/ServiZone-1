package com.fincoapps.servizone.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fincoapps.servizone.R;
import com.fincoapps.servizone.models.ReviewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kolawoleadewale on 10/7/17.
 */

public class ReviewsAdapter extends ArrayAdapter<ReviewModel.Data> {

    private static class ViewHolder {
        TextView txtName, txtMessage, rating;
        RatingBar ratingBar;
        ImageView userImage;
    }

    private final List<ReviewModel.Data> data;
    private final Context context;
    int lastPosition = -1;

    public ReviewsAdapter(List<ReviewModel.Data> data, Context context) {
        super(context, R.layout.row_review_item, data);
        this.data = data;
        this.context = context;
    }

    public long getItemId(int position) {
        return position;
    }

    //========================= GET THE ROW  LOGIC HAPPENS HERE=============================
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        //================ GET REFERENCE TO ROW VIEWS ================
        viewHolder = new ViewHolder();
        LayoutInflater inflater = LayoutInflater.from(getContext());
        convertView = inflater.inflate(R.layout.row_review_item, parent, false);
        viewHolder.ratingBar = (RatingBar) convertView.findViewById(R.id.rating);
        viewHolder.txtName = (TextView) convertView.findViewById(R.id.txtName);
        viewHolder.txtMessage = (TextView) convertView.findViewById(R.id.txtMessage);
        convertView.setTag(viewHolder);


        //================= SET THE VALUES OF THE ROW =================
        ReviewModel.Data reviewModel = data.get(position);
        viewHolder.txtName.setText(reviewModel.user.name);
        viewHolder.txtMessage.setText(reviewModel.message);
        viewHolder.ratingBar.setRating(Float.parseFloat(reviewModel.rating));

//        Glide.with(viewHolder.userImage.getContext())
//                .load(reviewModel.thumbnailUrl)
//                .placeholder(R.drawable.placeholder)
//                .fitCenter()
//                .into(viewHolder.userImage);

        return convertView;
    }
}