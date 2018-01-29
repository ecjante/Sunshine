package com.udacity.android.enrico.sunshine.sync;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.udacity.android.enrico.sunshine.data.WeatherContract;

/**
 * Created by enrico on 1/29/18.
 */

public class SunshineSyncUtils {

    private static boolean sInitialized = false;

    synchronized public static void initialize(@NonNull final Context context) {
        if (sInitialized)
            return;

        sInitialized = true;

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
        Intent intent = new Intent(context, SunshineSyncIntentService.class);
        context.startService(intent);
    }
}
