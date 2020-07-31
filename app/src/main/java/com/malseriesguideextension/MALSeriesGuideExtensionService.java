package com.malseriesguideextension;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.battlelancer.seriesguide.api.Action;
import com.battlelancer.seriesguide.api.Episode;
import com.battlelancer.seriesguide.api.SeriesGuideExtension;

public class MALSeriesGuideExtensionService extends SeriesGuideExtension {
    private static final String TAG = "MALSeriesGuideExtension";

    public MALSeriesGuideExtensionService() {
        super("MALSeriesGuideExtension");
        Log.d(TAG, "Constructor called");
    }

    @Override
    protected void onRequest(int episodeIdentifier, Episode episode) {
        Log.d(TAG, "onRequest() called");

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://myanimelist.net"));

        publishAction(new Action.Builder(getString(R.string.action_label_episode), episodeIdentifier)
                        .viewIntent(intent).build());
    }
}
