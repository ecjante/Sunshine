package com.udacity.android.enrico.sunshine;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.udacity.android.enrico.sunshine.data.SunshinePreferences;
import com.udacity.android.enrico.sunshine.utilities.NetworkUtils;
import com.udacity.android.enrico.sunshine.utilities.OpenWeatherJsonUtils;

import java.net.URL;

public class MainActivity extends AppCompatActivity implements ForecastAdapter.ForecastAdapterOnClickHandler {

    private TextView mErrorMessageTextView;
    private ProgressBar mLoadingIndicator;
    private RecyclerView mRecyclerView;

    private ForecastAdapter mAdapter;

    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);

        mErrorMessageTextView = findViewById(R.id.tv_error_message);
        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);
        mRecyclerView = findViewById(R.id.rv_forecast);

        mAdapter = new ForecastAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
        LinearLayoutManager linearLayout = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayout);
        mRecyclerView.setHasFixedSize(true);

        loadWeatherData();
    }

    public void loadWeatherData() {
        showWeatherData();
        String location = SunshinePreferences.getPreferredWeatherLocation(this);
        new FetchWeatherTask().execute(location);
    }

    private void showWeatherData() {
        mErrorMessageTextView.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onListItemClicked(String data) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(this, data, Toast.LENGTH_LONG);
        mToast.show();
    }

    private class FetchWeatherTask extends AsyncTask<String, Void, String[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected String[] doInBackground(String... strings) {
            if (strings.length == 0)
                return null;

            String location = strings[0];
            URL locationUrl = NetworkUtils.buildUrl(location);

            try {
                String jsonResponse = NetworkUtils.getResponseFromHttpUrl(locationUrl);
                String[] weatherResponse = OpenWeatherJsonUtils.getSimpleWeatherStringsFromJson(MainActivity.this, jsonResponse);

                return weatherResponse;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String[] strings) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (strings != null) {
                showWeatherData();
                mAdapter.setWeatherData(strings);
            } else {
                showErrorMessage();
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
        if (itemSelected == R.id.action_refresh) {
            mAdapter.setWeatherData(null);
            loadWeatherData();
        }
        return super.onOptionsItemSelected(item);
    }
}
