package com.example.hayzohar.weatherapp.Service;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.hayzohar.weatherapp.CustomAdapter;
import com.example.hayzohar.weatherapp.DayTempInformation;
import com.example.hayzohar.weatherapp.Sql.WeatherDataSourceHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class FetchWeatherData extends AsyncTask<String, Void, List<DayTempInformation>> {
    private WeatherDataSourceHandler mDbHandler;
    private String mLastSavedLocation;
    private int mFetch;
    private CustomAdapter mCustomAdapter;
    private Activity mActivity;

    public FetchWeatherData(WeatherDataSourceHandler dbHandler, CustomAdapter customAdapter, Activity activity, int fetch) {
        super();
        mDbHandler = dbHandler;
        mActivity = activity;
        mCustomAdapter = customAdapter;
        mFetch = fetch;
    }

    @Override
    protected List<DayTempInformation> doInBackground(String... params) {
        mLastSavedLocation = params[0];
        boolean locationInDB = false;
        boolean internetAvailable = haveNetworkConnection();
        locationInDB = LocationInDB(mLastSavedLocation);

        if (!locationInDB && internetAvailable) {
            return FetchFromServer(mLastSavedLocation);
        }else { // if we just fetch from the database.
            try {
                return RefreshData();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private List<DayTempInformation> FetchFromServer(String location)
    {
        String[] stringedWeather = null;
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String forecastJsonStr = null;
        try {
            URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q="
                    + location + "&mode=json&units=metric&cnt=7");

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }

            forecastJsonStr = buffer.toString();
        } catch (IOException e) {
            Log.e("Error: ", "Weather data was not recieved. ", e);
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("Error: ", "Error closing stream", e);
                }
            }
        }
        try {
            return getWeatherDaysForecast(forecastJsonStr);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<DayTempInformation> RefreshData() throws SQLException {
        List<DayTempInformation> dayList = mDbHandler.getAllDays(mLastSavedLocation);
        if(dayList.size() == 0)
            return null;

        if (dayList.get(0).getmDay() != Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) {//if the list is NOT up-to date.
            mDbHandler.deleteAllDays(dayList);
            mFetch = 1; // refetch.
            doInBackground(mLastSavedLocation);
            dayList = mDbHandler.getAllDays(mLastSavedLocation);
        }
        return dayList;
    }

    private boolean LocationInDB(String location) {
        return mDbHandler.LocationExist(location);
    }

    @Override
    protected void onPostExecute(List<DayTempInformation> result) {
        super.onPostExecute(result);
        mCustomAdapter.clear();

        if(result == null) {
            Toast.makeText(mActivity, "No internet connection", Toast.LENGTH_LONG).show();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    waitUntilInternetIsAvailable();
                }
            }).start();

            return;
        }

        for(DayTempInformation day : result)
        {
            mCustomAdapter.add(day);
        }
    }

    public List<DayTempInformation> getWeatherDaysForecast(String weatherJsonStr)
            throws JSONException, SQLException {

        JSONObject weather = new JSONObject(weatherJsonStr);
        JSONArray days = weather.getJSONArray("list");
        List<DayTempInformation> temperatureOfWeek = new ArrayList<>();

        Calendar cal = Calendar.getInstance();
        Date d = Calendar.getInstance().getTime();
        int day = cal.get(Calendar.DATE);
        for (int dayNum = 0; dayNum < 7; dayNum++) {
            String dayOfTheWeek = (String) android.text.format.DateFormat.format("EEEE", d);

            String date = dayOfTheWeek + " " +day ;
            JSONObject chosenDay = days.getJSONObject(dayNum);
            JSONObject TemperatureInfo = chosenDay.getJSONObject("temp");
            JSONArray WeatherInfo = chosenDay.getJSONArray("weather");

            int humidity = chosenDay.getInt("humidity");
            int pressure = chosenDay.getInt("pressure");
            int speed = chosenDay.getInt("speed");
            DayTempInformation dayToAdd = new DayTempInformation(TemperatureInfo, WeatherInfo, date, humidity, pressure, speed);
            temperatureOfWeek.add(dayToAdd);
            cal.add(Calendar.DATE,1);
            day++;
            d=cal.getTime();
        }

        UpdateDataBase(temperatureOfWeek);
        return temperatureOfWeek;
    }

    private void UpdateDataBase(List<DayTempInformation> daysToAdd) throws SQLException {
        for (DayTempInformation day : daysToAdd)
            mDbHandler.createDay(day,mLastSavedLocation);
    }

    private boolean haveNetworkConnection() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) mActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    private boolean waitUntilInternetIsAvailable()
    {
        while(!haveNetworkConnection()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        new FetchWeatherData(mDbHandler,mCustomAdapter,mActivity,mFetch).execute(mLastSavedLocation);
        mFetch = 0;//do not fetch later.
        return true;
    }
}