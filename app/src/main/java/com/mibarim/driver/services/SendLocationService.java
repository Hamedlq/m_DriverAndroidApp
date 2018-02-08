package com.mibarim.driver.services;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

import com.google.gson.Gson;
import com.mibarim.driver.BootstrapApplication;
import com.mibarim.driver.core.Constants;
import com.mibarim.driver.core.LocationService;
import com.mibarim.driver.dataBase.CurrentLocationDataBase;
import com.mibarim.driver.models.Address.Location;
import com.mibarim.driver.models.ApiResponse;
import com.mibarim.driver.models.Plus.DriverTripModel;
import com.mibarim.driver.models.Trip.LocationDataBaseModel;
import com.mibarim.driver.util.SafeAsyncTask;

import java.util.List;

import javax.inject.Inject;

import static com.mibarim.driver.core.Constants.Auth.AUTH_TOKEN;

/**
 * Created by Arya on 1/27/2018.
 */

public class SendLocationService extends Service {

    @Inject
    TripService tripService;
    private SafeAsyncTask thread;
    private ApiResponse apiResponse;
    private String authToken;
    private CurrentLocationDataBase db;
    private List<LocationDataBaseModel> model;
    private DriverTripModel tripModel;
    private long tripId;
    private int tripState;
    private SharedPreferences preferences;
    private Location latLng;
    private android.location.Location location;

    @Override
    public void onCreate() {
        super.onCreate();
        BootstrapApplication.component().inject(this);
        tripModel = new DriverTripModel();
        latLng = new Location();
        db = new CurrentLocationDataBase(this);
        db.openDB();
        preferences = getSharedPreferences("com.mibarim.driver", MODE_PRIVATE);
        authToken = preferences.getString(AUTH_TOKEN, "");
        getLocation();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void getLocation() {
        location = LocationService.getLocationManager(this).getLocation();
        thread = new SafeAsyncTask<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                tripId = preferences.getLong(Constants.Service.TripId, -1);
                tripState = preferences.getInt(Constants.Service.TripStateLocation, -1);
                if (location != null) {
                    latLng.lat = String.valueOf(location.getLatitude());
                    latLng.lng = String.valueOf(location.getLongitude());
                    model = db.getAllData();
                    if (tripId != -1 && tripState != 0)
                        model.add(new LocationDataBaseModel(latLng.lat, latLng.lng, tripId, tripState));
                    if (model.size() > 0)
                        apiResponse = tripService.setTripPoints(authToken, model);
                }
                    Thread.sleep(10000);
                return true;
            }

            @Override
            protected void onSuccess(Boolean aBoolean) throws Exception {
                super.onSuccess(aBoolean);
                db.deleteAll();
            }

            @Override
            protected void onException(Exception e) throws RuntimeException {
                super.onException(e);
                db.inserting(latLng.lat, latLng.lng, tripId, tripState);
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }

            @Override
            protected void onFinally() throws RuntimeException {
                super.onFinally();
                getLocation();
            }
        };
        if (apiResponse != null) {
            for (String tripJson : apiResponse.Messages)
                tripModel = new Gson().fromJson(tripJson, DriverTripModel.class);
            if (tripModel.ServicePeriod == 0)
                stopService();
            else
                thread.execute();
        } else
            thread.execute();
    }

    private void stopService() {
        db.closeDB();
        stopSelf();
    }
}
