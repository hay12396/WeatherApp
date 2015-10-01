package com.example.hayzohar.weatherapp.Sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Hay Zohar on 18/09/2015.
 */
public class MySQLiteHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "WeatherApp.db";
    public static final String TABLE_NAME = "WeatherInfo";
    public static final String COLUMN_DAY_TEMP = "day_temp";
    public static final String COLUMN_NIGHT_TEMP = "night_temp";
    public static final String COLUMN_MORNING_TEMP = "morning_temp";
    public static final String COLUMN_EVENING_TEMP = "evening_temp";
    public static final String COLUMN_HUMIDITY = "humidity";
    public static final String COLUMN_SPEED = "speed";
    public static final String COLUMN_PRESSURE = "pressure";
    public static final String COLUMN_DESC = "desc";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_LOCATION = "location";
    public static final String COLUMN_MIN_TEMP = "min_temp";
    public static final String COLUMN_MAX_TEMP = "max_temp";
    public static final String COLUMN_MAIN = "main";

    private String getCreateString()
    {
        return "CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_LOCATION + " text not null,"
                + COLUMN_DATE + " text not null,"
                + COLUMN_DAY_TEMP + " integer,"
                + COLUMN_NIGHT_TEMP + " integer,"
                + COLUMN_MORNING_TEMP + " integer,"
                + COLUMN_EVENING_TEMP + " integer,"
                + COLUMN_DESC + " text not null,"
                + COLUMN_MAIN + " text not null,"
                + COLUMN_MAX_TEMP + " text not null,"
                + COLUMN_MIN_TEMP + " text not null,"
                + COLUMN_HUMIDITY + " integer,"
                + COLUMN_SPEED + " integer,"
                + COLUMN_PRESSURE + " integer" + ")";
    }

    MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(getCreateString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

}
