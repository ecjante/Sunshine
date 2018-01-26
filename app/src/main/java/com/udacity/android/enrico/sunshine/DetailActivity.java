package com.udacity.android.enrico.sunshine;

import android.content.Intent;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {

    private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";

    private String mWeatherDetails;

    private TextView mDisplayWeatherTextview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mDisplayWeatherTextview = findViewById(R.id.tv_display_weather);

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(Intent.EXTRA_TEXT)) {
                mWeatherDetails = intent.getStringExtra(Intent.EXTRA_TEXT);
                mDisplayWeatherTextview.setText(mWeatherDetails);
            }
        }
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
}
