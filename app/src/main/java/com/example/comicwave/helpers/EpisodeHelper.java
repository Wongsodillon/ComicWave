package com.example.comicwave.helpers;

import android.util.Log;

import com.example.comicwave.models.Episode;

import java.util.ArrayList;

public class EpisodeHelper {
    public static Integer getAvailableNexts(ArrayList<Episode> episodes, Integer position, Boolean isAscending) {
        int count = 0;
        if (isAscending) {
            for (int i = position + 1; i < episodes.size(); i++) {
                Log.d("EpisodeAdapter", "Current Episode " + episodes.get(i).getTitle());
                if (!DateHelper.isReleased(episodes.get(i).getReleaseDate())) {
                    break;
                }
                count++;
            }
        } else {
            for (int i = position - 1; i >= 0; i--) {
                Log.d("EpisodeAdapter", "Current Episode " + episodes.get(i).getTitle());
                if (!DateHelper.isReleased(episodes.get(i).getReleaseDate())) {
                    break;
                }
                count++;
            }
        }
        return count;
    }
}
