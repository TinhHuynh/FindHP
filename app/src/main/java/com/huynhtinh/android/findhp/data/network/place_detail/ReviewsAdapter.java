package com.huynhtinh.android.findhp.data.network.place_detail;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.huynhtinh.android.findhp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Chip Caber on 24-Mar-18.
 */

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {
    private Context context;
    private List<Review> reviewList;


    public ReviewsAdapter(Context context, List<Review> reviewList) {
        this.context = context;
        this.reviewList = reviewList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_review_item, parent, false);
        return new ReviewsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Review review = reviewList.get(position);
        Picasso.with(context).load(review.getProfilePhotoUrl()).into(holder.profilePhotoImg);
        holder.authorNameTv.setText(review.getAuthorName());
        holder.reviewTextTv.setText(review.getText());
        holder.reviewRatingBar.setEnabled(false);
        holder.reviewRatingBar.setMax(5);
        holder.reviewRatingBar.setStepSize(0.1f);
        holder.reviewRatingBar.setRating(review.getRating());
        holder.reviewRatingTv.setText("(" + review.getRating() + ")");
        holder.reviewTimeTv.setText(review.getTime());
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView profilePhotoImg;
        private TextView authorNameTv;
        private TextView reviewTimeTv;
        private TextView reviewTextTv;
        private RatingBar reviewRatingBar;
        private TextView reviewRatingTv;

        public ViewHolder(View itemView) {
            super(itemView);
            profilePhotoImg = (ImageView) itemView.findViewById(R.id.author_photo_image_view);
            authorNameTv = (TextView) itemView.findViewById(R.id.author_text_view);
            reviewTextTv = (TextView) itemView.findViewById(R.id.review_text_text_view);
            reviewRatingBar = (RatingBar) itemView.findViewById(R.id.review_rating_bar);
            reviewRatingTv = (TextView) itemView.findViewById(R.id.review_rating_text_view);
            reviewTimeTv = (TextView) itemView.findViewById(R.id.review_date_text_view);
        }
    }
}
