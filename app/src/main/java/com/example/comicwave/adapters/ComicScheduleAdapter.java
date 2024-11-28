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
import com.example.comicwave.helpers.NumberHelper;
import com.example.comicwave.models.Comic;

import java.util.ArrayList;

public class ComicScheduleAdapter extends RecyclerView.Adapter<ComicScheduleAdapter.ComicScheduleViewHolder> {

    private Context activityContext;
    private ArrayList<Comic> comics;
    private int layout;

    public ComicScheduleAdapter(ArrayList<Comic> comics, int layout) {
        this.layout = layout;
        this.comics = comics;
    }

    @NonNull
    @Override
    public ComicScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.activityContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(activityContext);
        View view = inflater.inflate(this.layout, parent, false);
        return new ComicScheduleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ComicScheduleViewHolder holder, int position) {
        Comic comic = comics.get(position);
        Glide.with(activityContext)
                .load(comic.getImageUrl())
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(8)))
                .into(holder.itemScheduleImage);
        holder.itemScheduleTitle.setText(comic.getTitle());
        holder.itemScheduleRating.setText(comic.getRating().toString());
        holder.itemScheduleViews.setText(NumberHelper.format(comic.getTotalViews()));
        holder.itemView.setOnClickListener(e -> {
            Intent i = new Intent(activityContext, ComicDetailsActivity.class);
            i.putExtra("comicId", comic.getId());
            activityContext.startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return comics.size();
    }

    public static class ComicScheduleViewHolder extends RecyclerView.ViewHolder {
        ImageView itemScheduleImage;
        TextView itemScheduleRating, itemScheduleViews, itemScheduleTitle;
        public ComicScheduleViewHolder(@NonNull View itemView) {
            super(itemView);
            itemScheduleImage = itemView.findViewById(R.id.itemScheduleImage);
            itemScheduleRating = itemView.findViewById(R.id.itemScheduleRating);
            itemScheduleViews = itemView.findViewById(R.id.itemScheduleViews);
            itemScheduleTitle = itemView.findViewById(R.id.itemScheduleTitle);
        }
    }

}
