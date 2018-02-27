package com.udacity.android.enrico.sunshine.utilities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.udacity.android.enrico.sunshine.MainActivity;
import com.udacity.android.enrico.sunshine.R;
import com.udacity.android.enrico.sunshine.data.SunshinePreferences;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;

import static com.udacity.android.enrico.sunshine.MainActivity.PERMISSIONS_REQUEST_FINE_LOCATION;

/**
 * Created by enrico on 1/31/18.
 */

public class LocationUtils {

    public static boolean locationIsEnabled(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(context.getString(R.string.pref_enable_location_key), false);
    }

    public static boolean hasLocationPermission(Context context) {
        return ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestLocationPermission(Activity activity, @Nullable Runnable action) {
        boolean requestPermission = false;
        int accessFineLocationPermission = ContextCompat.checkSelfPermission(
                activity.getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
        );

        if (accessFineLocationPermission == PackageManager.PERMISSION_DENIED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)) {
                requestPermission = true;
            } else {
                showGoToSettingsDialog(activity, action);
            }
        } else if (accessFineLocationPermission != PackageManager.PERMISSION_GRANTED) {
            requestPermission = true;
        }

        if (requestPermission) {
            ActivityCompat.requestPermissions(
                    activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_FINE_LOCATION
            );
        }
    }

    private static void showGoToSettingsDialog(final Activity activity, @Nullable final Runnable action) {
        AlertDialog dialog = new AlertDialog.Builder(activity).create();
        dialog.setTitle("");
        dialog.setMessage("Location permission for Sunshine is disabled. Enable Location permission for Sunshine in App Info > Permissions");
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Go to App Info", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                intent.setData(uri);
                activity.startActivity(intent);
                dialogInterface.dismiss();
            }
        });
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if (action != null) {
                    action.run();
                }
            }
        });
        dialog.show();
    }

    public static void getLocationUpdate(FusedLocationProviderClient mFusedLocationClient, final Activity activity) {
        if (ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(activity, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // Logic to handle location object
                                double lat = location.getLatitude();
                                double lon = location.getLongitude();
                                String latStr = String.format(Locale.getDefault(), "%.2f", lat);
                                String lonStr = String.format(Locale.getDefault(), "%.2f", lon);
                                lat = Double.valueOf(latStr);
                                lon = Double.valueOf(lonStr);
                                SunshinePreferences.setLocationDetails(
                                        activity,
                                        lat,
                                        lon
                                );

                                Geocoder gcd = new Geocoder(activity, Locale.getDefault());
                                try {
                                    List<Address> addresses = gcd.getFromLocation(lat, lon, 1);
                                    if (addresses.size() > 0) {
                                        Address address = addresses.get(0);
                                        String city = address.getLocality();
                                        String state = address.getAdminArea();
                                        String zipCode = address.getPostalCode();
                                        String countryCode = address.getCountryCode();

                                        String readableLocation = city + ", " +
                                                state + " " +
                                                zipCode + ", " +
                                                countryCode;

                                        SunshinePreferences.setLocation(activity, readableLocation);
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
        }
    }
}
