package com.malseriesguideextension;

import android.content.Intent;

import com.battlelancer.seriesguide.api.Action;
import com.battlelancer.seriesguide.api.Episode;
import com.battlelancer.seriesguide.api.SeriesGuideExtension;

public class MALSeriesGuideExtensionService extends SeriesGuideExtension {
    private static final String TAG = "MALSeriesGuideExtension";

    public MALSeriesGuideExtensionService() {
        super("MALSeriesGuideExtension");
    }

    @Override
    protected void onRequest(int episodeIdentifier, Episode episode) {
        // Get show's title and season to pass to SearchActivity
        String query = episode.getShowTitle();
        if (episode.getSeason() > 1) {
            query = query.concat(" " + getString(R.string.function_word_season) + " " + episode.getSeason());
        }

        // Create intent
        Intent intent = new Intent(this, SearchActivity.class);
        intent.putExtra(SearchActivity.SEARCH_QUERY, query);

        // Publish action to SeriesGuide
        publishAction(new Action.Builder(getString(R.string.action_label_episode), episodeIdentifier)
                        .viewIntent(intent).build());
    }
}
