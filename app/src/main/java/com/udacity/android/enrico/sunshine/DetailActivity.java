package com.udacity.android.enrico.sunshine;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.udacity.android.enrico.sunshine.data.WeatherContract.WeatherEntry;
import com.udacity.android.enrico.sunshine.utilities.SunshineDateUtils;
import com.udacity.android.enrico.sunshine.utilities.SunshineWeatherUtils;

public class DetailActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";

    public static final String[] DETAIL_PROJECTION = {
            WeatherEntry.COLUMN_DATE,
            WeatherEntry.COLUMN_MAX_TEMP,
            WeatherEntry.COLUMN_MIN_TEMP,
            WeatherEntry.COLUMN_HUMIDITY,
            WeatherEntry.COLUMN_PRESSURE,
            WeatherEntry.COLUMN_WIND_SPEED,
            WeatherEntry.COLUMN_DEGREES,
            WeatherEntry.COLUMN_WEATHER_ID
    };

    public static final int INDEX_WEATHER_DATE = 0;
    public static final int INDEX_WEATHER_MAX_TEMP = 1;
    public static final int INDEX_WEATHER_MIN_TEMP = 2;
    public static final int INDEX_WEATHER_HUMIDITY = 3;
    public static final int INDEX_WEATHER_PRESSURE = 4;
    public static final int INDEX_WEATHER_WIND_SPEED = 5;
    public static final int INDEX_WEATHER_DEGREES = 6;
    public static final int INDEX_WEATHER_CONDITION_ID = 7;

    private static final int LOADER_ID = 44;

    private String mWeatherDetails;

    private Uri mUri;

    private TextView mDate, mDescription, mHigh, mLow, mHumidity, mWind, mPressure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mDate = findViewById(R.id.date);
        mDescription = findViewById(R.id.weather_description);
        mHigh = findViewById(R.id.high_temperature);
        mLow = findViewById(R.id.low_temperature);
        mHumidity = findViewById(R.id.humidity);
        mWind = findViewById(R.id.wind);
        mPressure = findViewById(R.id.pressure);

        Intent intent = getIntent();
        if (intent != null) {
            mUri = intent.getData();
            if (mUri == null) {
                throw new NullPointerException("URI for DetailActivity cannot be null");
            }
        }

        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        switch (i) {
            case LOADER_ID:

                return new CursorLoader(
                        this,
                        mUri,
                        DETAIL_PROJECTION,
                        null, null, null
                );
            default:
                throw new RuntimeException("Loader Not Implemented: " + i);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        boolean cursorHasValidData = false;
        if (data != null && data.moveToFirst()) {
            cursorHasValidData = true;
        }

        if (cursorHasValidData) {
            long date = data.getLong(INDEX_WEATHER_DATE);
            String readableDate = SunshineDateUtils.getFriendlyDateString(this, date, true);
            mDate.setText(readableDate);

            int weatherId = data.getInt(INDEX_WEATHER_CONDITION_ID);
            String description = SunshineWeatherUtils.getStringForWeatherCondition(this, weatherId);
            mDescription.setText(description);

            double minTemp = data.getDouble(INDEX_WEATHER_MIN_TEMP);
            String lowTemp = SunshineWeatherUtils.formatTemperature(this, minTemp);
            mLow.setText(lowTemp);

            double maxTemp = data.getDouble(INDEX_WEATHER_MAX_TEMP);
            String highTemp = SunshineWeatherUtils.formatTemperature(this, maxTemp);
            mHigh.setText(highTemp);

            float humidity = data.getFloat(INDEX_WEATHER_HUMIDITY);
            String humidityStr = SunshineWeatherUtils.getFormattedHumidity(this, humidity);
            mHumidity.setText(humidityStr);

            float pressure = data.getFloat(INDEX_WEATHER_PRESSURE);
            String pressureStr = SunshineWeatherUtils.getFormattedPressure(this, pressure);
            mPressure.setText(pressureStr);

            float windSpeed = data.getFloat(INDEX_WEATHER_WIND_SPEED);
            float windDirection = data.getFloat(INDEX_WEATHER_DEGREES);
            String windStr = SunshineWeatherUtils.getFormattedWind(this, windSpeed, windDirection);
            mWind.setText(windStr);

            mWeatherDetails = readableDate + " - " + description + " - " + lowTemp + "/" + highTemp;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);
        MenuItem item = menu.findItem(R.id.action_share);
        item.setIntent(createShareDetailsIntent());
        return true;
    }

    private Intent createShareDetailsIntent() {
        return ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setText(mWeatherDetails)
                .getIntent();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemSelected = item.getItemId();
        switch (itemSelected) {
            case R.id.action_settings:
                SettingsActivity.launch(this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
