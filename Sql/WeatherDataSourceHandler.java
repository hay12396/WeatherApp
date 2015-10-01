package com.example.hayzohar.weatherapp.Sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import com.example.hayzohar.weatherapp.DayTempInformation;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hay Zohar on 18/09/2015.
 */
public class WeatherDataSourceHandler implements Serializable{
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;

    private String[] allColumns = {MySQLiteHelper.COLUMN_ID,
            MySQLiteHelper.COLUMN_DAY_TEMP, MySQLiteHelper.COLUMN_DATE, MySQLiteHelper.COLUMN_DESC,
            MySQLiteHelper.COLUMN_NIGHT_TEMP, MySQLiteHelper.COLUMN_LOCATION, MySQLiteHelper.COLUMN_MAIN,
            MySQLiteHelper.COLUMN_MIN_TEMP, MySQLiteHelper.COLUMN_MAX_TEMP, MySQLiteHelper.COLUMN_PRESSURE,
            MySQLiteHelper.COLUMN_SPEED, MySQLiteHelper.COLUMN_HUMIDITY, MySQLiteHelper.COLUMN_MORNING_TEMP,
            MySQLiteHelper.COLUMN_EVENING_TEMP};

    public WeatherDataSourceHandler(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }
    public void createDay(DayTempInformation dayToInsert,String location) throws SQLException {
        //package contains the values to fill to the db table.
        open();
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_DATE, dayToInsert.getmDate());
        values.put(MySQLiteHelper.COLUMN_DAY_TEMP, dayToInsert.getmDayTemp());
        values.put(MySQLiteHelper.COLUMN_NIGHT_TEMP, dayToInsert.getmNightTemp());
        values.put(MySQLiteHelper.COLUMN_EVENING_TEMP, dayToInsert.getmEveningTemp());
        values.put(MySQLiteHelper.COLUMN_MORNING_TEMP, dayToInsert.getmMorningTemp());
        values.put(MySQLiteHelper.COLUMN_LOCATION, location);
        values.put(MySQLiteHelper.COLUMN_DESC, dayToInsert.getmDescription());
        values.put(MySQLiteHelper.COLUMN_MAIN, dayToInsert.getmMain());
        values.put(MySQLiteHelper.COLUMN_MIN_TEMP, dayToInsert.getmMinTemp());
        values.put(MySQLiteHelper.COLUMN_MAX_TEMP, dayToInsert.getmMaxTemp());
        values.put(MySQLiteHelper.COLUMN_HUMIDITY, dayToInsert.getmHumidity());
        values.put(MySQLiteHelper.COLUMN_SPEED, dayToInsert.getmSpeed());
        values.put(MySQLiteHelper.COLUMN_PRESSURE, dayToInsert.getmPressure());
        long id = database.insert(MySQLiteHelper.TABLE_NAME, null, values);
        dayToInsert.setmId(id);
        close();
    }

    public void deleteDay(DayTempInformation dayToDelete) throws SQLException {
        open();
        database.delete(MySQLiteHelper.TABLE_NAME, MySQLiteHelper.COLUMN_ID + " = " + dayToDelete.getmId(), null);
        close();
    }

    public List<DayTempInformation> getAllDays(String location) throws SQLException {
        open();
        List<DayTempInformation> dayList = new ArrayList<>();
        String statment = null;

        if(! location.isEmpty())
            statment= MySQLiteHelper.COLUMN_LOCATION + " = '" + location+"'";

        Cursor cursor = database.query(MySQLiteHelper.TABLE_NAME, allColumns, statment, null, null, null, null);

        cursor.moveToFirst();
        while(!cursor.isAfterLast())
        {
            DayTempInformation dayToAdd = cursorToDay(cursor);
            dayList.add(dayToAdd);
            cursor.moveToNext();
        }
        close();
        return dayList;
    }

    public DayTempInformation getToday(String location) throws SQLException, InterruptedException {
        open();
        String statment = null;

        if(! location.isEmpty())
            statment= MySQLiteHelper.COLUMN_LOCATION + " = '" + location+"'" + " LIMIT 1;";

        Cursor cursor = database.query(MySQLiteHelper.TABLE_NAME, allColumns, statment, null, null, null, null);

        cursor.moveToFirst();
        DayTempInformation today = cursorToDay(cursor);
        close();
        return today;
    }

    public boolean LocationExist(String location)
    {
        try {
            open();
            return DatabaseUtils.longForQuery(database, "select count(*) from " + MySQLiteHelper.TABLE_NAME + " where location=? limit 1", new String[] {location}) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            close();
        }
        return false;
    }

    public void deleteAllDays(List<DayTempInformation> dayList) throws SQLException {
        for(DayTempInformation day : dayList)
        {
            deleteDay(day);
        }
    }

    private DayTempInformation cursorToDay(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndex("_id"));
        String date = cursor.getString(cursor.getColumnIndex("date"));
        int dayT=cursor.getInt(cursor.getColumnIndex("day_temp"));
        int nightT=cursor.getInt(cursor.getColumnIndex("night_temp"));
        String desc = cursor.getString(cursor.getColumnIndex("desc"));
        int minTemp = cursor.getInt(cursor.getColumnIndex("min_temp"));
        int maxTemp = cursor.getInt(cursor.getColumnIndex("max_temp"));
        int morningTemp = cursor.getInt(cursor.getColumnIndex("morning_temp"));
        int eveningTemp = cursor.getInt(cursor.getColumnIndex("evening_temp"));
        int speed = cursor.getInt(cursor.getColumnIndex("speed"));
        int humidity = cursor.getInt(cursor.getColumnIndex("humidity"));
        int pressure = cursor.getInt(cursor.getColumnIndex("pressure"));
        String main = cursor.getString(cursor.getColumnIndex("main"));
        DayTempInformation dayToAdd = new DayTempInformation(id, date, dayT, nightT, desc,
                minTemp, maxTemp, main, humidity, speed, pressure, morningTemp, eveningTemp);
        return dayToAdd;
    }
}
