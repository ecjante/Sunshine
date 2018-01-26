package com.udacity.android.enrico.sunshine.utilities;

import android.util.Log;

import timber.log.Timber;

/**
 * Created by enrico on 1/26/18.
 */

public class ReleaseTree extends Timber.Tree {

    private static final int MAX_LOG_LENGTH = 4000;

    @Override
    protected boolean isLoggable(String tag, int priority) {
        if (priority == Log.VERBOSE || priority == Log.DEBUG || priority == Log.INFO) {
            return false;
        }

        return super.isLoggable(tag, priority);
    }

    @Override
    protected void log(int priority, String tag, String message, Throwable t) {
        if (isLoggable(tag, priority)) {
            if (t != null) {
                if (priority == Log.ASSERT) {
                    Log.wtf(tag, message, t);
                } else {
                    Log.println(priority, tag, message);
                }
            }
        }
    }
}
