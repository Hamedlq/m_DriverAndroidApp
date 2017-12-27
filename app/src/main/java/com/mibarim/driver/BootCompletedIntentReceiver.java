package com.mibarim.driver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.mibarim.driver.core.Constants;
import com.mibarim.driver.services.HelloService;
import com.mibarim.driver.services.getLocationService;
import com.mibarim.driver.ui.activities.MainActivity;
import com.mibarim.driver.ui.activities.RidingActivity;

import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by mohammad hossein on 11/12/2017.
 */

public class BootCompletedIntentReceiver extends BroadcastReceiver {
    SharedPreferences PrefGPS;

    @Override
    public void onReceive(Context context, Intent i) {

        PrefGPS = context.getSharedPreferences("taximeter", MODE_PRIVATE);
        Calendar cur_cal = Calendar.getInstance();
        cur_cal.setTimeInMillis(System.currentTimeMillis());
        cur_cal.add(Calendar.SECOND, 10);
        Intent intent = new Intent(context, HelloService.class);
        Intent intent2 = new Intent(context, getLocationService.class);
        intent2.putExtra(Constants.Service.SERVICE_PERIOD, PrefGPS.getInt(Constants.Service.SERVICE_PERIOD,0));
        intent2.putExtra(Constants.Service.TripId, PrefGPS.getInt(Constants.Service.TripId,0));
        intent2.putExtra(Constants.Service.autTokenLocation, PrefGPS.getString(Constants.Service.autTokenLocation,""));
        intent2.putExtra(Constants.Service.TripStateLocation, PrefGPS.getInt(Constants.Service.TripStateLocation,0));
        PendingIntent pi = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        PendingIntent pi2 = PendingIntent.getService(context, PrefGPS.getInt(Constants.Service.TripId,0), intent2, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarm_manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarm_manager.set(AlarmManager.RTC, cur_cal.getTimeInMillis(), pi);
        alarm_manager.setRepeating(AlarmManager.RTC, cur_cal.getTimeInMillis(), 60 * 1000, pi);
        alarm_manager.set(AlarmManager.RTC, cur_cal.getTimeInMillis(), pi2);
        alarm_manager.setRepeating(AlarmManager.RTC, cur_cal.getTimeInMillis(), 60 * 1000, pi2);

    }
}