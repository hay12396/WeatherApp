package com.example.hayzohar.weatherapp;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hayzohar.weatherapp.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    private DayTempInformation mDay;
    android.support.v7.widget.ShareActionProvider mShareActionProvider;

    public DetailActivityFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.full_detail, container, false);
        Bundle bundle = this.getArguments();
        if(bundle != null)
        {
            mDay = (DayTempInformation) bundle.getSerializable("day");
        }
        else
            mDay = (DayTempInformation) getActivity().getIntent().getExtras().getSerializable("day");


        if (mDay != null) {
            FillInformationToFields(mDay, v);
        }
        v.setBackgroundColor(Color.parseColor(CustomAdapter.setBackGroundColor(mDay)));

        return v;
    }

    private void FillInformationToFields(final DayTempInformation mDay, View v) {
        TextView dateTextView = (TextView) v.findViewById(R.id.list_item_date_textview);
        TextView dayTextView = (TextView) v.findViewById(R.id.list_item_day_temp_textview);
        TextView morningTextView = (TextView) v.findViewById(R.id.list_morning_temp_textview);
        TextView eveningTextView = (TextView) v.findViewById(R.id.list_item_evening_temp_textview);
        TextView nightTextView = (TextView) v.findViewById(R.id.list_item_night_temp_textview);
        TextView descTextView = (TextView) v.findViewById(R.id.list_item_desc_textview);
        TextView minTempTextView = (TextView) v.findViewById(R.id.list_item_min_temp);
        TextView maxTempTextView = (TextView) v.findViewById(R.id.list_item_max_temp);
        TextView pressureTextView = (TextView) v.findViewById(R.id.list_item_pressure);
        TextView humidityTextView = (TextView) v.findViewById(R.id.list_item_humidity);
        TextView speedTextView = (TextView) v.findViewById(R.id.list_item_speed);
        ImageView iconImageView = (ImageView) v.findViewById(R.id.list_item_icon);
        Button shareButton = (Button) v.findViewById(R.id.shareImageButton);

        iconImageView.setImageDrawable(mDay.getBigIcons(getContext()));
        dateTextView.setText(mDay.getmDate());
        dayTextView.setText(String.valueOf("Day: " + mDay.getmDayTemp()) + "°C");
        nightTextView.setText("Night: " + String.valueOf(mDay.getmNightTemp()) + "°C");
        minTempTextView.setText("Min: " + String.valueOf(mDay.getmMinTemp()) + "°C");
        maxTempTextView.setText("Max: " + String.valueOf(mDay.getmMaxTemp()) + "°C");
        descTextView.setText(mDay.getmDescription());
        morningTextView.setText("Morning: " + String.valueOf(mDay.getmMorningTemp() + "°C"));
        eveningTextView.setText("Evening: " + String.valueOf(mDay.getmEveningTemp() + "°C"));
        pressureTextView.setText("Pressure: " + String.valueOf(mDay.getmPressure() + "mb"));
        humidityTextView.setText("Humidity: " + String.valueOf(mDay.getmHumidity() + "%"));
        speedTextView.setText("Speed: " + String.valueOf(mDay.getmSpeed() + "mph"));
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createShareForecastIntent(mDay);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detailfragment, menu);
    }

    private void createShareForecastIntent(DayTempInformation day) {
        try{
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
            shareIntent.setType("text/plain");//what kind of data we want to share.
            shareIntent.putExtra(Intent.EXTRA_TEXT, day.toString());
            getActivity().startActivity(shareIntent);

        } catch (ActivityNotFoundException e) {
            Toast.makeText(getContext(),"No application can perform this action.",Toast.LENGTH_SHORT).show();
        }
    }
}
