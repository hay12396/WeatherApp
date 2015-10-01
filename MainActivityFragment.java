package com.example.hayzohar.weatherapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.hayzohar.weatherapp.Notification.NotificationClass;
import com.example.hayzohar.weatherapp.Service.FetchWeatherData;
import com.example.hayzohar.weatherapp.Sql.WeatherDataSourceHandler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    CustomAdapter mCustomAdapter;
    private int mFetch;
    private String mLastSavedLocation;
    private WeatherDataSourceHandler mDbHandler;
    private ListView mListView;

    @Override
    public void onStart() {
        super.onStart();
        mDbHandler = new WeatherDataSourceHandler(getContext());
        UpdateWeatherData();
        SetNotification();
    }

    private void SetNotification() {
        //Setting the wanted tiem that the notification will popup(set to 18:30:00 in this example):
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 17);
        calendar.set(Calendar.MINUTE, 44);
        calendar.set(Calendar.SECOND, 10);

        //Sending information to the function that handle the notification recieving:
        Intent informationToPass = new Intent(getActivity(), NotificationClass.class);
        informationToPass.putExtra("location", mLastSavedLocation);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 0, informationToPass, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager am = (AlarmManager) getActivity().getSystemService(getActivity().ALARM_SERVICE);

        //Setting up the repeating attitude on a daily basis(Interval_Day).
        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mFetch = 1;//Fetch from server
        mLastSavedLocation = getString(R.string.pref_location_key);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_main, container, false);

        mListView = (ListView) v.findViewById(R.id.ListViewForcast);
        List<DayTempInformation> list = new ArrayList<>();
        list.add(null);
        mCustomAdapter = new CustomAdapter(getActivity(), R.layout.list_items_forcast, list);

        mListView.setAdapter(mCustomAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onListItemClick(position);
            }
        });

        return v;
    }


    private void onListItemClick(int position) {
        DayTempInformation day = mCustomAdapter.getItem(position);
        Bundle bundle = new Bundle();
        bundle.putSerializable("day", day);
        if (getResources().getConfiguration().orientation == getResources().getConfiguration().ORIENTATION_LANDSCAPE) {
            DetailActivityFragment frag = new DetailActivityFragment();
            frag.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.group1, frag)
                    .commit();
        } else {
            Intent intent = new Intent(getActivity(), DetailActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    public boolean UpdateWeatherData() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String location = prefs.getString(getString(R.string.pref_location_key)
                , getString(R.string.pref_location_default));

        if (!location.equalsIgnoreCase(mLastSavedLocation)) {
            mLastSavedLocation = location;
            mFetch = 1;
        }

        if (mFetch == 1) {
            new FetchWeatherData(mDbHandler, mCustomAdapter, getActivity(), mFetch).execute(location);
            mFetch = 0;//do not fetch later.
        }

        return true;
    }
}
