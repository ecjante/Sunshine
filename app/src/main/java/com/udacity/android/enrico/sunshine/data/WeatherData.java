package com.udacity.android.enrico.sunshine.data;

import android.content.Context;
import android.database.Cursor;

import com.udacity.android.enrico.sunshine.DetailActivity;
import com.udacity.android.enrico.sunshine.MainActivity;
import com.udacity.android.enrico.sunshine.utilities.SunshineDateUtils;
import com.udacity.android.enrico.sunshine.utilities.SunshineWeatherUtils;

/**
 * Created by enrico on 1/29/18.
 */

public class WeatherData {
    private int icon;
    private int largeIcon;
    private long dateMillis;
    private String date;
    private String description;
    private String lowTemperature;
    private String highTemperature;
    private String humidty;
    private String pressure;
    private String wind;

    public static WeatherData createListData(Context context, Cursor cursor) {
        WeatherData weatherData = new WeatherData();

        weatherData.dateMillis = cursor.getLong(MainActivity.INDEX_WEATHER_DATE);
        weatherData.date = SunshineDateUtils.getFriendlyDateString(context, weatherData.dateMillis, false);

        int weatherId = cursor.getInt(MainActivity.INDEX_WEATHER_CONDITION_ID);
        weatherData.description = SunshineWeatherUtils.getStringForWeatherCondition(context, weatherId);

        weatherData.icon = SunshineWeatherUtils.getSmallArtResourceIdForWeatherCondition(weatherId);
        weatherData.largeIcon = SunshineWeatherUtils.getLargeArtResourceIdForWeatherCondition(weatherId);

        double minTemp = cursor.getDouble(MainActivity.INDEX_WEATHER_MIN_TEMP);
        weatherData.lowTemperature = SunshineWeatherUtils.formatTemperature(context, minTemp);

        double maxTemp = cursor.getDouble(MainActivity.INDEX_WEATHER_MAX_TEMP);
        weatherData.highTemperature = SunshineWeatherUtils.formatTemperature(context, maxTemp);

        return weatherData;
    }

    public static WeatherData createDetailData(Context context, Cursor cursor) {
        WeatherData weatherData = new WeatherData();

        weatherData.dateMillis = cursor.getLong(DetailActivity.INDEX_WEATHER_DATE);
        weatherData.date = SunshineDateUtils.getFriendlyDateString(context, weatherData.dateMillis, false);

        int weatherId = cursor.getInt(DetailActivity.INDEX_WEATHER_CONDITION_ID);
        weatherData.description = SunshineWeatherUtils.getStringForWeatherCondition(context, weatherId);

        weatherData.icon = SunshineWeatherUtils.getLargeArtResourceIdForWeatherCondition(weatherId);

        double minTemp = cursor.getDouble(DetailActivity.INDEX_WEATHER_MIN_TEMP);
        weatherData.lowTemperature = SunshineWeatherUtils.formatTemperature(context, minTemp);

        double maxTemp = cursor.getDouble(DetailActivity.INDEX_WEATHER_MAX_TEMP);
        weatherData.highTemperature = SunshineWeatherUtils.formatTemperature(context, maxTemp);

        float humidity = cursor.getFloat(DetailActivity.INDEX_WEATHER_HUMIDITY);
        weatherData.humidty = SunshineWeatherUtils.getFormattedHumidity(context, humidity);

        float pressure = cursor.getFloat(DetailActivity.INDEX_WEATHER_PRESSURE);
        weatherData.pressure = SunshineWeatherUtils.getFormattedPressure(context, pressure);

        float windSpeed = cursor.getFloat(DetailActivity.INDEX_WEATHER_WIND_SPEED);
        float windDirection = cursor.getFloat(DetailActivity.INDEX_WEATHER_DEGREES);
        weatherData.wind = SunshineWeatherUtils.getFormattedWind(context, windSpeed, windDirection);

        return weatherData;
    }

    public int getIcon() {
        return icon;
    }

    public int getLargeIcon() {
        return largeIcon;
    }

    public long getDateMillis() {
        return dateMillis;
    }

    public String getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public String getLowTemperature() {
        return lowTemperature;
    }

    public String getHighTemperature() {
        return highTemperature;
    }

    public String getHumidty() {
        return humidty;
    }

    public String getPressure() {
        return pressure;
    }

    public String getWind() {
        return wind;
    }

    public String getSummary() {
        return date+ " - " + description + " - " + highTemperature + "/" + lowTemperature;
    }
}
