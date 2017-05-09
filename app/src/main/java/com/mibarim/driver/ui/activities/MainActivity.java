

package com.mibarim.driver.ui.activities;


import android.app.Dialog;
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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.mibarim.driver.BootstrapApplication;
import com.mibarim.driver.BootstrapServiceProvider;
import com.mibarim.driver.R;
import com.mibarim.driver.authenticator.LogoutService;
import com.mibarim.driver.authenticator.TokenRefreshActivity;
import com.mibarim.driver.core.ImageUtils;
import com.mibarim.driver.data.UserData;
import com.mibarim.driver.events.NetworkErrorEvent;
import com.mibarim.driver.events.RestAdapterErrorEvent;
import com.mibarim.driver.events.UnAuthorizedErrorEvent;
import com.mibarim.driver.models.ApiResponse;
import com.mibarim.driver.models.ImageResponse;
import com.mibarim.driver.models.Plus.DriverRouteModel;
import com.mibarim.driver.models.Plus.PassRouteModel;
import com.mibarim.driver.models.Plus.PaymentDetailModel;
import com.mibarim.driver.models.Route.BriefRouteModel;
import com.mibarim.driver.models.Route.RouteResponse;
import com.mibarim.driver.models.UserInfoModel;
import com.mibarim.driver.services.AuthenticateService;
import com.mibarim.driver.services.RouteRequestService;
import com.mibarim.driver.services.RouteResponseService;
import com.mibarim.driver.services.UserInfoService;
import com.mibarim.driver.ui.BootstrapActivity;
import com.mibarim.driver.ui.HandleApiMessages;
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

    @Inject
    protected BootstrapServiceProvider serviceProvider;
    @Inject
    RouteRequestService routeRequestService;
    @Inject
    LogoutService getLogoutService;
    @Inject
    RouteResponseService routeResponseService;
    @Inject
    UserInfoService userInfoService;
    @Inject
    UserData userData;


    private CharSequence title;
    private Toolbar toolbar;
    private ApiResponse deleteRes;
    private ApiResponse tripRes;
    private String authToken;
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
    private int ROUTESELECTED = 2456;
    private View parentLayout;
    private boolean netErrorMsg = false;
    boolean doubleBackToExitPressedOnce = false;
    private UserInfoModel userInfoModel;
    DriverRouteModel selectedRouteTrip;
    int selectedRouteHour;
    int selectedRouteMin;

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
        parentLayout = findViewById(R.id.main_activity_root);
        // View injection with Butterknife
        ButterKnife.bind(this);

        if (getWindow().getDecorView().getLayoutDirection() == View.LAYOUT_DIRECTION_LTR) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        checkAuth();
        //initScreen();
    }

    private void initScreen() {
        checkVersion();
        getUserInfoFromServer();
        final FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.main_container, new DriverCardFragment(), DRIVE_FRAGMENT_TAG)
                .commit();
        fragmentManager.beginTransaction()
                .add(R.id.main_container, new FabFragment())
                .commit();
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


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

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
                String token = serviceProvider.getAuthToken(MainActivity.this);
                imageResponse = userInfoService.GetImageById(token, imageId);
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


    public void BookSeat(final PassRouteModel selectedItem) {
        showProgress();
        new SafeAsyncTask<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                if (authToken == null) {
                    authToken = serviceProvider.getAuthToken(MainActivity.this);
                }
                paymentDetailModel = routeRequestService.bookRequest(authToken, selectedItem.TripId);
                return true;
            }

            @Override
            protected void onException(final Exception e) throws RuntimeException {
                super.onException(e);
                if (e instanceof android.os.OperationCanceledException) {
                    // User cancelled the authentication process (back button, etc).
                    // Since auth could not take place, lets finish this activity.
//                    finish();
                }
                hideProgress();
            }

            @Override
            protected void onSuccess(final Boolean succees) throws Exception {
                hideProgress();
                if (succees) {
                    gotoBankPayPage(paymentDetailModel);
                }
                new HandleApiMessages(MainActivity.this, response).showMessages();
                //finishIt();
            }
        }.execute();
    }

    private void gotoBankPayPage(PaymentDetailModel paymentDetailModel) {
        if (paymentDetailModel.State == 100) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(paymentDetailModel.BankLink));
            startActivity(browserIntent);
        } else {
            Snackbar.make(parentLayout, R.string.payment_error, Snackbar.LENGTH_LONG).show();
        }
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
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://mibarimapp.com/androidapp/download"));
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
        if (selectedItem.HasTrip) {
            Snackbar.make(parentLayout, R.string.NoCancel, Snackbar.LENGTH_LONG).show();
        } else {
            selectedRouteTrip = selectedItem;
            showSetTime();
        }
    }

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
                selectedRouteHour = selectedHour;
                selectedRouteMin = selectedMinute;
                setEmptySeats();
            }


        }, hour, minute, true);
        mTimePicker.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                refreshList();
            }
        });

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
        new SafeAsyncTask<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                if (authToken == null) {
                    serviceProvider.invalidateAuthToken();
                    authToken = serviceProvider.getAuthToken(MainActivity.this);
                }
                tripRes = routeRequestService.setRouteTrip(authToken, selectedRouteTrip.DriverRouteId,
                        seat_picker.getValue(), selectedRouteHour, selectedRouteMin);
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
            }
        }.execute();
    }
}