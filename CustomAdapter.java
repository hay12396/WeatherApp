package com.example.hayzohar.weatherapp;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.Serializable;
import java.util.List;


/**
 * Created by Hay Zohar on 20/09/2015.
 */
public class CustomAdapter extends ArrayAdapter<DayTempInformation> implements Serializable{
    private final int VIEW_TYPE_TODAY = 0;
    private final int VIEW_TYPE_FUTURE_DAY = 1;
    private final int VIEW_TYPE_COUNT = 2;

    public CustomAdapter(Context context, int resource) {
        super(context, resource);
    }

    public CustomAdapter(Context context, int resource, List<DayTempInformation> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        LayoutInflater vi;
        vi = LayoutInflater.from(getContext());
        DayTempInformation dayToDisplay = getItem(position);
        int type = getItemViewType(position);

        switch (type) {
            case VIEW_TYPE_TODAY:
                v = vi.inflate(R.layout.item_list_forecast_today, null);
                v.setBackgroundColor(Color.parseColor("#01c4fa"));

                break;

            case VIEW_TYPE_FUTURE_DAY:
                v = vi.inflate(R.layout.list_items_forcast, null);
                v.setBackgroundColor(Color.parseColor("#1d2436"));
                break;
        }

        if (dayToDisplay != null) {
            TextView dateTextView = (TextView) v.findViewById(R.id.list_item_date_textview);
            TextView dayTextView = (TextView) v.findViewById(R.id.list_item_day_textview);
            TextView nightTextView = (TextView) v.findViewById(R.id.list_item_night_textview);
            TextView descTextView = (TextView) v.findViewById(R.id.list_item_main_textview);
            ImageView iconImageView = (ImageView) v.findViewById(R.id.list_item_icon);

            if (dateTextView != null) {
                dateTextView.setText(dayToDisplay.getmDate());
            }
            if (dayTextView != null) {
                dayTextView.setText("Day: " + String.valueOf(dayToDisplay.getmDayTemp()) + "°C");
            }
            if (nightTextView != null) {
                nightTextView.setText("Night: " + String.valueOf(dayToDisplay.getmNightTemp()) + "°C");
            }
            if (descTextView != null) {
                descTextView.setText(dayToDisplay.getmMain());
            }
            if (iconImageView != null) {
                if (type == VIEW_TYPE_TODAY) {
                    iconImageView.setImageDrawable(dayToDisplay.getBigIcons(getContext()));
                    v.setBackgroundColor(Color.parseColor(setBackGroundColor(dayToDisplay)));
                }
                else
                    iconImageView.setImageDrawable(dayToDisplay.getSmallIcon(getContext()));
            }
        }

        return v;
    }

    public static String setBackGroundColor(DayTempInformation dayToDisplay) {
        switch (dayToDisplay.getmMain()) {
            case "Sunny":
                return "#01c4fa";

            case "Rain":
                return "#a0a0a0";

            case "Clouds":
                return "#e0e0e0";

            case "Snow":
                return "#8BE4FF";

            case "Thunderstorm":
                return "#919292";
        }
        return "#ffffff";
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0) ? VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE_DAY;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

}
