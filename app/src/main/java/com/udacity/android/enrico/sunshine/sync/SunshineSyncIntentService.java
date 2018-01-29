package com.udacity.android.enrico.sunshine.sync;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

/**
 * Created by enrico on 1/29/18.
 */

public class SunshineSyncIntentService extends IntentService {
    public SunshineSyncIntentService() {
        super(SunshineSyncIntentService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        SunshineSyncTask.syncWeather(this);
    }
}
