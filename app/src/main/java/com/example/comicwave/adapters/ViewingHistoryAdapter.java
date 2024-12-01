package com.example.comicwave.adapters;

import android.content.Context;
import android.content.Intent;
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
import com.example.comicwave.models.ViewingHistory;

import java.util.ArrayList;

public class ViewingHistoryAdapter extends RecyclerView.Adapter<ViewingHistoryAdapter.ViewingHistoryViewHolder> {

    private final int layout;
    private final ArrayList<ViewingHistory> viewingHistories;
    private Context activityContext;
    private boolean isLoading = false;

    public ViewingHistoryAdapter(ArrayList<ViewingHistory> viewingHistories, int layout) {
        this.viewingHistories = viewingHistories;
        this.layout = layout;
    }

    public int getItemViewType(int position) {
        return isLoading ? R.layout.item_slider_skeleton : this.layout;
    }

    @NonNull
    @Override
    public ViewingHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.activityContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(activityContext);
        View view = inflater.inflate(viewType, parent, false);
        if (viewType == R.layout.item_slider_skeleton) {
            return new SkeletonViewHolder(view);
        }
        return new ViewingHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewingHistoryViewHolder holder, int position) {
        if (isLoading) {
            return;
        }
        ViewingHistory comic = viewingHistories.get(position);
        holder.itemSliderTitle.setText(comic.getComicTitle());
        Glide.with(activityContext)
                .load(comic.getImageUrl())
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(8)))
                .into(holder.itemSliderImage);
        holder.itemView.setOnClickListener(e -> {
            Intent i = new Intent(activityContext, ComicDetailsActivity.class);
            i.putExtra("comicId", comic.getComicId());
            activityContext.startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return isLoading ? 5 : viewingHistories.size();
    }

    public void setLoading(boolean isLoading) {
        this.isLoading = isLoading;
        notifyDataSetChanged();
    }

    public static class ViewingHistoryViewHolder extends RecyclerView.ViewHolder {
        ImageView itemSliderImage;
        TextView itemSliderTitle;
        public ViewingHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemSliderImage = itemView.findViewById(R.id.itemSliderImage);
            this.itemSliderTitle = itemView.findViewById(R.id.itemSliderTitle);
        }
    }
    public static class SkeletonViewHolder extends ViewingHistoryViewHolder {
        public SkeletonViewHolder(View itemView) {
            super(itemView);
        }
    }
}
