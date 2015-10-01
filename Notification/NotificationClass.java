package com.example.hayzohar.weatherapp.Notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.example.hayzohar.weatherapp.DayTempInformation;
import com.example.hayzohar.weatherapp.MainActivity;
import com.example.hayzohar.weatherapp.Sql.WeatherDataSourceHandler;

import java.sql.SQLException;

/**
 * Created by Hay Zohar on 30/09/2015.
 */
public class NotificationClass extends BroadcastReceiver {
    private int MID;
    @Override
    public void onReceive(Context context, Intent intent) {
        //Digging out the information that was sent.
        String location = intent.getStringExtra("location");
        WeatherDataSourceHandler dbHandler = new WeatherDataSourceHandler(context);
        DayTempInformation today = null;
        try {
            today = dbHandler.getToday(location);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        //This is the Activity that will be opened once the notification is clicked.
        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        //This determine how the application will handle(in terms of view) the incoming notification,
        //The flag indicates that the app will update its last notification preview with the new data.
        // It can be also set to show it as a new notification(in addition to the old one in the notification area).
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        //This is the alarm sound that will be heared once the notification is recieved.
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        //Here we are setting the information that will be displayed in the notification itself.
        if (today != null) {
            NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(
                    context).setSmallIcon(today.getNotificationIcon(context))
                    .setContentTitle("Today's Weather")
                    .setContentText(today.ShowAsNotification()).setSound(alarmSound)
                    .setAutoCancel(true).setWhen(when)
                    .setContentIntent(pendingIntent)
                    .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
            notificationManager.notify(MID, mNotifyBuilder.build());
            MID++;
        }
    }
}
