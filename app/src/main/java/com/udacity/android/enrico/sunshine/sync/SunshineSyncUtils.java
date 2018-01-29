package com.udacity.android.enrico.sunshine.sync;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

/**
 * Created by enrico on 1/29/18.
 */

public class SunshineSyncUtils {

    public static void startImmediateSync(@NonNull final Context context) {
        Intent intent = new Intent(context, SunshineSyncIntentService.class);
        context.startService(intent);
    }
}
