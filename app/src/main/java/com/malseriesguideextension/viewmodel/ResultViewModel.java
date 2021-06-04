package com.malseriesguideextension.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.malseriesguideextension.AnimeSearchResult;
import java.util.List;

public class ResultViewModel extends AndroidViewModel {
    private List<AnimeSearchResult> results = null;
    private RequestQueue queue;
    private boolean isLoading;

    public ResultViewModel(@NonNull Application application) {
        super(application);

        // Initialize Volley RequestQueue (to make GET request for search).
        queue = Volley.newRequestQueue(application);
        isLoading = false;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (queue != null) {
            queue.stop();
        }
    }

    public List<AnimeSearchResult> getResults() {
        return results;
    }

    public void setResults(List<AnimeSearchResult> results) {
        this.results = results;
    }

    public void addToQueue(JsonObjectRequest request) {
        if (queue != null) {
            queue.add(request);
        }
    }

    public void resetQueue(Object tag) {
        if (queue != null) {
            queue.cancelAll(tag);
            queue.stop();
            queue.start();
        }
    }

    public void setIsLoading(boolean isLoading) {
        this.isLoading = isLoading;
    }

    public boolean getIsLoading() {
        return isLoading;
    }
}
