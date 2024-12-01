package com.example.comicwave.adapters;

import android.content.Context;
import android.content.Intent;
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
import com.example.comicwave.ComicDetailsActivity;
import com.example.comicwave.R;
import com.example.comicwave.models.Favorites;

import java.util.ArrayList;

public class FavoritesSliderAdapter extends RecyclerView.Adapter<FavoritesSliderAdapter.FavoritesSliderViewHolder> {

    private Context activityContext;
    private ArrayList<Favorites> favoriteList;
    private int layout;
    private boolean isLoading = false;

    public FavoritesSliderAdapter(ArrayList<Favorites> favoriteList, int layout) {
        this.favoriteList = favoriteList;
        this.layout = layout;
    }

    @Override
    public int getItemViewType(int position) {
        if (isLoading) {
            return R.layout.item_slider_skeleton;
        }
        if (position >= favoriteList.size()) {
            return R.layout.item_slider_placeholder;
        }
        return this.layout;
    }

    @NonNull
    @Override
    public FavoritesSliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.activityContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(this.activityContext);
        View view = inflater.inflate(viewType, parent, false);
        if (viewType == R.layout.item_slider_skeleton) {
            return new SkeletonViewHolder(view);
        } else if (viewType == R.layout.item_slider_placeholder) {
            return new PlaceholderViewHolder(view);
        }
        return new FavoritesSliderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoritesSliderViewHolder holder, int position) {
        if (isLoading || position >= favoriteList.size() || position > 5) {
            return;
        }
        Favorites favorite = favoriteList.get(position);
        holder.itemSliderTitle.setText(favorite.getTitle());
        Glide.with(activityContext)
                .load(favorite.getImageUrl())
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(8)))
                .into(holder.itemSliderImage);
        holder.itemView.setOnClickListener(e -> {
            Intent i = new Intent(activityContext, ComicDetailsActivity.class);
            i.putExtra("comicId", favorite.getComicId());
            activityContext.startActivity(i);
        });

    }

    @Override
    public int getItemCount() {
        return 5;
    }

    public void setLoading(boolean isLoading) {
        this.isLoading = isLoading;
        notifyDataSetChanged();
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

    public static class SkeletonViewHolder extends FavoritesSliderViewHolder  {
        public SkeletonViewHolder(View itemView) {
            super(itemView);
        }
    }

    public static class PlaceholderViewHolder extends FavoritesSliderViewHolder {
        public PlaceholderViewHolder(View itemView) {
            super(itemView);
        }
    }

}
