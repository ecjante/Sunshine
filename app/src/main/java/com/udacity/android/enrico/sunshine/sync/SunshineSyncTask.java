package com.udacity.android.enrico.sunshine.sync;

import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.format.DateUtils;

import com.udacity.android.enrico.sunshine.MainActivity;
import com.udacity.android.enrico.sunshine.R;
import com.udacity.android.enrico.sunshine.data.SunshinePreferences;
import com.udacity.android.enrico.sunshine.data.WeatherContract;
import com.udacity.android.enrico.sunshine.utilities.NetworkUtils;
import com.udacity.android.enrico.sunshine.utilities.NotificationUtils;
import com.udacity.android.enrico.sunshine.utilities.OpenWeatherJsonUtils;

import java.net.URL;

import static com.udacity.android.enrico.sunshine.MainActivity.MAIN_FORECAST_PROJECTION;

/**
 * Created by enrico on 1/29/18.
 */

public class SunshineSyncTask {

    synchronized public static void syncWeather(Context context) {
        try {
            // Get query url
            URL url = NetworkUtils.getUrl(context);
            // Get string response data
            String response = NetworkUtils.getResponseFromHttpUrl(url);

            // Convert string response to array of ContentValues
            ContentValues[] values = OpenWeatherJsonUtils.getWeatherContentValuesFromJson(context, response);

            // Check if values is valid and has data
            if (values != null && values.length > 0) {
                ContentResolver resolver = context.getContentResolver();
                Uri uri = WeatherContract.WeatherEntry.CONTENT_URI;

                // Clear table
                resolver.delete(uri, null, null);

                // Bulk insert new data
                resolver.bulkInsert(uri, values);

                boolean notificationsEnabled = SunshinePreferences.areNotificationsEnabled(context);


                long timeSinceLastNotification = SunshinePreferences
                        .getEllapsedTimeSinceLastNotification(context);

                boolean oneDayPassedSinceLastNotification = false;

                if (timeSinceLastNotification >= DateUtils.DAY_IN_MILLIS) {
                    oneDayPassedSinceLastNotification = true;
                }

                if (notificationsEnabled && oneDayPassedSinceLastNotification) {
                    NotificationUtils.notifyUserOfNewWeather(context);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
