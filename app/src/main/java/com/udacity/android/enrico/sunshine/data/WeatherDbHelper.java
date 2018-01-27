package com.udacity.android.enrico.sunshine.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.udacity.android.enrico.sunshine.data.WeatherContract.WeatherEntry;

/**
 * Created by enrico on 1/26/18.
 */

public class WeatherDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "weather.db";

    public static final int DATABASE_VERSION = 1;

    public WeatherDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(
                "CREATE TABLE " + WeatherEntry.TABLE_NAME + " (" +
                        WeatherEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        WeatherEntry.COLUMN_DATE       + " INTEGER, " +
                        WeatherEntry.COLUMN_WEATHER_ID + " INTEGER, " +
                        WeatherEntry.COLUMN_MIN_TEMP   + " REAL, " +
                        WeatherEntry.COLUMN_MAX_TEMP   + " REAL, " +
                        WeatherEntry.COLUMN_HUMIDITY   + " REAL, " +
                        WeatherEntry.COLUMN_PRESSURE   + " REAL, " +
                        WeatherEntry.COLUMN_WIND_SPEED + " REAL, " +
                        WeatherEntry.COLUMN_DEGREES    + " REAL" +
                ");"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
//        sqLiteDatabase.execSQL("DROP TABLE " + WeatherEntry.TABLE_NAME);
    }
}
