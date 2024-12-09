package com.example.comicwave.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.comicwave.EpisodeContentActivity;
import com.example.comicwave.R;
import com.example.comicwave.helpers.DateHelper;
import com.example.comicwave.models.Episode;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.GrayscaleTransformation;

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

    private Integer getTotalNexts(Integer position) {
        int i = position;
        Log.d("EpisodeContent", "getTotalNexts " + position);
        for (; i < episodes.size(); i++) {
            if (!DateHelper.isReleased(episodes.get(i).getReleaseDate())) {
                break;
            }
        }
        Log.d("EpisodeContent", "nexts " + i);
        return i - position - 1;
    }

    @Override
    public void onBindViewHolder(@NonNull EpisodeViewHolder holder, int position) {
        Episode episode = episodes.get(position);
        if (DateHelper.isReleased(episode.getReleaseDate())) {
            Glide.with(activityContext)
                    .load(episode.getImageUrl())
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(8)))
                    .into(holder.itemEpisodeImage);
            if (DateHelper.isReleased(episode.getReleaseDate())) {
                holder.itemView.setOnClickListener(e -> {
                    Intent i = new Intent(activityContext, EpisodeContentActivity.class);
                    i.putExtra("episodeId", episode.getEpisodeId());
                    i.putExtra("comicId", episode.getComicId());
                    i.putExtra("nexts", getTotalNexts(episode.getEpisodeNumber() - 1));
                    i.putExtra("prevs", episode.getEpisodeNumber() - 1);
                    activityContext.startActivity(i);
                });
            }
            holder.itemEpisodeReleaseDate.setText(DateHelper.format(episode.getReleaseDate()));
        } else {
            Glide.with(activityContext)
                    .load(episode.getImageUrl())
                    .apply(RequestOptions.bitmapTransform(new MultiTransformation<Bitmap>(
                            new BlurTransformation(7, 3),
                            new RoundedCorners(8),
                            new GrayscaleTransformation()
                    )))
                    .into(holder.itemEpisodeImage);
            holder.itemEpisodeReleaseDate.setText(String.format("Coming soon %s", DateHelper.format(episode.getReleaseDate())));
        }
        holder.itemEpisodeTitle.setText(String.format("%d. %s", episode.getEpisodeNumber(), episode.getTitle()));
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
