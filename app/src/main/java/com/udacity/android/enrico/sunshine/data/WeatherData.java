package com.udacity.android.enrico.sunshine.data;

import android.content.Context;
import android.database.Cursor;

import com.udacity.android.enrico.sunshine.DetailActivity;
import com.udacity.android.enrico.sunshine.MainActivity;
import com.udacity.android.enrico.sunshine.R;
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
    private String humidity;
    private String pressure;
    private String wind;

    // Accessibility variables
    private String descriptionA11y;
    private String lowTempA11y;
    private String highTempA11y;
    private String humidityA11y;
    private String pressureA11y;
    private String windA11y;

    public static WeatherData createListData(Context context, Cursor cursor) {
        WeatherData weatherData = new WeatherData();

        weatherData.dateMillis = cursor.getLong(MainActivity.INDEX_WEATHER_DATE);
        weatherData.date = SunshineDateUtils.getFriendlyDateString(context, weatherData.dateMillis, false);

        int weatherId = cursor.getInt(MainActivity.INDEX_WEATHER_CONDITION_ID);
        weatherData.description = SunshineWeatherUtils.getStringForWeatherCondition(context, weatherId);
        weatherData.descriptionA11y = context.getString(R.string.a11y_forecast, weatherData.description);

        weatherData.icon = SunshineWeatherUtils.getSmallArtResourceIdForWeatherCondition(weatherId);
        weatherData.largeIcon = SunshineWeatherUtils.getLargeArtResourceIdForWeatherCondition(weatherId);

        double minTemp = cursor.getDouble(MainActivity.INDEX_WEATHER_MIN_TEMP);
        weatherData.lowTemperature = SunshineWeatherUtils.formatTemperature(context, minTemp);
        weatherData.lowTempA11y = context.getString(R.string.a11y_low_temp, weatherData.lowTemperature);

        double maxTemp = cursor.getDouble(MainActivity.INDEX_WEATHER_MAX_TEMP);
        weatherData.highTemperature = SunshineWeatherUtils.formatTemperature(context, maxTemp);
        weatherData.highTempA11y = context.getString(R.string.a11y_high_temp, weatherData.highTemperature);

        return weatherData;
    }

    public static WeatherData createDetailData(Context context, Cursor cursor) {
        WeatherData weatherData = new WeatherData();

        weatherData.dateMillis = cursor.getLong(DetailActivity.INDEX_WEATHER_DATE);
        weatherData.date = SunshineDateUtils.getFriendlyDateString(context, weatherData.dateMillis, false);

        int weatherId = cursor.getInt(DetailActivity.INDEX_WEATHER_CONDITION_ID);
        weatherData.description = SunshineWeatherUtils.getStringForWeatherCondition(context, weatherId);
        weatherData.descriptionA11y = context.getString(R.string.a11y_forecast, weatherData.description);

        weatherData.largeIcon = SunshineWeatherUtils.getLargeArtResourceIdForWeatherCondition(weatherId);

        double minTemp = cursor.getDouble(DetailActivity.INDEX_WEATHER_MIN_TEMP);
        weatherData.lowTemperature = SunshineWeatherUtils.formatTemperature(context, minTemp);
        weatherData.lowTempA11y = context.getString(R.string.a11y_low_temp, weatherData.lowTemperature);

        double maxTemp = cursor.getDouble(DetailActivity.INDEX_WEATHER_MAX_TEMP);
        weatherData.highTemperature = SunshineWeatherUtils.formatTemperature(context, maxTemp);
        weatherData.highTempA11y = context.getString(R.string.a11y_high_temp, weatherData.highTemperature);

        float humidity = cursor.getFloat(DetailActivity.INDEX_WEATHER_HUMIDITY);
        weatherData.humidity = SunshineWeatherUtils.getFormattedHumidity(context, humidity);
        weatherData.humidityA11y = context.getString(R.string.a11y_humidity, weatherData.humidity);

        float pressure = cursor.getFloat(DetailActivity.INDEX_WEATHER_PRESSURE);
        weatherData.pressure = SunshineWeatherUtils.getFormattedPressure(context, pressure);
        weatherData.pressureA11y = context.getString(R.string.a11y_pressure, weatherData.pressure);

        float windSpeed = cursor.getFloat(DetailActivity.INDEX_WEATHER_WIND_SPEED);
        float windDirection = cursor.getFloat(DetailActivity.INDEX_WEATHER_DEGREES);
        weatherData.wind = SunshineWeatherUtils.getFormattedWind(context, windSpeed, windDirection);
        weatherData.windA11y = context.getString(R.string.a11y_wind, weatherData.wind);

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

    public String getDescriptionA11y() {
        return descriptionA11y;
    }

    public String getLowTemperature() {
        return lowTemperature;
    }

    public String getLowTempA11y() {
        return lowTempA11y;
    }

    public String getHighTemperature() {
        return highTemperature;
    }

    public String getHighTempA11y() {
        return highTempA11y;
    }

    public String getHumidity() {
        return humidity;
    }

    public String getHumidityA11y() {
        return humidityA11y;
    }

    public String getPressure() {
        return pressure;
    }

    public String getPressureA11y() {
        return pressureA11y;
    }

    public String getWind() {
        return wind;
    }

    public String getWindA11y() {
        return windA11y;
    }

    public String getSummary() {
        return date + " - " + description + " - " + highTemperature + "/" + lowTemperature;
    }
}
