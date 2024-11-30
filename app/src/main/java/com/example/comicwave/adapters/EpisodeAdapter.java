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
import com.example.comicwave.helpers.DateHelper;
import com.example.comicwave.models.Episode;

import java.util.ArrayList;

public class EpisodeAdapter extends RecyclerView.Adapter<EpisodeAdapter.EpisodeViewHolder> {

    private ArrayList<Episode> episodes;
    private Context activityContext;
    public EpisodeAdapter(ArrayList<Episode> episodes) {
        this.episodes = episodes;
    }


    @NonNull
    @Override
    public EpisodeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        activityContext = parent.getContext();
        LayoutInflater inflate = LayoutInflater.from(activityContext);
        View view = inflate.inflate(R.layout.item_episode, parent, false);
        return new EpisodeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EpisodeViewHolder holder, int position) {
        Episode episode = episodes.get(position);
        Glide.with(activityContext)
                .load(episode.getImageUrl())
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(8)))
                .into(holder.itemEpisodeImage);
        holder.itemEpisodeTitle.setText(String.format("%d. %s", episode.getEpisodeNumber(), episode.getTitle()));
        holder.itemEpisodeReleaseDate.setText(DateHelper.format(episode.getReleaseDate()));
    }

    @Override
    public int getItemCount() {
        return episodes.size();
    }

    public static class EpisodeViewHolder extends RecyclerView.ViewHolder {
        TextView itemEpisodeTitle, itemEpisodeReleaseDate;
        ImageView itemEpisodeImage;
        public EpisodeViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemEpisodeImage = itemView.findViewById(R.id.itemEpisodeImage);
            this.itemEpisodeTitle = itemView.findViewById(R.id.itemEpisodeTitle);
            this.itemEpisodeReleaseDate = itemView.findViewById(R.id.itemEpisodeReleaseDate);
        }
    }

}
