package com.example.comicwave.models;

import com.google.firebase.Timestamp;

public class ViewingHistory {
    private String lastViewedEpisodeId;
    private String nextEpisodeId;
    private Timestamp lastViewedTimestamp;

    public ViewingHistory(String lastViewedEpisodeId, String nextEpisodeId) {
        this.lastViewedEpisodeId = lastViewedEpisodeId;
        this.nextEpisodeId = nextEpisodeId;
        this.lastViewedTimestamp = Timestamp.now();
    }

    public String getLastViewedEpisodeId() {
        return lastViewedEpisodeId;
    }

    public void setLastViewedEpisodeId(String lastViewedEpisodeId) {
        this.lastViewedEpisodeId = lastViewedEpisodeId;
    }

    public String getNextEpisodeId() {
        return nextEpisodeId;
    }

    public void setNextEpisodeId(String nextEpisodeId) {
        this.nextEpisodeId = nextEpisodeId;
    }

    public Timestamp getLastViewedTimestamp() {
        return lastViewedTimestamp;
    }

    public void setLastViewedTimestamp(Timestamp lastViewedTimestamp) {
        this.lastViewedTimestamp = lastViewedTimestamp;
    }
}
