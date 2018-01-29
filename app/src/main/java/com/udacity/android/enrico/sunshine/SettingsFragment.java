package com.udacity.android.enrico.sunshine;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;

import com.udacity.android.enrico.sunshine.data.SunshinePreferences;
import com.udacity.android.enrico.sunshine.data.WeatherContract;
import com.udacity.android.enrico.sunshine.sync.SunshineSyncUtils;

/**
 * Created by enrico on 1/26/18.
 */

public class SettingsFragment extends PreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pref_general);

        PreferenceScreen prefScreen = getPreferenceScreen();
        SharedPreferences pref = prefScreen.getSharedPreferences();
        int count = prefScreen.getPreferenceCount();

        for (int i = 0; i < count; i++) {
            Preference p = prefScreen.getPreference(i);
            if (!(p instanceof CheckBoxPreference)) {
                String value = pref.getString(p.getKey(), "");
                setPreferenceSummary(p, value);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Activity activity = getActivity();

        if (key.equals(getString(R.string.pref_location_key))) {
            SunshinePreferences.resetLocationCoordinates(activity);
            SunshineSyncUtils.startImmediateSync(activity);
        } else if (key.equals(getString(R.string.pref_units_key))) {
            activity.getContentResolver().notifyChange(WeatherContract.WeatherEntry.CONTENT_URI, null);
        }
        Preference p = findPreference(key);
        if (p != null) {
            if (!(p instanceof CheckBoxPreference)) {
                String value = sharedPreferences.getString(p.getKey(), "");
                setPreferenceSummary(p, value);
            }
        }
    }

    private void setPreferenceSummary(Preference p, Object value) {
        String stringValue = value.toString();

        if (p instanceof ListPreference) {
            ListPreference list = (ListPreference) p;
            int prefIndex = list.findIndexOfValue(stringValue);
            if (prefIndex >= 0) {
                list.setSummary(list.getEntries()[prefIndex]);
            }
        } else {
            p.setSummary(stringValue);
        }
    }
}
