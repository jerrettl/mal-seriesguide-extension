package com.malseriesguideextension.viewmodel;

import androidx.lifecycle.ViewModel;
import com.malseriesguideextension.AnimeSearchResult;
import java.util.List;

public class ResultViewModel extends ViewModel {
    private List<AnimeSearchResult> results = null;

    public List<AnimeSearchResult> getResults() {
        return results;
    }

    public void setResults(List<AnimeSearchResult> results) {
        this.results = results;
    }
}
