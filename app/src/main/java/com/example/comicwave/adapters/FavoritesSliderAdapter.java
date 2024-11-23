package com.example.comicwave.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.comicwave.R;
import com.example.comicwave.models.Favorites;

import java.util.ArrayList;

public class FavoritesSliderAdapter extends RecyclerView.Adapter<FavoritesSliderAdapter.FavoritesSliderViewHolder> {

    private Context activityContext;
    private ArrayList<Favorites> favoriteList;
    private int layout;

    public FavoritesSliderAdapter(ArrayList<Favorites> favoriteList, int layout) {
        this.favoriteList = favoriteList;
        this.layout = layout;
    }

    @NonNull
    @Override
    public FavoritesSliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.activityContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(this.activityContext);
        View view = inflater.inflate(this.layout, parent, false);
        return new FavoritesSliderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoritesSliderViewHolder holder, int position) {
        Favorites favorite = favoriteList.get(position);
        Log.d("FavoritesData", "Title: " + favorite.getTitle() + ", Image: " + favorite.getImageUrl());
        holder.itemSliderTitle.setText(favorite.getTitle());
        Glide.with(activityContext)
                .load(favorite.getImageUrl())
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(8)))
                .into(holder.itemSliderImage);
    }

    @Override
    public int getItemCount() {
        return favoriteList.size();
    }

    public static class FavoritesSliderViewHolder extends RecyclerView.ViewHolder {
        ImageView itemSliderImage;
        TextView itemSliderTitle;
        public FavoritesSliderViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemSliderImage = itemView.findViewById(R.id.itemSliderImage);
            this.itemSliderTitle = itemView.findViewById(R.id.itemSliderTitle);
        }
    }

}
