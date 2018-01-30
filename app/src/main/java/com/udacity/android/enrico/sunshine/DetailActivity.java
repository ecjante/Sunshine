package com.udacity.android.enrico.sunshine;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.udacity.android.enrico.sunshine.data.WeatherContract.WeatherEntry;
import com.udacity.android.enrico.sunshine.data.WeatherData;
import com.udacity.android.enrico.sunshine.databinding.ActivityDetailBinding;
import com.udacity.android.enrico.sunshine.utilities.SunshineDateUtils;
import com.udacity.android.enrico.sunshine.utilities.SunshineWeatherUtils;

import timber.log.Timber;

public class DetailActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<WeatherData> {

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

    ActivityDetailBinding mBinding;

    private String mWeatherDetails;

    private Uri mUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);

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
    public Loader<WeatherData> onCreateLoader(int i, Bundle bundle) {
        switch (i) {
            case LOADER_ID:

                return new AsyncTaskLoader<WeatherData>(this) {

                    @Override
                    protected void onStartLoading() {
                        super.onStartLoading();
                        forceLoad();
                    }

                    @Override
                    public WeatherData loadInBackground() {

                        Cursor cursor = getContext().getContentResolver().query(
                                mUri,
                                DETAIL_PROJECTION,
                                null, null, null
                        );

                        if (cursor != null && cursor.moveToFirst()) {
                            WeatherData data = WeatherData.createDetailData(getContext(), cursor);

                            return data;
                        }

                        return null;
                    }
                };
            default:
                throw new RuntimeException("Loader Not Implemented: " + i);
        }
    }

    @Override
    public void onLoadFinished(Loader<WeatherData> loader, WeatherData data) {
        if (data != null) {
            mBinding.setWeatherData(data);
            mBinding.executePendingBindings();

            mWeatherDetails = data.getSummary();
        }
    }

    @Override
    public void onLoaderReset(Loader<WeatherData> loader) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    private Intent createShareDetailsIntent() {
        Intent intent =  ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setText(mWeatherDetails + " " + FORECAST_SHARE_HASHTAG)
                .getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        return intent;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemSelected = item.getItemId();
        switch (itemSelected) {
            case R.id.action_share:
                Intent intent = createShareDetailsIntent();
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
                return true;
            case R.id.action_settings:
                SettingsActivity.launch(this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
