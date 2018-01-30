package com.udacity.android.enrico.sunshine.sync;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;
import com.udacity.android.enrico.sunshine.MainActivity;
import com.udacity.android.enrico.sunshine.data.WeatherContract;

import java.util.concurrent.TimeUnit;

/**
 * Created by enrico on 1/29/18.
 */

public class SunshineSyncUtils {

    private static final int SYNC_INTERVAL_HOURS = 3;
    private static final int SYNC_INTERVAL_SECONDS = (int) TimeUnit.HOURS.toSeconds(SYNC_INTERVAL_HOURS);
    private static final int SYNC_FLEXTIME_SECONDS = SYNC_INTERVAL_SECONDS / SYNC_INTERVAL_HOURS;

    private static final String SUNSHINE_SYNC_TAG = "sunshine-sync";

    private static boolean sInitialized = false;

    static void scheduleFirebaseJobDispatcherSync(@NonNull final Context context) {
        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        Job job = dispatcher.newJobBuilder()
                .setService(SunshineFirebaseJobService.class)
                .setTag(SUNSHINE_SYNC_TAG)
                .setRecurring(true)
                .setLifetime(Lifetime.FOREVER)
                .setTrigger(Trigger.executionWindow(
                        SYNC_INTERVAL_SECONDS, SYNC_INTERVAL_SECONDS + SYNC_FLEXTIME_SECONDS
                ))
//                For testing purposes
//                .setTrigger(Trigger.executionWindow(
//                        10, 15
//                ))
                .setReplaceCurrent(true)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .build();

        dispatcher.schedule(job);
    }

    synchronized public static void initialize(@NonNull final Context context) {
        if (sInitialized)
            return;

        sInitialized = true;

        scheduleFirebaseJobDispatcherSync(context);

        Thread checkEmpty = new Thread(new Runnable() {
            @Override
            public void run() {
                Uri uri = WeatherContract.WeatherEntry.CONTENT_URI;
                String[] projection = new String[] { WeatherContract.WeatherEntry._ID };
                String selectionStatement = WeatherContract.WeatherEntry
                        .getSqlSelectForTodayOnwards();

                Cursor cursor = context.getContentResolver().query(
                        uri,
                        projection,
                        selectionStatement,
                        null,
                        null
                );

                if (cursor == null || cursor.getCount() == 0) {
                    startImmediateSync(context);
                }

                if (cursor != null) cursor.close();
            }
        });

        checkEmpty.start();
    }

    public static void startImmediateSync(@NonNull final Context context) {
        MainActivity.sSyncData = true;
        Intent intent = new Intent(context, SunshineSyncIntentService.class);
        context.startService(intent);
    }
}
