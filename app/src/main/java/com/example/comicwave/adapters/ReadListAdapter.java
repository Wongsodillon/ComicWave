package com.example.comicwave.adapters;

import android.content.Context;
import android.content.Intent;
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
import com.example.comicwave.ComicDetailsActivity;
import com.example.comicwave.R;
import com.example.comicwave.models.Favorites;
import com.example.comicwave.models.ReadList;

import java.util.ArrayList;

public class ReadListAdapter extends RecyclerView.Adapter<ReadListAdapter.ReadListViewHolder> {

    private Context activityContext;
    private ArrayList<ReadList> readLists;
    private int layout;
    private boolean isLoading = false;

    public ReadListAdapter(ArrayList<ReadList> readLists, int layout) {
        this.readLists = readLists;
        this.layout = layout;
    }

    @Override
    public int getItemViewType(int position) {
        if (isLoading) {
            return R.layout.item_grid_skeleton;
        }
        return this.layout;
    }

    @NonNull
    @Override
    public ReadListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        activityContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(activityContext);
        View view = inflater.inflate(viewType, parent, false);
        if (viewType == R.layout.item_grid_skeleton) {
            return  new ReadListAdapter.SkeletonViewHolder(view);
        }
        return new ReadListAdapter.ReadListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReadListViewHolder holder, int position) {
        if (isLoading) {
            return;
        }
        ReadList f = readLists.get(position);
        Glide.with(activityContext)
                .load(f.getImageUrl())
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(8)))
                .into(holder.itemFavoritesImage);
        holder.itemFavoritesTitle.setText(f.getTitle());
        holder.itemView.setOnClickListener(e -> {
            Intent i = new Intent(activityContext, ComicDetailsActivity.class);
            i.putExtra("comicId", f.getComicId());
            activityContext.startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        if (isLoading) {
            return 9;
        }
        return readLists.size();
    }

    public void setLoading(boolean isLoading) {
        this.isLoading = isLoading;
        notifyDataSetChanged();
    }

    public static class ReadListViewHolder extends RecyclerView.ViewHolder {
        LinearLayout itemFavoritesLayout;
        TextView itemFavoritesTitle;
        ImageView itemFavoritesImage;
        public ReadListViewHolder(@NonNull View itemView) {
            super(itemView);
            itemFavoritesLayout = itemView.findViewById(R.id.itemFavoritesLayout);
            itemFavoritesTitle = itemView.findViewById(R.id.itemFavoritesTitle);
            itemFavoritesImage = itemView.findViewById(R.id.itemFavoritesImage);
        }
    }

    public static class SkeletonViewHolder extends ReadListViewHolder {
        public SkeletonViewHolder(View itemView) {
            super(itemView);
        }
    }

}
