package com.mibarim.driver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mibarim.driver.services.HelloService;
import com.mibarim.driver.ui.activities.MainActivity;

import java.util.Calendar;

/**
 * Created by mohammad hossein on 11/12/2017.
 */

public class BootCompletedIntentReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent i) {


        Calendar cur_cal = Calendar.getInstance();
        cur_cal.setTimeInMillis(System.currentTimeMillis());
        cur_cal.add(Calendar.SECOND, 10);
        Intent intent = new Intent(context, HelloService.class);
        PendingIntent pi = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarm_manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarm_manager.set(AlarmManager.RTC, cur_cal.getTimeInMillis(), pi);
        alarm_manager.setRepeating(AlarmManager.RTC, cur_cal.getTimeInMillis(), 60 * 1000, pi);

    }
}