package com.mibarim.driver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

/**
 * Created by mohammad hossein on 11/12/2017.
 */

public class BootCompletedIntentReceiver extends BroadcastReceiver {
    SharedPreferences PrefGPS;

    @Override
    public void onReceive(Context context, Intent i) {

//        PrefGPS = context.getSharedPreferences("com.mibarim.driver", MODE_PRIVATE);
//        Calendar cur_cal = Calendar.getInstance();
//        cur_cal.setTimeInMillis(System.currentTimeMillis());
//        cur_cal.add(Calendar.SECOND, 10);
//        Intent intent = new Intent(context, SendCurrentLocationService.class);
//        Intent intent2 = new Intent(context, GetLocationService.class);
//        intent2.putExtra(Constants.Service.SERVICE_PERIOD, PrefGPS.getInt(Constants.Service.SERVICE_PERIOD,0));
//        intent2.putExtra(Constants.Service.TripId, PrefGPS.getInt(Constants.Service.TripId,0));
//        intent2.putExtra(Constants.Service.autTokenLocation, PrefGPS.getString(Constants.Service.autTokenLocation,""));
//        intent2.putExtra(Constants.Service.TripStateLocation, PrefGPS.getInt(Constants.Service.TripStateLocation,0));
//        PendingIntent pi = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
//        PendingIntent pi2 = PendingIntent.getService(context, PrefGPS.getInt(Constants.Service.TripId,0), intent2, PendingIntent.FLAG_CANCEL_CURRENT);
//        AlarmManager alarm_manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        alarm_manager.set(AlarmManager.RTC, cur_cal.getTimeInMillis(), pi);
//        alarm_manager.setRepeating(AlarmManager.RTC, cur_cal.getTimeInMillis(), 30 * 60 * 1000, pi);
//        alarm_manager.set(AlarmManager.RTC, cur_cal.getTimeInMillis(), pi2);
//        alarm_manager.setRepeating(AlarmManager.RTC, cur_cal.getTimeInMillis(), 60 * 1000, pi2);

    }
}