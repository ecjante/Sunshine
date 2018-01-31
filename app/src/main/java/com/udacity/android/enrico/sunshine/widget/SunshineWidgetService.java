package com.udacity.android.enrico.sunshine.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.Nullable;

import com.udacity.android.enrico.sunshine.MainActivity;
import com.udacity.android.enrico.sunshine.data.WeatherContract;
import com.udacity.android.enrico.sunshine.data.WeatherData;

/**
 * Created by enrico on 1/30/18.
 */

public class SunshineWidgetService extends IntentService {

    public static final String ACTION_UPDATE = "com.udacity.android.enrico.sunshine.action.update";

    public SunshineWidgetService() {
        super(SunshineWidgetService.class.getSimpleName());
    }

    public static void startActionUpdateSunshineWidgets(Context context) {
        Intent intent = new Intent(context, SunshineWidgetService.class);
        intent.setAction(ACTION_UPDATE);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_UPDATE.equals(action)) {
                handleActionUpdate();
            }
        }
    }

    private void handleActionUpdate() {
        Cursor cursor = getContentResolver().query(
                WeatherContract.WeatherEntry.CONTENT_URI,
                MainActivity.MAIN_FORECAST_PROJECTION,
                WeatherContract.WeatherEntry.getSqlSelectForToday(),
                null, null
        );

        WeatherData weatherData;
        if (cursor != null && cursor.moveToFirst()) {
            weatherData = WeatherData.createListData(this, cursor);
            cursor.close();
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, SunshineWidgetProvider.class));
            SunshineWidgetProvider.updateSunshineWidgets(this, appWidgetManager, weatherData, appWidgetIds);
        }
    }
}
