package com.example.comicwave.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.comicwave.R;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class EpisodeContentAdapter extends RecyclerView.Adapter<EpisodeContentAdapter.EpisodeContentViewHolder> {

    private ArrayList<String> contents;
    private Context activityContext;
    public EpisodeContentAdapter(ArrayList<String> contents) {
        this.contents = contents;
    }

    @NonNull
    @Override
    public EpisodeContentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        activityContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(activityContext);
        View view = inflater.inflate(R.layout.item_episode_content, parent, false);
        return new EpisodeContentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EpisodeContentViewHolder holder, int position) {
        String content = contents.get(position);
        Glide.with(activityContext).load(content).into(holder.episodeContent);
    }

    @Override
    public int getItemCount() {
        return contents.size();
    }

    public static class EpisodeContentViewHolder extends RecyclerView.ViewHolder {
        ImageView episodeContent;
        public EpisodeContentViewHolder(@NonNull View itemView) {
            super(itemView);
            episodeContent = itemView.findViewById(R.id.episodeContent);
        }
    }

}
