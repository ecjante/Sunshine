package com.udacity.android.enrico.sunshine;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.udacity.android.enrico.sunshine.data.SunshinePreferences;
import com.udacity.android.enrico.sunshine.utilities.NetworkUtils;
import com.udacity.android.enrico.sunshine.utilities.OpenWeatherJsonUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private TextView mWeatherTextView;
    private TextView mErrorMessageTextView;
    private ProgressBar mLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);

        mWeatherTextView = findViewById(R.id.tv_weather_data);
        mErrorMessageTextView = findViewById(R.id.tv_error_message);
        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);

        loadWeatherData();
    }

    public void loadWeatherData() {
        showWeatherData();
        String location = SunshinePreferences.getPreferredWeatherLocation(this);
        new FetchWeatherTask().execute(location);
    }

    private void showWeatherData() {
        mErrorMessageTextView.setVisibility(View.INVISIBLE);
        mWeatherTextView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        mWeatherTextView.setVisibility(View.INVISIBLE);
        mErrorMessageTextView.setVisibility(View.VISIBLE);
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
                for (String str : strings) {
                    mWeatherTextView.append(str + "\n\n\n");
                }
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
            mWeatherTextView.setText("");
            loadWeatherData();
        }
        return super.onOptionsItemSelected(item);
    }
}
