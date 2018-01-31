package com.udacity.android.enrico.sunshine.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.udacity.android.enrico.sunshine.MainActivity;
import com.udacity.android.enrico.sunshine.R;
import com.udacity.android.enrico.sunshine.data.WeatherData;

/**
 * Created by enrico on 1/30/18.
 */

public class SunshineWidgetProvider extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                WeatherData weatherData, int appWidgetId) {
        // Create intent to launch when widget is clicked
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                0
        );

        // Construc the remote views object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.sunshine_widget);
        views.setImageViewResource(R.id.weather_icon, weatherData.getLargeIcon());
        // Widgets allow click handlers to only launch pending intents
        views.setOnClickPendingIntent(R.id.weather_icon, pendingIntent);
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        SunshineWidgetService.startActionUpdateSunshinWidgets(context);
    }

    public static void updateSunshineWidgets(Context context, AppWidgetManager appWidgetManager,
                                             WeatherData weatherData, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, weatherData, appWidgetId);
        }
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
