package com.mibarim.driver.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.mibarim.driver.R;
import com.mibarim.driver.core.Constants;
import com.mibarim.driver.ui.activities.SplashActivity;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Hamed on 5/7/2016.
 */
public class NotificationReceiver extends BroadcastReceiver {
    static final String TAG = "NotificationReceiver";
    final static String MAINACTIVITY = "com.mibarim.driver.ui.activities.MainActivity";
    private SharedPreferences preferences;

    @Override
    public void onReceive(Context context, Intent intent) {
//        AlarmManager alarmManager = new AlarmManager(DrugReminderApp.getAppContext());
        //Log.d(TAG,"resid");
        preferences = context.getSharedPreferences("com.mibarim.driver", MODE_PRIVATE);
        if (preferences.getInt(Constants.Service.TripStateLocation, 10) > 15)
            ShowNotification(context, intent);
        /*Intent activityIntent= new Intent(context, SplashActivity.class);
        context.startActivity(activityIntent);*/
        //alarmManager.playNotification(intent);
        Log.d(TAG, "onReceive");
    }

    private void ShowNotification(Context context, Intent intent) {
        String title = intent.getStringExtra("NotifTitle");
        String notificationText = intent.getStringExtra("NotifText");
        int driverId = intent.getIntExtra("DriverRouteId", 0);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent alarmActivityIntent = new Intent(context, SplashActivity.class);
        PendingIntent displayIntent = PendingIntent.getActivity(
                context, driverId,
                alarmActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        //set notification
        Notification notification;
        notification = new Notification.Builder(context)
                .setContentTitle(title)
                .setContentText(notificationText)
                .setSmallIcon(R.mipmap.ic_circle_logo)
                .setContentIntent(displayIntent)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_LIGHTS)
                .build();
        //---sets the notification to trigger---
        notificationManager.notify(driverId, notification);
        //set no action alarm
    }
}