package com.example.hayzohar.weatherapp;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Hay Zohar on 12/09/2015.
 */
public class DayTempInformation implements Serializable{
    private long mId;
    private int mNightTemp;
    private int mDayTemp;
    private  int mMorningTemp;
    private  int mEveningTemp;
    private int mMinTemp;
    private int mMaxTemp;
    private int mHumidity;
    private int mSpeed;
    private int mPressure;
    private String mMain;
    private String mDescription;
    private String mDate;

    public DayTempInformation(JSONObject temperatureInfo, JSONArray weatherInfo, String date, int humidity, int pressure, int speed) throws JSONException {
        mMinTemp = temperatureInfo.getInt("min");
        mMaxTemp = temperatureInfo.getInt("max");
        mNightTemp = temperatureInfo.getInt("night");
        mDayTemp = temperatureInfo.getInt("day");
        mMorningTemp = temperatureInfo.getInt("morn");
        mEveningTemp = temperatureInfo.getInt("eve");

        mHumidity = humidity;
        mPressure = pressure;
        mSpeed = speed;
        JSONObject weatherObject = weatherInfo.getJSONObject(0);

        mMain = weatherObject.getString("main");
        if(mMain.equalsIgnoreCase("Clear"))
            mMain = "Sunny";

        mDescription = weatherObject.getString("description");

        mDate = date;
    }

    public DayTempInformation(long id, String date, int dayTemp, int nightTemp, String desc, int minTemp, int maxTemp, String main,
                              int humidity, int speed, int pressure, int morningTemp, int eveningTemp) {
        mId = id;
        mNightTemp = nightTemp;
        mDayTemp = dayTemp;
        mDescription = desc;
        mDate = date;
        mMinTemp = minTemp;
        mMaxTemp = maxTemp;
        mMain = main;
        mHumidity = humidity;
        mPressure = pressure;
        mSpeed = speed;
        mMorningTemp = morningTemp;
        mEveningTemp = eveningTemp;
    }

    @Override
    public String toString() {
        return mDate  + ", " + mDescription + ","+ "\n" + "Day Temperature:" + mDayTemp + "°C" + "\nNight Temperature:" + mNightTemp + "°C"
                + "\n" + "Morning Temperature:" + mMorningTemp + "°C" + "\nEvening Temperature:" + mEveningTemp + "°C" + "\nSpeed:" + mSpeed + "mph"
                + "\nHumidity:" + mHumidity + "%" + "\nPressure:" + mPressure + "mb";
    }

    public String ShowAsNotification()
    {
        return mMain + ","+ "" + "Day: " + mDayTemp + "°C " + "Night: " + mNightTemp + "°C";
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public Drawable getSmallIcon(Context context)
    {
        if(mDescription.contains("clear") || mDescription.contains("Sunny")) {
            return context.getResources().getDrawable(R.drawable.sunny_32, context.getTheme());
        }
        else if(mDescription.contains("rain"))
        {
            return context.getResources().getDrawable(R.drawable.light_showers_32, context.getTheme());
        }
        else if(mDescription.contains("clouds"))
        {
            return context.getResources().getDrawable(R.drawable.sunny_period_32, context.getTheme());
        }
        else if(mDescription.contains("snow")) {
            return context.getResources().getDrawable(R.drawable.snow_32, context.getTheme());
        }
        else
        {
            return context.getResources().getDrawable(R.drawable.thunderstorms_32, context.getTheme());
        }
    }

    public int getNotificationIcon(Context context)
    {
        if(mDescription.contains("clear") || mDescription.contains("Sunny")) {
            return R.drawable.sunny_32;
        }
        else if(mDescription.contains("rain"))
        {
            return R.drawable.light_showers_32;
        }
        else if(mDescription.contains("clouds"))
        {
            return R.drawable.sunny_period_32;
        }
        else if(mDescription.contains("snow")) {
            return R.drawable.snow_32;
        }
        else
        {
            return R.drawable.thunderstorms_32;
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public Drawable getBigIcons(Context context)
    {
        if(mDescription.contains("clear")) {
            return context.getResources().getDrawable(R.drawable.sunny_128, context.getTheme());
        }
        else if(mDescription.contains("rain"))
        {
            return context.getResources().getDrawable(R.drawable.light_showers_128, context.getTheme());
        }
        else if(mDescription.contains("clouds"))
        {
            return context.getResources().getDrawable(R.drawable.sunny_period_128, context.getTheme());
        }
        else if(mDescription.contains("snow")) {
            return context.getResources().getDrawable(R.drawable.snow_128, context.getTheme());
        }
        else
        {
            return context.getResources().getDrawable(R.drawable.thunderstorms_128, context.getTheme());
        }
    }

    public String getmDate() {
        return mDate;
    }
    public int getmDayTemp() {
        return mDayTemp;
    }
    public int getmNightTemp() {
        return mNightTemp;
    }
    public void setmId(long mId) {
        this.mId = mId;
    }
    public long getmId() {
        return mId;
    }
    public String getmDescription() {
        return mDescription;
    }
    public int getmDay() {
        String day = mDate.replaceAll("[a-zA-Z,]", " ");
        day = day.trim();
        return Integer.valueOf(day);
    }
    public String getmMain() {
        return mMain;
    }
    public int getmMinTemp() {
        return mMinTemp;
    }
    public int getmMaxTemp() {
        return mMaxTemp;
    }
    public int getmEveningTemp() {
        return mEveningTemp;
    }
    public int getmHumidity() {
        return mHumidity;
    }
    public int getmMorningTemp() {
        return mMorningTemp;
    }
    public int getmPressure() {
        return mPressure;
    }
    public int getmSpeed() {
        return mSpeed;
    }
}
