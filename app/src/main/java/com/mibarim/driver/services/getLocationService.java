package com.mibarim.driver.services;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.Vibrator;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import android.os.Process;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.mibarim.driver.MobileModel;
import com.mibarim.driver.R;
import com.mibarim.driver.core.Constants;
import com.mibarim.driver.core.LocationService;
import com.mibarim.driver.models.Address.Location;
import com.mibarim.driver.models.ApiResponse;
import com.mibarim.driver.models.NotifModel;
import com.mibarim.driver.models.Plus.DriverTripModel;
import com.mibarim.driver.ui.activities.MainActivity;
import com.mibarim.driver.ui.activities.RidingActivity;
import com.mibarim.driver.util.SafeAsyncTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import retrofit.RestAdapter;

/**
 * Created by mohammad hossein on 30/11/2017.
 */

public class getLocationService extends Service{
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private ApiResponse tripApiResponse;
    TripService tripService;
    String authToken;
    private DriverTripModel driverTripModel;
    private DriverTripModel tripResponse;
    private int TripId;
    private int TripState;
    private Context context;
    private int servicePeriod;
    Handler mHandler;
    Runnable mRunnable;
    private static final ScheduledExecutorService worker =
            Executors.newSingleThreadScheduledExecutor();
    SharedPreferences PrefGPS = null;



    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(final Message msg) {

            PrefGPS = getSharedPreferences("taximeter", MODE_PRIVATE);
            /*mHandler = new Handler();
            mRunnable = new Runnable() {
                @Override
                public void run() {
                    if (PrefGPS.getInt("locationRepeat", 10) == 0)
                        stopSelf(msg.arg1);

                    sendRequest();
                    mHandler.postDelayed(this, PrefGPS.getInt("locationRepeat", 10) * 1000);
                }
            };
            worker.schedule(mRunnable, 0, TimeUnit.SECONDS);
*/
            sendRequest();

            stopSelf(msg.arg1);
        }

        public void sendRequest() {
            int thePeriod=0;
            Log.i("testGPS", "first");
            RestAdapter adapter = new RestAdapter.Builder()
                    .setEndpoint(Constants.Http.URL_BASE)
                    .build();
            tripService = new TripService(adapter);
            Location point = new Location();
            android.location.Location location = LocationService.getLocationManager(context).getLocation();
            if(location != null) {
                point.lat = String.valueOf(location.getLatitude());
                point.lng = String.valueOf(location.getLongitude());

                tripApiResponse = tripService.setTripPoint(PrefGPS.getString("autTokenLocation", ""), point.lat, point.lng, PrefGPS.getLong("TripIdLocation", 10), PrefGPS.getInt("TripStateLocation", 10));

                Log.i("testGPS", "send");

                if (tripApiResponse != null) {
                    for (String tripJson : tripApiResponse.Messages) {
                        tripResponse = new Gson().fromJson(tripJson, DriverTripModel.class);
                        PrefGPS.edit().putInt("locationRepeat", tripResponse.ServicePeriod).apply();
                        thePeriod=tripResponse.ServicePeriod;
                        Log.i("testGPS", tripResponse.ServicePeriod + " ");
                    }
                }

                if (servicePeriod != 0 && thePeriod != 0) {
                    if (servicePeriod != thePeriod) {
                        Calendar cur_cal = Calendar.getInstance();
                        cur_cal.setTimeInMillis(System.currentTimeMillis());
                        cur_cal.add(Calendar.SECOND, servicePeriod);
                        AlarmManager alarm_manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                        Intent cancelServiceIntent = new Intent(context, getLocationService.class);
                        PendingIntent cancelServicePendingIntent = PendingIntent.getBroadcast(
                                context,
                                TripId, // integer constant used to identify the service
                                cancelServiceIntent,
                                0 //no FLAG needed for a service cancel
                        );
                        alarm_manager.cancel(cancelServicePendingIntent);

                        Intent intent = new Intent(context, getLocationService.class);
                        intent.putExtra(Constants.Service.SERVICE_PERIOD, thePeriod);
                        intent.putExtra(Constants.Service.TripId, TripId);
                        PendingIntent pi = PendingIntent.getService(context, TripId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                        alarm_manager.set(AlarmManager.RTC, cur_cal.getTimeInMillis(), pi);
                        alarm_manager.setRepeating(AlarmManager.RTC, cur_cal.getTimeInMillis(), servicePeriod * 1000, pi);
                    }
                }
            }
/*
            new SafeAsyncTask<Boolean>() {

                @Override
                public Boolean call() throws Exception {

                    return true;
                }

                @Override
                protected void onFinally() throws RuntimeException {
                    super.onFinally();
                }

                @Override
                protected void onException(Exception e) throws RuntimeException {
                    super.onException(e);
                }

                @Override
                protected void onSuccess(Boolean aBoolean) throws Exception {
                    super.onSuccess(aBoolean);


                }
            }.execute();*/
        }

    }

    @Override
    public void onCreate() {
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (!isGPSEnable())
            stopSelf();
        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        mServiceHandler.sendMessage(msg);
        context=this;

        if (intent !=null && intent.getExtras()!=null){
            servicePeriod = intent.getIntExtra(Constants.Service.SERVICE_PERIOD,0);
            TripId = intent.getIntExtra(Constants.Service.TripId,0);
        }
        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onDestroy() {
    }

    private boolean isGPSEnable() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return true;
        } else
            return false;
    }

}
