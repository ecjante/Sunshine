package com.udacity.android.enrico.sunshine.data;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

/**
 * Created by enrico on 1/29/18.
 */

public class BindingAdapters {

    @BindingAdapter("android:setWeatherIcon")
    public static void setWeatherIcon(ImageView view, int weatherIcon) {
        view.setImageResource(weatherIcon);
    }
}
