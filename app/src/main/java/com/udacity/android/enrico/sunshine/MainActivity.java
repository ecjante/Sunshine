package com.udacity.android.enrico.sunshine;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.udacity.android.enrico.sunshine.data.SunshinePreferences;
import com.udacity.android.enrico.sunshine.data.WeatherContract;
import com.udacity.android.enrico.sunshine.data.WeatherData;
import com.udacity.android.enrico.sunshine.sync.SunshineSyncUtils;
import com.udacity.android.enrico.sunshine.utilities.LocationUtils;
import com.udacity.android.enrico.sunshine.utilities.ReleaseTree;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ForecastAdapter.ForecastAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks<List<WeatherData>> {

    public static final int PERMISSIONS_REQUEST_FINE_LOCATION = 111;

    private static final int LOADER_ID = 22;

    private ProgressBar mLoadingIndicator;
    private RecyclerView mRecyclerView;

    public static boolean sSyncData = false;

    private int mPosition = RecyclerView.NO_POSITION;

    private ForecastAdapter mAdapter;

    private GoogleApiClient mClient;
    private FusedLocationProviderClient mFusedLocationClient;


    public static final String[] MAIN_FORECAST_PROJECTION = {
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
    };

    public static final int INDEX_WEATHER_DATE = 0;
    public static final int INDEX_WEATHER_MAX_TEMP = 1;
    public static final int INDEX_WEATHER_MIN_TEMP = 2;
    public static final int INDEX_WEATHER_CONDITION_ID = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new ReleaseTree());
        }

        setContentView(R.layout.activity_forecast);
        getSupportActionBar().setElevation(0f);

        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);
        mRecyclerView = findViewById(R.id.rv_forecast);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (recyclerView.canScrollVertically(-1)) {
                    getSupportActionBar().setElevation(4f);
                } else {
                    getSupportActionBar().setElevation(0f);
                }
            }
        });

        mAdapter = new ForecastAdapter(this, this);
        mRecyclerView.setAdapter(mAdapter);
        LinearLayoutManager linearLayout = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayout);
        mRecyclerView.setHasFixedSize(true);

        mClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .enableAutoManage(this, this)
                .build();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        showLoading();

        SunshineSyncUtils.initialize(this);

        getSupportLoaderManager().initLoader(LOADER_ID, null, this);
    }

    public void openLocationInMap() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String addressString = pref.getString(
                getString(R.string.pref_location_key),
                getString(R.string.pref_location_default)
        );
        Uri geoLocation = Uri.parse("geo:0,0?q=" + addressString);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Timber.d("Couldn't call " + geoLocation.toString()
                    + ", no receiving apps installed!");
        }
    }

    private void setLocation() {
        if (LocationUtils.hasLocationPermission(this)) {
            if (LocationUtils.locationIsEnabled(this)) {
                updateLocation();
            } else {
                enableLocationDialog();
            }
        } else {
            LocationUtils.requestLocationPermission(this, null);
        }
    }

    private void enableLocationDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setTitle("");
        dialog.setMessage("Location for Sunshine is disabled. Would you like to enable location?");
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Enable", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SunshinePreferences.setLocationEnabled(MainActivity.this, true);
                updateLocation();

                dialogInterface.dismiss();
            }
        });
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        dialog.show();
    }

    private void updateLocation() {
        LocationUtils.getLocationUpdate(mFusedLocationClient, this);
    }

    private void showWeatherData() {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showLoading() {
        mLoadingIndicator.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onListItemClicked(View view, long date) {
        Uri uri = WeatherContract.WeatherEntry.buildWeatherUriWithDate(date);
        Intent intent = new Intent(this, DetailActivity.class);
        intent.setData(uri);
        if (Build.VERSION.SDK_INT >= 21) {
            Bundle b = ActivityOptions.makeSceneTransitionAnimation(
                    this,
                    Pair.create(view, view.getTransitionName())
            ).toBundle();

            startActivity(intent, b);
        } else {
            startActivity(intent);
        }
    }

    @Override
    public Loader<List<WeatherData>> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case LOADER_ID:
                return new AsyncTaskLoader<List<WeatherData>>(this) {

                    List<WeatherData> mWeatherData;

                    @Override
                    protected void onStartLoading() {
                        super.onStartLoading();

                        showLoading();
                        if (sSyncData) {
                            sSyncData = false;
                            forceLoad();
                            return;
                        }

                        if (mWeatherData != null) {
                            showWeatherData();
                            deliverResult(mWeatherData);
                        } else {
                            forceLoad();
                        }
                    }

                    @Nullable
                    @Override
                    public List<WeatherData> loadInBackground() {
                        Uri uri = WeatherContract.WeatherEntry.CONTENT_URI;
                        String selection = WeatherContract.WeatherEntry.getSqlSelectForTodayOnwards();
                        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";

                        Cursor cursor = getContext().getContentResolver().query(
                                uri,
                                MAIN_FORECAST_PROJECTION,
                                selection,
                                null,
                                sortOrder
                        );

                        ArrayList<WeatherData> data = new ArrayList<>();

                        if (cursor != null) {
                            while (cursor.moveToNext()) {
                                WeatherData weather = WeatherData.createListData(getContext(), cursor);
                                data.add(weather);
                            }
                            cursor.close();
                            return data;
                        } else {
                            return null;
                        }
                    }

                    @Override
                    public void deliverResult(@Nullable List<WeatherData> data) {
                        mWeatherData = data;
                        super.deliverResult(mWeatherData);
                    }
                };
            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<List<WeatherData>> loader, List<WeatherData> data) {
        mAdapter.swapData(data);
        if (mPosition == RecyclerView.NO_POSITION)
            mPosition = 0;

        mRecyclerView.smoothScrollToPosition(mPosition);
        if (data.size() != 0)
            showWeatherData();
        else
            getSupportLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    public void onLoaderReset(Loader<List<WeatherData>> loader) {
        mAdapter.swapData(null);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSIONS_REQUEST_FINE_LOCATION:
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SunshinePreferences.setLocationEnabled(this, true);
                } else {
                    SunshinePreferences.setLocationEnabled(this, false);
                }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.forecast, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemSelected = item.getItemId();
        switch (itemSelected) {
            case R.id.action_refresh:
                mAdapter.swapData(null);
                getSupportLoaderManager().restartLoader(LOADER_ID, null, this);
                return true;
            case R.id.action_map:
                openLocationInMap();
                return true;
            case R.id.action_location:
                setLocation();
                return true;
            case R.id.action_settings:
                SettingsActivity.launch(this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Timber.i("API Client Connection Successful!");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Timber.i("API Client Connection Suspended!");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Timber.e("API Client Connection Failed!");
    }
}
