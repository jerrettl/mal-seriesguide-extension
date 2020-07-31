package com.malseriesguideextension;


import android.util.Log;

import com.battlelancer.seriesguide.api.SeriesGuideExtension;
import com.battlelancer.seriesguide.api.SeriesGuideExtensionReceiver;

public class MALSeriesGuideExtensionReceiver extends SeriesGuideExtensionReceiver {
    @Override
    protected int getJobId() {
        return 1;
    }

    @Override
    protected Class<? extends SeriesGuideExtension> getExtensionClass() {
        return MALSeriesGuideExtensionService.class;
    }
}
