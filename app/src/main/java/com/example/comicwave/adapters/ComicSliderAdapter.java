package com.example.comicwave.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.comicwave.ComicDetails;
import com.example.comicwave.R;
import com.example.comicwave.models.Comic;

import java.util.List;

public class ComicSliderAdapter extends RecyclerView.Adapter<ComicSliderAdapter.ComicSliderViewHolder> {

    private Context activityContext;
    private List<Comic> comicList;
    private int layout;

    public ComicSliderAdapter(List<Comic> comicList, int layout) {
        this.comicList = comicList;
        this.layout = layout;
    }

    @NonNull
    @Override
    public ComicSliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.activityContext = parent.getContext();
        Log.d("ComicSliderAdapter", "Context: " + activityContext.toString());
        LayoutInflater inflater = LayoutInflater.from(activityContext);
        View view = inflater.inflate(this.layout, parent, false);
        return new ComicSliderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ComicSliderViewHolder holder, int position) {
        Comic comic = comicList.get(position);
        Log.d("HomeFragment", comic.getTitle());
        holder.itemSliderTitle.setText(comic.getTitle());
        Glide.with(activityContext)
                .load(comic.getImageUrl())
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(8)))
                .into(holder.itemSliderImage);
        holder.itemView.setOnClickListener(e -> {
            Toast.makeText(activityContext, "Image clicked: " + comic.getTitle(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return comicList.size();
    }

    public static class ComicSliderViewHolder extends RecyclerView.ViewHolder {
        LinearLayout itemSliderLayout;
        ImageView itemSliderImage;
        TextView itemSliderTitle;
        public ComicSliderViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemSliderLayout = itemView.findViewById(R.id.itemSliderLayout);
            this.itemSliderImage = itemView.findViewById(R.id.itemSliderImage);
            this.itemSliderTitle = itemView.findViewById(R.id.itemSliderTitle);
        }
    }

}
