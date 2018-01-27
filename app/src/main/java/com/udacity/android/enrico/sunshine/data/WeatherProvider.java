package com.udacity.android.enrico.sunshine.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.udacity.android.enrico.sunshine.data.WeatherContract.WeatherEntry;
import com.udacity.android.enrico.sunshine.utilities.SunshineDateUtils;

/**
 * Created by enrico on 1/26/18.
 */

public class WeatherProvider extends ContentProvider {

    private static final int CODE_WEATHER = 100;
    private static final int CODE_WEATHER_WITH_DATE = 101;

    WeatherDbHelper mOpenHelper;

    private static UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        matcher.addURI(
                WeatherContract.CONTENT_AUTHORITY,
                WeatherContract.PATH_WEATHER,
                CODE_WEATHER
        );
        matcher.addURI(
                WeatherContract.CONTENT_AUTHORITY,
                WeatherContract.PATH_WEATHER + "/#",
                CODE_WEATHER_WITH_DATE
        );

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new WeatherDbHelper(getContext());

        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection,
                        @Nullable String selection, @Nullable String[] selectionArgs,
                        @Nullable String sortOrder) {
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();

        Cursor retCursor = null;
        switch (sUriMatcher.match(uri)) {
            case CODE_WEATHER:
                retCursor = db.query(
                        WeatherEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null, null,
                        sortOrder
                );
                break;
            case CODE_WEATHER_WITH_DATE:
                String utcString = uri.getLastPathSegment();
                String[] selectionArguments = new String[] { utcString };

                retCursor = db.query(
                        WeatherEntry.TABLE_NAME,
                        projection,
                        WeatherEntry.COLUMN_DATE + " = ?",
                        selectionArguments,
                        null, null,
                        sortOrder
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return retCursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        return null;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        switch (sUriMatcher.match(uri)) {
            case CODE_WEATHER:
                SQLiteDatabase db = mOpenHelper.getWritableDatabase();
                db.beginTransaction();
                int insertCount = 0;
                try {
                    for (ContentValues value : values) {
                        // Check to make sure date is sent in the correct format
                        long weatherDate = value.getAsLong(WeatherEntry.COLUMN_DATE);
                        if (!SunshineDateUtils.isDateNormalized(weatherDate))
                            throw new IllegalArgumentException("Date must be normalized to insert");

                        long _id = db.insert(WeatherEntry.TABLE_NAME, null, value);
                        if (_id != -1)
                            insertCount++;
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                if (insertCount > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return insertCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        int numRowsDeleted;

        /*
         * If we pass null as the selection to SQLiteDatabase#delete, our entire table will be
         * deleted. However, if we do pass null and delete all of the rows in the table, we won't
         * know how many rows were deleted. According to the documentation for SQLiteDatabase,
         * passing "1" for the selection will delete all rows and return the number of rows
         * deleted, which is what the caller of this method expects.
         */
        if (null == selection) selection = "1";

        switch (sUriMatcher.match(uri)) {
            case CODE_WEATHER:
                numRowsDeleted = db.delete(
                        WeatherContract.WeatherEntry.TABLE_NAME,
                        selection,
                        selectionArgs
                );

                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (numRowsDeleted > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numRowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues,
                      @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }
}
