package com.example.comicwave.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.comicwave.R;
import com.example.comicwave.models.Favorites;

import java.util.ArrayList;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.FavoritesViewHolder> {

    private Context activityContext;
    private ArrayList<Favorites> favorites;
    private int layout;
    private boolean isLoading = false;

    @Override
    public int getItemViewType(int position) {
        if (isLoading) {
            return R.layout.item_grid_skeleton;
        }
        return this.layout;
    }

    public FavoritesAdapter(ArrayList<Favorites> favorites, int layout) {
        this.favorites = favorites;
        this.layout = layout;
    }

    @NonNull
    @Override
    public FavoritesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        activityContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(activityContext);
        View view = inflater.inflate(viewType, parent, false);
        if (viewType == R.layout.item_grid_skeleton) {
            return  new SkeletonViewHolder(view);
        }
        return new FavoritesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoritesViewHolder holder, int position) {
        if (isLoading) {
            return;
        }
        Favorites f = favorites.get(position);
        Glide.with(activityContext)
                .load(f.getImageUrl())
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(8)))
                .into(holder.itemFavoritesImage);
        holder.itemFavoritesTitle.setText(f.getTitle());
    }

    @Override
    public int getItemCount() {
        if (favorites.isEmpty()) {
            return 9;
        }
        return this.favorites.size();
    }

    public void setLoading(boolean isLoading) {
        this.isLoading = isLoading;
        notifyDataSetChanged();
    }

    public static class FavoritesViewHolder extends RecyclerView.ViewHolder {
        LinearLayout itemFavoritesLayout;
        TextView itemFavoritesTitle;
        ImageView itemFavoritesImage;
        public FavoritesViewHolder(@NonNull View itemView) {
            super(itemView);
            itemFavoritesLayout = itemView.findViewById(R.id.itemFavoritesLayout);
            itemFavoritesTitle = itemView.findViewById(R.id.itemFavoritesTitle);
            itemFavoritesImage = itemView.findViewById(R.id.itemFavoritesImage);
        }
    }

    public static class SkeletonViewHolder extends FavoritesViewHolder {
        public SkeletonViewHolder(View itemView) {
            super(itemView);
        }
    }

}
