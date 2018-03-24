package com.huynhtinh.android.findhp.data.network.place_detail;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.huynhtinh.android.findhp.R;
import com.huynhtinh.android.findhp.data.Photo;
import com.huynhtinh.android.findhp.data.network.Configs;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Chip Caber on 24-Mar-18.
 */

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder> {
    private ArrayList<Photo> photoList;
    private Context context;

    public PhotoAdapter(Context context, ArrayList<Photo> photoList) {
        this.context = context;
        this.photoList = photoList;
    }

    @Override
    public PhotoAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_image_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(photoList != null && !photoList.isEmpty()) {
            String photoUrl = "https://maps.googleapis.com/maps/api/place/photo"
                    + "?key=" + Configs.PLACE_API_KEY
                    + "&photoreference=" + photoList.get(position).getPhotoReference()
                    + "&maxwidth=" + photoList.get(position).getWidth()
                    + "&maxheight=" + photoList.get(position).getHeight();
            Picasso.with(context).load(photoUrl).into(holder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        return photoList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.item_photo);
        }
    }
}
