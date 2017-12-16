package com.mibarim.driver.ui.activities;


import android.accounts.OperationCanceledException;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.graphics.Color;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.ArrayRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.gson.Gson;
import com.mibarim.driver.BootstrapApplication;
import com.mibarim.driver.BootstrapServiceProvider;
import com.mibarim.driver.R;
import com.mibarim.driver.authenticator.TokenRefreshActivity;
import com.mibarim.driver.core.Constants;
import com.mibarim.driver.core.LocationService;
import com.mibarim.driver.events.UnAuthorizedErrorEvent;
import com.mibarim.driver.locationServices.GoogleLocationService;
import com.mibarim.driver.models.Address.Location;
import com.mibarim.driver.models.ApiResponse;
import com.mibarim.driver.models.ImageResponse;
import com.mibarim.driver.models.Plus.DriverTripModel;
import com.mibarim.driver.models.Trip.TripResponse;
import com.mibarim.driver.models.enums.TripStates;
import com.mibarim.driver.services.AuthenticateService;
import com.mibarim.driver.services.TripService;
import com.mibarim.driver.services.UserInfoService;
import com.mibarim.driver.ui.BootstrapActivity;
import com.mibarim.driver.ui.HandleApiMessages;
import com.mibarim.driver.ui.fragments.MapFragment;
import com.mibarim.driver.ui.fragments.tripFragments.MainTripFragment;
import com.mibarim.driver.util.SafeAsyncTask;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;

import static com.mibarim.driver.core.Constants.Auth.AUTH_TOKEN;
import static com.mibarim.driver.core.Constants.GlobalConstants.TRIP_ID_INTENT_TAG;

//import org.osmdroid.tileprovider.constants.OpenStreetMapTileProviderConstants;


/**
 * Initial activity for the application.
 * * <p/>
 * If you need to remove the authentication from the application please see
 */
public class RidingActivity extends BootstrapActivity {
    static final String TAG = "RidingActivity";

    @Inject
    BootstrapServiceProvider serviceProvider;
    @Inject
    TripService tripService;
    @Inject
    UserInfoService userInfoService;

    @Bind(R.id.station_add)
    protected TextView station_add;
    @Bind(R.id.station_dis)
    protected TextView station_dis;
    @Bind(R.id.wait_btn)
    protected AppCompatButton wait_btn;
    @Bind(R.id.map_container)
    protected FrameLayout map_container;
    @Bind(R.id.map_container_web)
    protected WebView map_container_web;
    @Bind(R.id.contact_passengers_button)
    protected AppCompatButton contactPassengersButton;


    Intent googleServiceIntent;
    Intent serviceIntent;
    GoogleLocationService mService;
    boolean mBound = false;
    private Tracker mTracker;
    private ImageView support;


    private String authToken;
    private int RELOAD_REQUEST = 1234;
    private Toolbar toolbar;
    private DriverTripModel driverTripModel;
    private int delay = 10000;
    private ApiResponse tripApiResponse;
    private ApiResponse endTripApiResponse;
    private DriverTripModel tripResponse;
    private int endTripResult;
    private int LocationServiceInUse = 1;// GOOGLE=1 LOCAL_SERVICE=2
    private ImageResponse imageResponse;
    private AlertDialog gpsAlert;
    private int stationDistance = 500;


    private Handler mHandler;
    private Runnable mRunnable;
    private Handler endHandler;
    private Runnable endRunnable;
    private static final ScheduledExecutorService worker =
            Executors.newSingleThreadScheduledExecutor();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        super.onCreate(savedInstanceState);
        BootstrapApplication.component().inject(this);

        if (getIntent() != null && getIntent().getExtras() != null) {
            authToken = getIntent().getExtras().getString(AUTH_TOKEN);
            driverTripModel = (DriverTripModel) getIntent().getExtras().getSerializable(Constants.GlobalConstants.DRIVER_TRIP_MODEL);
        }

        BootstrapApplication application = (BootstrapApplication) getApplication();
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName("RidingActivity");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        mTracker.send(new HitBuilders.EventBuilder().setCategory("Activity").setAction("RidingActivity").build());


        setContentView(R.layout.riding_activity);

        // View injection with Butterknife
        ButterKnife.bind(this);

        if (getWindow().getDecorView().getLayoutDirection() == View.LAYOUT_DIRECTION_LTR) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }


        toolbar = (Toolbar) findViewById(R.id.ride_toolbar);
        setSupportActionBar(toolbar);

        support = (ImageView) toolbar.findViewById(R.id.support);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            if (Build.VERSION.SDK_INT >= 17) {
                actionBar.setHomeAsUpIndicator(R.mipmap.ic_menu_forward);
            }
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        serviceIntent = new Intent(this, LocationService.class);
        googleServiceIntent = new Intent(this, GoogleLocationService.class);
        map_container_web.setVisibility(View.GONE);
        map_container.setVisibility(View.GONE);
        wait_btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (driverTripModel.TripState == TripStates.InPreTripTime.toInt()) {
                        if (stationDistance > 200) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(RidingActivity.this);
                            String msg = "فاصله شما از ایستگاه بسیار زیاد است. لطفا به ایستگاه نزدیک شوید";
                            builder.setMessage(msg).setPositiveButton("باشه", dialogClickListener);
                            gpsAlert = builder.create();
                            gpsAlert.show();
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(RidingActivity.this);
                            String msg = "زمان سفر فرانرسیده است. آیا "+driverTripModel.FilledSeats+" مسافر سوار خودرو شده‌اند؟";
                            builder.setMessage(msg).setPositiveButton("بله", tripdialogClickListener).setNegativeButton("خیر", tripdialogClickListener);
                            gpsAlert = builder.create();
                            gpsAlert.show();
                        }
                        sendTripPoint(driverTripModel.TripState);
                    } else if (driverTripModel.TripState == TripStates.InTripTime.toInt()) {
                        if (stationDistance > 200) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(RidingActivity.this);
                            String msg = "فاصله شما از ایستگاه بسیار زیاد است. لطفا به ایستگاه نزدیک شوید";
                            builder.setMessage(msg).setPositiveButton("باشه", dialogClickListener);
                            gpsAlert = builder.create();
                            gpsAlert.show();
                        } else {
                            sendTripPoint(TripStates.InRiding.toInt());
                            //showTimer
                        }
                        sendTripPoint(driverTripModel.TripState);
                    } else if (driverTripModel.TripState == TripStates.InRiding.toInt()) {
                        sendTripPoint(TripStates.InDriving.toInt());
                    } else {
                        finishIt();
                    }
                    return true;
                }
                return false;
            }
        });

        contactPassengersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RidingActivity.this,ContactPassengersActivity.class);
                intent.putExtra(AUTH_TOKEN,authToken);
                intent.putExtra(TRIP_ID_INTENT_TAG, driverTripModel.TripId);
                startActivity(intent);

            }
        });

        support.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    call("02128426784");
                    return true;
                }
                return false;
            }
        });
        initScreen();
    }

    private void initScreen() {
        station_add.setText(driverTripModel.StAddress);

        if (isPlayServicesConfigured()) {
            map_container.setVisibility(View.VISIBLE);
            final FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(R.id.map_container, new MapFragment())
                    .commitAllowingStateLoss();
        } else {
            map_container_web.setVisibility(View.VISIBLE);
            map_container_web.loadUrl(driverTripModel.StLink);
            map_container_web.getSettings().setDomStorageEnabled(true);
            map_container_web.getSettings().setJavaScriptEnabled(true);
        }
        periodicReLoading();
        locationReloading();
        finishRiding();
    }

    @Override
    public void onDestroy() {
        mHandler.removeCallbacks(mRunnable);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        finishIt();
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finishIt();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    private void sendTripPoint(final int tripState) {
        new SafeAsyncTask<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                tripApiResponse = tripService.setTripPoint(authToken, getLocation().lat, getLocation().lng, driverTripModel.TripId, tripState);
                if (tripApiResponse != null) {
                    for (String tripJson : tripApiResponse.Messages) {
                        tripResponse = new Gson().fromJson(tripJson, DriverTripModel.class);
                    }
                }
                return true;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                super.onException(e);
                //hideProgress();
            }

            @Override
            protected void onSuccess(final Boolean state) throws Exception {
                super.onSuccess(state);
                //
                if (driverTripModel.TripState == TripStates.InPreTripTime.toInt() &&
                        tripResponse.TripState == TripStates.InTripTime.toInt()) {
                    driverTripModel.TripState = tripResponse.TripState;
                } else if (driverTripModel.TripState == TripStates.InTripTime.toInt() &&
                        tripResponse.TripState == TripStates.InRiding.toInt()) {
                    wait_btn.setEnabled(false);
                    wait_btn.setText("شروع سفر");
                    driverTripModel.TripState = tripResponse.TripState;
                    showTimer();
                }
                if (driverTripModel.TripState == TripStates.InRiding.toInt() &&
                        tripResponse.TripState == TripStates.InDriving.toInt()) {
                    finishIt();
                }
                //showTripInfo(tripResponse);
            }
        }.execute();
    }

    private void showTimer() {
        new CountDownTimer(300000, 60000) {

            public void onTick(long millisUntilFinished) {
                wait_btn.setText(" لطفا " + millisUntilFinished / 60000 + " دقیقه منتظر " + driverTripModel.FilledSeats + " مسافر بمانید ");
            }

            public void onFinish() {
                wait_btn.setEnabled(true);
                wait_btn.setText("شروع سفر");
            }
        }.start();

    }

    public void call(String tel) {
        if (tel != null) {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + tel));
            startActivity(intent);
        }
    }

    private void periodicReLoading() {
        mHandler = new Handler();
        mRunnable = new Runnable() {
            @Override
            public void run() {
                reloadThread();
                mHandler.postDelayed(this, 20000);
            }
        };
        worker.schedule(mRunnable, 10, TimeUnit.SECONDS);
    }

    private void finishRiding() {
        endRunnable = new Runnable() {
            @Override
            public void run() {
                finishIt();
            }
        };
        worker.schedule(endRunnable, 10, TimeUnit.MINUTES);
    }


    public void reloadThread() {
        new SafeAsyncTask<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return true;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                super.onException(e);
            }

            @Override
            protected void onSuccess(final Boolean isMsgSubmitted) throws Exception {
                super.onSuccess(isMsgSubmitted);
                locationReloading();
            }
        }.execute();

    }

/*
    private void getRidingInfo() {
        showProgress();
        new SafeAsyncTask<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                tripApiResponse = tripService.getTripInfo(authToken,driverRouteId);
                if (tripApiResponse != null) {
                    for (String tripJson : tripApiResponse.Messages) {
                        tripResponse = new Gson().fromJson(tripJson, TripResponse.class);
                    }
                }
                return true;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                super.onException(e);
                hideProgress();
            }

            @Override
            protected void onSuccess(final Boolean state) throws Exception {
                super.onSuccess(state);
                hideProgress();
                showTripInfo(tripResponse);
            }
        }.execute();

    }
*/

    private void showTripInfo() {
        Location loc = getLocation();
        if (loc != null) {
            float[] results = {0.1f};
            android.location.Location.distanceBetween(
                    Double.parseDouble(driverTripModel.StLat),
                    Double.parseDouble(driverTripModel.StLng),
                    Double.parseDouble(loc.lat),
                    Double.parseDouble(loc.lng),
                    results);
            stationDistance = (int) results[0];
            if (stationDistance > 300) {
                station_dis.setTextColor(Color.RED);
            } else if (stationDistance > 200) {
                station_dis.setTextColor(Color.YELLOW);
            } else if (stationDistance < 200) {
                station_dis.setTextColor(Color.GREEN);
            }
            station_dis.setText(String.valueOf(stationDistance));
            showOnMap(loc);
            sendTripPoint(driverTripModel.TripState);
        }
    }

    private void showOnMap(Location loc) {
        if (isPlayServicesConfigured()) {
            final FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment fragment = fragmentManager.findFragmentById(R.id.map_container);
            if (fragment != null) {
                ((MapFragment) fragment).setStation(driverTripModel.StLat, driverTripModel.StLng, loc.lat, loc.lng);
            }
        }
    }

    private void locationReloading() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String msg = "لطفا مکان یاب تلفن همراه را روشن کنید";
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            android.location.Location location = LocationService.getLocationManager(this).getLocation();
            if (location != null) {
                if (gpsAlert != null && gpsAlert.isShowing()) {
                    gpsAlert.dismiss();
                }
                showTripInfo();
                checkNetwork();
            }
            /*else {
                if (gpsAlert == null || !gpsAlert.isShowing()) {
                    builder.setMessage(msg).setPositiveButton("باشه", gpsClickListener);
                    gpsAlert = builder.create();
                    gpsAlert.show();
                }
            }*/

        } else {
            if (gpsAlert == null || !gpsAlert.isShowing()) {
                builder.setMessage(msg).setPositiveButton("باشه", gpsClickListener);
                gpsAlert = builder.create();
                gpsAlert.show();
            }
        }

    }

    private void checkNetwork() {
        if (!isNetworkAvailable()) {
            if (gpsAlert == null || !gpsAlert.isShowing()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                String msg = "لطفا ارتباط اینترنت را فعال کنید";
                builder.setMessage(msg).setPositiveButton("باشه", dataClickListener);
                gpsAlert = builder.create();
                gpsAlert.show();
            }
        }
    }

    DialogInterface.OnClickListener gpsClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    turnOnGps();
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    //finishAffinity();
                    break;
            }
        }
    };

    DialogInterface.OnClickListener dataClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    break;
            }
        }
    };

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    //finishAffinity();
                    break;
            }
        }
    };

    DialogInterface.OnClickListener tripdialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    sendTripPoint(TripStates.DriverRiding.toInt());
                    finishIt();
                    break;
                case DialogInterface.BUTTON_NEGATIVE:

                    //finishAffinity();
                    break;
            }
        }
    };


    /**
     * Hide progress dialog
     */
    @SuppressWarnings("deprecation")
    protected void hideProgress() {
        dismissDialog(0);
    }

    /**
     * Show progress dialog
     */
    @SuppressWarnings("deprecation")
    protected void showProgress() {
        showDialog(0);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(getText(R.string.please_wait));
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        return dialog;
    }

    public void turnOnGps() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void startLocationService() {
        boolean IsGoogleServiceSupported = false;
        if (isPlayServicesConfigured()) {
            //bind to google location
            try {
                startService(googleServiceIntent);
                bindService(googleServiceIntent, mConnection, Context.BIND_AUTO_CREATE);
                IsGoogleServiceSupported = true;
                LocationServiceInUse = 1;
            } catch (Exception e) {
                IsGoogleServiceSupported = false;
            }
        } else {
            IsGoogleServiceSupported = false;
        }
        if (!IsGoogleServiceSupported) {
            //bind to manual location provider
            startService(serviceIntent);
            bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE);
            LocationServiceInUse = 2;
        }
    }

    public void stopService() {
        unbindService(mConnection);
        if (LocationServiceInUse == 1) {
            stopService(googleServiceIntent);
        } else if (LocationServiceInUse == 2) {
            stopService(serviceIntent);
        }
    }

    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            GoogleLocationService.LocalBinder binder = (GoogleLocationService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    private boolean isPlayServicesConfigured() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this.getApplicationContext());
        if (status == ConnectionResult.SUCCESS)
            return true;
        else {
            Log.d(TAG, "Error connecting with Google Play services. Code: " + String.valueOf(status));
            return false;
        }
    }

    public Location getLocation() {
        Location point = new Location();
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            android.location.Location location = LocationService.getLocationManager(this).getLocation();
            if (location != null) {
                point.lat = String.valueOf(location.getLatitude());
                point.lng = String.valueOf(location.getLongitude());
            }
        }
        return point;
    }

    private void finishIt() {
        mHandler.removeCallbacks(mRunnable);
        LocationService.destroy(RidingActivity.this);
        Intent i= getIntent();
        setResult(RESULT_OK,i);
        finish();
    }
}
