package com.example.comicwave.adapters;

import android.content.Context;
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
        LayoutInflater inflater = LayoutInflater.from(activityContext);
        View view = inflater.inflate(this.layout, parent, false);
        return new ComicSliderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ComicSliderViewHolder holder, int position) {
        Comic comic = comicList.get(position);
        holder.itemSliderTitle.setText(comic.getTitle());
        Glide.with(activityContext)
                .load(comic.getImageUrl())
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(8)))
                .into(holder.itemSliderImage);
    }

    @Override
    public int getItemCount() {
        return comicList.size();
    }

    public static class ComicSliderViewHolder extends RecyclerView.ViewHolder {
        ImageView itemSliderImage;
        TextView itemSliderTitle;
        public ComicSliderViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemSliderImage = itemView.findViewById(R.id.itemSliderImage);
            this.itemSliderTitle = itemView.findViewById(R.id.itemSliderTitle);
        }
    }

}
