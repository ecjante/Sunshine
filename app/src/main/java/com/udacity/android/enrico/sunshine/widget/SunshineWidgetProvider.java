package com.udacity.android.enrico.sunshine.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;

import com.udacity.android.enrico.sunshine.MainActivity;
import com.udacity.android.enrico.sunshine.R;
import com.udacity.android.enrico.sunshine.data.WeatherData;

/**
 * Created by enrico on 1/30/18.
 */

public class SunshineWidgetProvider extends AppWidgetProvider {

    private static WeatherData mWeatherData;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        // Create intent to launch when widget is clicked
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                0
        );

        Bundle options = appWidgetManager.getAppWidgetOptions(appWidgetId);
        int width = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
        int columns = getCellsForSize(width);
        RemoteViews views;

        if (columns >= 4) {
            views = new RemoteViews(context.getPackageName(), R.layout.sunshine_widget_4);
            views.setTextViewText(R.id.date, mWeatherData.getDate());
            views.setTextViewText(R.id.low_temperature, mWeatherData.getLowTemperature());
        } else if (columns >= 2) {
            views = new RemoteViews(context.getPackageName(), R.layout.sunshine_widget_2);
            views.setTextViewText(R.id.low_temperature, mWeatherData.getLowTemperature());
        } else {
            views = new RemoteViews(context.getPackageName(), R.layout.sunshine_widget);
        }

        views.setImageViewResource(R.id.weather_icon, mWeatherData.getLargeIcon());
        views.setTextViewText(R.id.high_temperature, mWeatherData.getHighTemperature());

        // Widgets allow click handlers to only launch pending intents
        views.setOnClickPendingIntent(R.id.weather_icon, pendingIntent);
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        SunshineWidgetService.startActionUpdateSunshineWidgets(context);
    }

    public static void updateSunshineWidgets(Context context, AppWidgetManager appWidgetManager,
                                             WeatherData weatherData, int[] appWidgetIds) {
        mWeatherData = weatherData;
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);

        updateAppWidget(context, appWidgetManager, appWidgetId);
    }

    private static int getCellsForSize(int size) {
        int n = 2;
        while ((70 * n - 30) < size) {
            n++;
        }
        return n - 1;
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }
}
