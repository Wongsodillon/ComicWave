package com.example.comicwave.adapters;

import android.content.Context;
import android.text.Layout;
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
import com.example.comicwave.helpers.NumberHelper;
import com.example.comicwave.models.Comic;

import java.util.ArrayList;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.SearchResultViewHolder> {
    private ArrayList<Comic> comics;
    private int layout;
    private Context activityContext;
    public SearchResultAdapter(ArrayList<Comic> comics, int layout) {
        this.comics = comics;
        this.layout = layout;
    }

    @NonNull
    @Override
    public SearchResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        activityContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(activityContext);
        View view = inflater.inflate(layout, parent, false);
        return new SearchResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchResultViewHolder holder, int position) {
        Comic comic = comics.get(position);
        holder.itemSearchTitle.setText(comic.getTitle());
        Glide.with(activityContext)
                .load(comic.getImageUrl())
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(8)))
                .into(holder.itemSearchImage);
        holder.itemSearchAuthor.setText(comic.getAuthor());
        holder.itemSearchViews.setText(NumberHelper.format(comic.getTotalViews()));
        holder.itemSearchFavorites.setText(NumberHelper.format(comic.getTotalFavorites()));
        holder.itemSearchRating.setText(comic.getRating().toString());
    }

    @Override
    public int getItemCount() {
        return this.comics.size();
    }

    public static class SearchResultViewHolder extends RecyclerView.ViewHolder {
        ImageView itemSearchImage;
        TextView itemSearchTitle, itemSearchAuthor, itemSearchViews, itemSearchFavorites, itemSearchRating;
        public SearchResultViewHolder(@NonNull View itemView) {
            super(itemView);
            itemSearchAuthor = itemView.findViewById(R.id.itemSearchAuthor);
            itemSearchImage = itemView.findViewById(R.id.itemSearchImage);
            itemSearchTitle = itemView.findViewById(R.id.itemSearchTitle);
            itemSearchFavorites = itemView.findViewById(R.id.itemSearchFavorites);
            itemSearchViews = itemView.findViewById(R.id.itemSearchViews);
            itemSearchRating = itemView.findViewById(R.id.itemSearchRating);
        }
    }
}
