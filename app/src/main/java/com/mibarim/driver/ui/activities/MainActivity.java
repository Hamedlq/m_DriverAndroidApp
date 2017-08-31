

package com.mibarim.driver.ui.activities;


import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.os.OperationCanceledException;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.google.gson.Gson;
import com.mibarim.driver.BootstrapApplication;
import com.mibarim.driver.BootstrapServiceProvider;
import com.mibarim.driver.R;
import com.mibarim.driver.authenticator.LogoutService;
import com.mibarim.driver.authenticator.TokenRefreshActivity;
import com.mibarim.driver.core.Constants;
import com.mibarim.driver.core.ImageUtils;
import com.mibarim.driver.data.UserData;
import com.mibarim.driver.events.NetworkErrorEvent;
import com.mibarim.driver.events.RestAdapterErrorEvent;
import com.mibarim.driver.events.UnAuthorizedErrorEvent;
import com.mibarim.driver.models.ApiResponse;
import com.mibarim.driver.models.ImageResponse;
import com.mibarim.driver.models.InviteModel;
import com.mibarim.driver.models.Plus.DriverRouteModel;
import com.mibarim.driver.models.Plus.DriverTripModel;
import com.mibarim.driver.models.Plus.PaymentDetailModel;
import com.mibarim.driver.models.Plus.TripTimeModel;
import com.mibarim.driver.models.Route.BriefRouteModel;
import com.mibarim.driver.models.Route.RouteResponse;
import com.mibarim.driver.models.ScoreModel;
import com.mibarim.driver.models.UserInfoModel;
import com.mibarim.driver.models.enums.TripStates;
import com.mibarim.driver.receiver.NotificationReceiver;
import com.mibarim.driver.services.AuthenticateService;
import com.mibarim.driver.services.RouteRequestService;
import com.mibarim.driver.services.RouteResponseService;
import com.mibarim.driver.services.TripService;
import com.mibarim.driver.services.UserInfoService;
import com.mibarim.driver.ui.BootstrapActivity;
import com.mibarim.driver.ui.HandleApiMessagesBySnackbar;
import com.mibarim.driver.ui.fragments.DriverFragments.DriverCardFragment;
import com.mibarim.driver.ui.fragments.DriverFragments.FabFragment;
import com.mibarim.driver.util.SafeAsyncTask;
import com.squareup.otto.Subscribe;

import java.util.Calendar;

import javax.inject.Inject;

import butterknife.ButterKnife;

//import org.osmdroid.tileprovider.constants.OpenStreetMapTileProviderConstants;


/**
 * Initial activity for the application.
 * * <p/>
 * If you need to remove the authentication from the application please see
 */
public class MainActivity extends BootstrapActivity {
    private static final String TAG = "MainActivity";
    @Inject
    protected BootstrapServiceProvider serviceProvider;
    @Inject
    RouteRequestService routeRequestService;
    @Inject
    LogoutService getLogoutService;
    @Inject
    RouteResponseService routeResponseService;
    @Inject
    TripService tripService;
    @Inject
    UserInfoService userInfoService;
    @Inject
    UserData userData;

    private CharSequence title;
    private Toolbar toolbar;
    private ApiResponse deleteRes;
    private ApiResponse tripRes;
    private ApiResponse userTrip;
    private String authToken;
    private String url;
    private ApiResponse response;
    private int appVersion = 0;
    private ApiResponse theSuggestRoute;
    //private RouteResponse selfRoute;
    private BriefRouteModel selectedRoute;
    private PaymentDetailModel paymentDetailModel;
    private Tracker mTracker;
    protected Bitmap result;//concurrency must be considered
    private int REFRESH_TOKEN_REQUEST = 3456;
    private boolean refreshingToken = false;
    String googletoken = "";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private int FINISH_USER_INFO = 5649;
    private int CREDIT_RETURN = 9999;
    private int ROUTESELECTED = 2456;
    private View parentLayout;
    private boolean netErrorMsg = false;
    boolean doubleBackToExitPressedOnce = false;
    private UserInfoModel userInfoModel;
    DriverRouteModel selectedRouteTrip;
    int selectedRouteHour;
    int seatPickerVal;
    int selectedRouteMin;
    private ScoreModel scoreModel;
    TextView user_credit;
    ImageView invite_btn;
    ImageView uploadButton;
    private InviteModel inviteModel;
    NumberPicker seat_picker;

    private static final String DRIVE_FRAGMENT_TAG = "driveFragment";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        super.onCreate(savedInstanceState);
  /*      if (getCacheDir() != null) {
            OpenStreetMapTileProviderConstants.setCachePath(getCacheDir().getAbsolutePath());
        }*/
        //Fabric.with(this, new Crashlytics());
        BootstrapApplication.component().inject(this);

        BootstrapApplication application = (BootstrapApplication) getApplication();
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName("MainActivity");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        mTracker.send(new HitBuilders.EventBuilder().setCategory("Activity").setAction("MainActivity").build());


        setContentView(R.layout.main_activity);
        if (getIntent() != null && getIntent().getExtras() != null) {
            url = getIntent().getExtras().getString(Constants.GlobalConstants.URL);
        }
        parentLayout = findViewById(R.id.main_activity_root);
        // View injection with Butterknife
        ButterKnife.bind(this);

        if (getWindow().getDecorView().getLayoutDirection() == View.LAYOUT_DIRECTION_LTR) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }

        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        user_credit = (TextView) toolbar.findViewById(R.id.user_credit);
        invite_btn = (ImageView) toolbar.findViewById(R.id.invite_btn);
        uploadButton = (ImageView) toolbar.findViewById(R.id.upload_button);
        checkAuth();
        //initScreen();
    }

    private void initScreen() {
        checkVersion();
        getUserInfoFromServer();
        getUserScore();
        getTripState();
        getInviteFromServer();
        final FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.main_container, new DriverCardFragment(), DRIVE_FRAGMENT_TAG)
                .commit();
        fragmentManager.beginTransaction()
                .add(R.id.main_container, new FabFragment(),"FabFragment")
                .commit();



        user_credit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    gotoCreditActivity();
                    return true;
                }
                return false;
            }
        });

        invite_btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    gotoInviteActivity();
                    return true;
                }
                return false;
            }
        });

        uploadButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    goToUploadActivity();
                    return true;
                }
                return false;
            }
        });

        if (url != null) {
            gotoWebView(url);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (authToken != null) {
            getTripState();
        }
    }

    private void checkAuth() {
        new SafeAsyncTask<Boolean>() {

            @Override
            public Boolean call() throws Exception {
                final AuthenticateService svc = serviceProvider.getService(MainActivity.this);
                if (svc != null) {
                    authToken = serviceProvider.getAuthToken(MainActivity.this);
                    return true;
                }
                return false;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                super.onException(e);
                if (e instanceof OperationCanceledException) {
                    // User cancelled the authentication process (back button, etc).
                    // Since auth could not take place, lets finish this activity.
                    finish();
                }
            }

            @Override
            protected void onSuccess(final Boolean hasAuthenticated) throws Exception {
                super.onSuccess(hasAuthenticated);
                //userHasAuthenticated = true;
                initScreen();
                sendRegistrationToServer();
            }
        }.execute();
    }

    private void getUserInfoFromServer() {
        new SafeAsyncTask<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                if (authToken == null) {
                    serviceProvider.invalidateAuthToken();
                    authToken = serviceProvider.getAuthToken(MainActivity.this);
                }
                userInfoModel = userInfoService.getUserInfo(authToken);
                return true;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                super.onException(e);
            }

            @Override
            protected void onSuccess(final Boolean state) throws Exception {
                super.onSuccess(state);
                userData.insertUserInfo(userInfoModel);
                getImageById(userInfoModel.UserImageId, R.mipmap.ic_camera);
                setInfoValues(userInfoModel.IsUserRegistered);
                //setEmail();
            }
        }.execute();
    }

    private void setInfoValues(boolean IsUserRegistered) {
        SharedPreferences prefs = this.getSharedPreferences(
                "com.mibarim.driver", Context.MODE_PRIVATE);
        if (IsUserRegistered) {
            prefs.edit().putInt("UserInfoRegistered", 1).apply();
        } else {
            prefs.edit().putInt("UserInfoRegistered", 0).apply();
            final Intent i = new Intent(this, RegisterActivity.class);
            startActivityForResult(i, FINISH_USER_INFO);
        }
    }

    public String getAuthToken() {
        return authToken;
    }

    @Subscribe
    public void onNetworkErrorEvent(NetworkErrorEvent event) {
        if (!netErrorMsg) {
            netErrorMsg = true;
            //Toaster.showLong(MainActivity0.this, getString(R.string.network_error), R.drawable.toast_warn);
            Snackbar.make(parentLayout, R.string.network_error, Snackbar.LENGTH_LONG).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    netErrorMsg = false;
                }
            }, 5000);
        }
    }


    @Subscribe
    public void onUnAuthorizedErrorEvent(UnAuthorizedErrorEvent event) {
        refreshToken();
    }

    @Subscribe
    public void onRestAdapterErrorEvent(RestAdapterErrorEvent event) {
        Snackbar.make(parentLayout, R.string.network_error, Snackbar.LENGTH_LONG).show();
        //Toaster.showLong(MainActivity.this, getString(R.string.network_error), R.drawable.toast_warn);
    }


    private void refreshToken() {
        if (!refreshingToken) {
            refreshingToken = true;
            final Intent i = new Intent(this, TokenRefreshActivity.class);
            startActivityForResult(i, REFRESH_TOKEN_REQUEST);
        }
    }

/*
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }
*/


    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REFRESH_TOKEN_REQUEST && resultCode == RESULT_OK) {
            authToken = null;
            serviceProvider.invalidateAuthToken();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    refreshingToken = false;
                }
            }, 5000);
        }
        if (requestCode == ROUTESELECTED && resultCode == RESULT_OK) {
            refreshList();
        }
        if (requestCode == CREDIT_RETURN && resultCode == RESULT_OK) {
            getUserScore();
        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        Snackbar.make(parentLayout, R.string.press_again_to_exit, Snackbar.LENGTH_LONG).show();
        this.doubleBackToExitPressedOnce = true;
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 4000);
    }

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
//        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
//            public void onCancel(final DialogInterface dialog) {
//            }
//        });
        return dialog;
    }


    public Bitmap getImageById(String imageId, int defaultDrawableId) {
        Bitmap icon = BitmapFactory.decodeResource(getResources(), defaultDrawableId);
        if (imageId == null || imageId.equals("") || imageId.equals("00000000-0000-0000-0000-000000000000")) {
            return icon;
        }
        ImageResponse imageResponse = userData.imageQuery(imageId);
        if (imageResponse != null) {
            Bitmap b = ImageUtils.loadImageFromStorage(imageResponse.ImageFilePath, imageResponse.ImageId);
            if (b != null) {
                return b;
            } else {
                getImageFromServer(imageId);
            }
        } else {
            getImageFromServer(imageId);
        }
        return icon;
    }

    private void getImageFromServer(final String imageId) {
        new SafeAsyncTask<Boolean>() {
            ImageResponse imageResponse = new ImageResponse();
            Bitmap decodedByte;

            @Override
            public Boolean call() throws Exception {
                if (authToken == null) {
                    serviceProvider.invalidateAuthToken();
                    authToken = serviceProvider.getAuthToken(MainActivity.this);
                }
                imageResponse = userInfoService.GetImageById(authToken, imageId);
                if (imageResponse != null && imageResponse.Base64ImageFile != null) {
                    return true;
                }
                return false;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                super.onException(e);
                if (e instanceof android.os.OperationCanceledException) {
                    // User cancelled the authentication process (back button, etc).
                    // Since auth could not take place, lets finish this activity.
//                    finish();
                }
            }

            @Override
            protected void onSuccess(final Boolean imageLoaded) throws Exception {
                if (imageLoaded) {
                    byte[] decodedString = Base64.decode(imageResponse.Base64ImageFile, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    String path = ImageUtils.saveImageToInternalStorage(getApplicationContext(), decodedByte, imageResponse.ImageId);
                    userData.insertImage(imageResponse, path);
                }
            }
        }.execute();
    }

    public RouteResponse getRoute() {
        RouteResponse selfRoute = new RouteResponse();
        selfRoute.RouteId = 127498;
        return selfRoute;
    }


    private void sendRegistrationToServer() {
        if (checkPlayServices()) {
            final InstanceID instanceID = InstanceID.getInstance(this);
            new SafeAsyncTask<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    googletoken = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                            GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                    return true;
                }

                @Override
                protected void onException(final Exception e) throws RuntimeException {
                    super.onException(e);
                    sendTokenToServer();
                }

                @Override
                protected void onSuccess(final Boolean state) throws Exception {
                    super.onSuccess(state);
                    sendTokenToServer();
                }
            }.execute();
        }
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                finish();
            }
            return false;
        }
        return true;
    }

    private void sendTokenToServer() {
        new SafeAsyncTask<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                if (authToken == null) {
                    serviceProvider.invalidateAuthToken();
                    authToken = serviceProvider.getAuthToken(MainActivity.this);
                }
                userInfoService.SaveGoogleToken(authToken, googletoken);
                return true;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                super.onException(e);
            }

            @Override
            protected void onSuccess(final Boolean state) throws Exception {
                super.onSuccess(state);
            }
        }.execute();
    }

    private void getTripState() {
        new SafeAsyncTask<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                if (authToken == null) {
                    serviceProvider.invalidateAuthToken();
                    authToken = serviceProvider.getAuthToken(MainActivity.this);
                }
                userTrip = tripService.getUserTrip(authToken);
                return true;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                super.onException(e);
            }

            @Override
            protected void onSuccess(final Boolean state) throws Exception {
                super.onSuccess(state);
                Gson gson = new Gson();
                for (String tripTimeModel : userTrip.Messages) {
                    DriverTripModel dm = gson.fromJson(tripTimeModel, DriverTripModel.class);

                    if (dm.TripState == TripStates.InTripTime.toInt() || dm.TripState == TripStates.InRiding.toInt()
                            || dm.TripState == TripStates.InPreTripTime.toInt()) {
                        if (dm.FilledSeats > 0) {
                            gotoRidingActivity(dm);
                        } else {
                            Snackbar.make(parentLayout, R.string.not_reserved, Snackbar.LENGTH_LONG).show();
                        }
                    } else if (dm.TripState == TripStates.InRanking.toInt()) {
                        //gotoRankingActivity
                    }
                }
            }
        }.execute();

    }

    public void gotoRidingActivity(DriverTripModel dm) {
        Intent intent = new Intent(this, RidingActivity.class);
        intent.putExtra(Constants.GlobalConstants.DRIVER_TRIP_MODEL, dm);
        intent.putExtra(Constants.Auth.AUTH_TOKEN, authToken);
        this.startActivity(intent);
    }

    public void gotoRidingActivity(DriverRouteModel dr) {
        DriverTripModel dm = new DriverTripModel();
        dm.TripState = dr.TripState;
        dm.StAddress = dr.SrcAddress;
        dm.DriverRouteId = dr.DriverRouteId;
        dm.TripId = dr.TripId;
        dm.StLink = dr.SrcLink;
        dm.StLat = dr.SrcLat;
        dm.StLng = dr.SrcLng;
        dm.FilledSeats = dr.FilledSeats;
        dm.TripState = dr.TripState;
        Intent intent = new Intent(this, RidingActivity.class);
        intent.putExtra(Constants.GlobalConstants.DRIVER_TRIP_MODEL, dm);
        intent.putExtra(Constants.Auth.AUTH_TOKEN, authToken);
        this.startActivity(intent);
    }

    public int getVersion() {
        int v = 1000;
        try {
            v = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return v;
    }

    private void checkVersion() {
        new SafeAsyncTask<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                ApiResponse ver = userInfoService.AppVersion();
                if (ver.Messages.size() > 0 && ver.Messages.get(0) != null) {
                    String version = ver.Messages.get(0);
                    appVersion = Integer.valueOf(version);
                }
                return true;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                super.onException(e);
            }

            @Override
            protected void onSuccess(final Boolean state) throws Exception {
                super.onSuccess(state);
                if (appVersion > getVersion()) {
                    showUpdateDialog(getString(R.string.update_msg));
                }
            }
        }.execute();
    }

    private void showUpdateDialog(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg).setPositiveButton("باشه", dialogClickListener).setNegativeButton("بستن برنامه", dialogClickListener).show();
    }

    private void refreshList() {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(DRIVE_FRAGMENT_TAG);
        ((DriverCardFragment) fragment).refresh();
    }

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.update_link)));
                    startActivity(browserIntent);
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    finishAffinity();
                    break;
            }
        }
    };

    public void gotoRouteLists() {
        Intent intent = new Intent(this, StationRouteListActivity.class);
        this.startActivityForResult(intent, ROUTESELECTED);
    }

    public void deleteRoute(final long driverRouteId) {
        showProgress();
        new SafeAsyncTask<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                if (authToken == null) {
                    serviceProvider.invalidateAuthToken();
                    authToken = serviceProvider.getAuthToken(MainActivity.this);
                }
                deleteRes = routeRequestService.deleteRoute(authToken, driverRouteId);
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
                new HandleApiMessagesBySnackbar(parentLayout, deleteRes).showMessages();
                refreshList();
            }
        }.execute();
    }

    public void ToggleTrip(DriverRouteModel selectedItem) {
        selectedRouteTrip = selectedItem;
        if (selectedItem.HasTrip) {
            String msg = getString(R.string.disable_confirm);
            showConfirmDisableDialog(msg);
            //Snackbar.make(parentLayout, R.string.NoCancel, Snackbar.LENGTH_LONG).show();
        } else {
            showSetTime();
        }
    }

    private void showConfirmDisableDialog(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg).setPositiveButton("بله", ConfirmDisableDialogClickListener)
                .setNegativeButton("خیر", ConfirmDisableDialogClickListener).show();
    }

    DialogInterface.OnClickListener ConfirmDisableDialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    seatPickerVal = 0;
                    //setTripTime();
                    disableTrip();
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    dialog.dismiss();
                    refreshList();
                    break;
            }
        }
    };

    private void showConfirmDialog(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg).setPositiveButton("تعهد می‌دهم", ConfirmDialogClickListener)
                .setNegativeButton("بیخیال", ConfirmDialogClickListener).show();

    }

    DialogInterface.OnClickListener ConfirmDialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    //showSetTime();
                    if (userInfoModel.UserImageId == null) {
                        Intent j = new Intent(getApplicationContext(), UserImageUploadActivity.class);
                        startActivity(j);
                    }
//                    Intent userImageIntent = new Intent(getApplicationContext(),UserImageUploadActivity.class);
//                    startActivity(userImageIntent);
                    setTripTime();
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    dialog.dismiss();
                    refreshList();
                    break;
            }
        }
    };

    private void showSetTime() {
        /*LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.clock_dialog, null);
        final NumberPicker minute_picker = (NumberPicker) alertLayout.findViewById(R.id.minute_picker);
        final LinearLayout minute_up = (LinearLayout) alertLayout.findViewById(R.id.minute_up);
        final LinearLayout minute_down = (LinearLayout) alertLayout.findViewById(R.id.minute_down);
        final NumberPicker hour_picker = (NumberPicker) alertLayout.findViewById(R.id.hour_picker);
        final LinearLayout hour_up = (LinearLayout) alertLayout.findViewById(R.id.hour_up);
        final LinearLayout hour_down = (LinearLayout) alertLayout.findViewById(R.id.hour_down);
        String[] allHours= new String[]{"5", "6", "7", "8", "9","10","11","12","13","14","15","16","17","18","19","20","21","22"};
        hour_picker.setMinValue(1);
        hour_picker.setMaxValue(18);
        hour_picker.setDisplayedValues(allHours);
        hour_picker.setWrapSelectorWheel(false);
        hour_picker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        minutes = new String[]{"00", "15", "30", "45"};
        minute_picker.setMinValue(1);
        minute_picker.setMaxValue(4);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.driver_time);
        builder.setView(alertLayout);
        builder.setCancelable(false);
        builder.setPositiveButton("تایید زمان", TimeDialogClickListener)
                .setNegativeButton("خیر", TimeDialogClickListener).show();*/
        //Calendar mcurrentTime = Calendar.getInstance();
        int hour = selectedRouteTrip.TimingHour;
        int minute = selectedRouteTrip.TimingMin;
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                if (timePicker.isShown()) {
                    selectedRouteHour = selectedHour;
                    selectedRouteMin = selectedMinute;
                    setEmptySeats();
                }
            }


        }, hour, minute, true);
        /*mTimePicker.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                refreshList();
            }
        });*/

        mTimePicker.setTitle("زمان دقیق حضور در مبدا");
        mTimePicker.setButton(DialogInterface.BUTTON_POSITIVE, "تایید زمان", mTimePicker);
        mTimePicker.setButton(DialogInterface.BUTTON_NEGATIVE, "بیخیال", mTimePicker);
        mTimePicker.show();
    }

    DialogInterface.OnClickListener TimeDialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    setEmptySeats();
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    dialog.dismiss();
                    refreshList();
                    break;
            }
        }
    };


    private void setEmptySeats() {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.seats_dialog, null);
        seat_picker = (NumberPicker) alertLayout.findViewById(R.id.seat_picker);
        LinearLayout seat_up = (LinearLayout) alertLayout.findViewById(R.id.seat_up);
        LinearLayout seat_down = (LinearLayout) alertLayout.findViewById(R.id.seat_down);
        seat_down.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    int val = seat_picker.getValue();
                    seat_picker.setValue(val - 1);
                }
                return true;
            }
        });
        seat_up.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    int val = seat_picker.getValue();
                    seat_picker.setValue(val + 1);
                }
                return true;
            }
        });
        String[] seats = new String[]{"1", "2", "3", "4"};
        seat_picker.setMinValue(1);
        seat_picker.setMaxValue(4);
        seat_picker.setValue(4);
        seat_picker.setDisplayedValues(seats);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.empty_seats);
        builder.setView(alertLayout);
        builder.setCancelable(false);
        builder.setPositiveButton("تایید", FinalDialogClickListener)
                .setNegativeButton("بیخیال", FinalDialogClickListener).show();
    }

    DialogInterface.OnClickListener FinalDialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    seatPickerVal = seat_picker.getValue();
                    String msg = getString(R.string.confirm_Msg);
                    showConfirmDialog(msg);
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    dialog.dismiss();
                    refreshList();
                    break;
            }
        }
    };

    private void setTripTime() {
        seatPickerVal = seat_picker.getValue();
        new SafeAsyncTask<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                if (authToken == null) {
                    serviceProvider.invalidateAuthToken();
                    authToken = serviceProvider.getAuthToken(MainActivity.this);
                }
                tripRes = routeRequestService.setRouteTrip(authToken, selectedRouteTrip.DriverRouteId,
                        seatPickerVal, selectedRouteHour, selectedRouteMin);
                return true;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                super.onException(e);
            }

            @Override
            protected void onSuccess(final Boolean state) throws Exception {
                super.onSuccess(state);
                new HandleApiMessagesBySnackbar(parentLayout, tripRes).showMessages();
                refreshList();
                Gson gson = new Gson();
                for (String tripTimeModel : tripRes.Messages) {
                    TripTimeModel tt = gson.fromJson(tripTimeModel, TripTimeModel.class);
                    if (tt.IsSubmited) {
                        setNotificationAlamManager(tt, (int) selectedRouteTrip.DriverRouteId);
                    }
                }
            }
        }.execute();
    }

    private void setNotificationAlamManager(TripTimeModel tt, int driverRouteId) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.HOUR_OF_DAY, tt.RemainHour);
        calendar.add(Calendar.MINUTE, tt.RemainMin);
        Intent intent = new Intent(MainActivity.this, NotificationReceiver.class);
        intent.putExtra("NotifTitle", getString(R.string.start_trip));
        intent.putExtra("NotifText", getString(R.string.open_app));
        intent.putExtra("DriverRouteId", driverRouteId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, driverRouteId
                , intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(android.app.AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    private void disableNotificationAlamManager(int driverRouteId) {
        Intent intent = new Intent(MainActivity.this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, driverRouteId
                , intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    private void disableTrip() {
        new SafeAsyncTask<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                if (authToken == null) {
                    serviceProvider.invalidateAuthToken();
                    authToken = serviceProvider.getAuthToken(MainActivity.this);
                }
                tripRes = routeRequestService.disableTrip(authToken, selectedRouteTrip.DriverRouteId);
                return true;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                super.onException(e);
            }

            @Override
            protected void onSuccess(final Boolean state) throws Exception {
                super.onSuccess(state);
                new HandleApiMessagesBySnackbar(parentLayout, tripRes).showMessages();
                if ((tripRes.Errors == null || tripRes.Errors.size() == 0) && tripRes.Status.equals("OK")) {
                    disableNotificationAlamManager((int) selectedRouteTrip.DriverRouteId);
                }
                refreshList();
            }
        }.execute();
    }

    private void gotoCreditActivity() {
        Intent intent = new Intent(this, CreditActivity.class);
        intent.putExtra(Constants.GlobalConstants.CREDIT_REMAIN, scoreModel.CreditMoneyString);
        intent.putExtra(Constants.Auth.AUTH_TOKEN, authToken);
        this.startActivityForResult(intent, CREDIT_RETURN);

    }

    private void gotoInviteActivity() {
        Intent intent = new Intent(this, InviteActivity.class);
        intent.putExtra(Constants.Auth.AUTH_TOKEN, authToken);
        this.startActivity(intent);
    }

    private void goToUploadActivity(){
        Intent intent = new Intent(this,UserDocumentsUploadActivity.class);
//        Intent intent = new Intent(this,UserImageUploadActivity.class);
        intent.putExtra(Constants.Auth.AUTH_TOKEN, authToken);
        startActivity(intent);
    }

    public void getUserScore() {
        new SafeAsyncTask<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                if (authToken == null) {
                    serviceProvider.invalidateAuthToken();
                    authToken = serviceProvider.getAuthToken(MainActivity.this);
                }
                scoreModel = userInfoService.getUserScores(authToken);
                return true;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                super.onException(e);
            }

            @Override
            protected void onSuccess(final Boolean state) throws Exception {
                super.onSuccess(state);
                setUserScore();
            }
        }.execute();
    }

    private void setUserScore() {
        user_credit.setText(scoreModel.CreditMoneyString);
    }

    public void gotoWebView(String link) {
        Intent i = new Intent(MainActivity.this, WebViewActivity.class);
        i.putExtra("URL", link);
        startActivity(i);

    }

    public void getInviteFromServer() {
        new SafeAsyncTask<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                if (authToken == null) {
                    serviceProvider.invalidateAuthToken();
                    authToken = serviceProvider.getAuthToken(MainActivity.this);
                }
                inviteModel = userInfoService.getInvite(authToken);
                return true;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                super.onException(e);
            }

            @Override
            protected void onSuccess(final Boolean state) throws Exception {
                super.onSuccess(state);
                userData.insertInvite(inviteModel);
            }
        }.execute();
    }

    public void hidefab(){
//        FabFragment fabFragment =
        final FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag("FabFragment");
        ((FabFragment)fragment).hideTheFab();
    }

    public void showFab(){
        final FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag("FabFragment");
        ((FabFragment)fragment).showTheFab();

    }

}
