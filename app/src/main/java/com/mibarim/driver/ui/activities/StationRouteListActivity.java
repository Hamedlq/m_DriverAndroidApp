

package com.mibarim.driver.ui.activities;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.os.OperationCanceledException;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

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
import com.mibarim.driver.models.Plus.PassRouteModel;
import com.mibarim.driver.models.Plus.PaymentDetailModel;
import com.mibarim.driver.models.Plus.StationRouteModel;
import com.mibarim.driver.models.Plus.SubStationModel;
import com.mibarim.driver.models.Route.BriefRouteModel;
import com.mibarim.driver.models.Route.RouteResponse;
import com.mibarim.driver.models.SubmitResult;
import com.mibarim.driver.models.UserInfoModel;
import com.mibarim.driver.services.AuthenticateService;
import com.mibarim.driver.services.RouteRequestService;
import com.mibarim.driver.services.RouteResponseService;
import com.mibarim.driver.services.UserInfoService;
import com.mibarim.driver.ui.BootstrapActivity;
import com.mibarim.driver.ui.HandleApiMessages;
import com.mibarim.driver.ui.HandleApiMessagesBySnackbar;
import com.mibarim.driver.ui.fragments.DriverFragments.DriverCardFragment;
import com.mibarim.driver.ui.fragments.DriverFragments.RoutesCardFragment;
import com.mibarim.driver.ui.fragments.DriverFragments.StationCardFragment;
import com.mibarim.driver.util.SafeAsyncTask;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import butterknife.ButterKnife;

//import org.osmdroid.tileprovider.constants.OpenStreetMapTileProviderConstants;


/**
 * Initial activity for the application.
 * * <p/>
 * If you need to remove the authentication from the application please see
 */
public class StationRouteListActivity extends BootstrapActivity {

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


    private static final String SubStationFragment="SubStationFragment";
    private CharSequence title;
    private Toolbar toolbar;
    private String authToken;
    private Tracker mTracker;
    private ApiResponse setRes;
    private View parentLayout;
    private boolean netErrorMsg = false;
    private StationRouteModel stationRouteModel;

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
        mTracker.setScreenName("StationRouteListActivity");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        mTracker.send(new HitBuilders.EventBuilder().setCategory("Activity").setAction("StationRouteListActivity").build());


        setContentView(R.layout.container_activity);
        parentLayout = findViewById(R.id.container_activity_root);
        // View injection with Butterknife
        ButterKnife.bind(this);

        if (getWindow().getDecorView().getLayoutDirection() == View.LAYOUT_DIRECTION_LTR) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }

        if (getIntent() != null && getIntent().getExtras() != null) {
            authToken = getIntent().getExtras().getString(Constants.Auth.AUTH_TOKEN);
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            if (Build.VERSION.SDK_INT >= 17) {
                actionBar.setHomeAsUpIndicator(R.mipmap.ic_menu_forward);
            }
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        //checkAuth();
        initScreen();
    }

    private void initScreen() {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.container, new RoutesCardFragment())
                .commit();
    }

    /*private void checkAuth() {
        new SafeAsyncTask<Boolean>() {

            @Override
            public Boolean call() throws Exception {
                final AuthenticateService svc = serviceProvider.getService(StationRouteListActivity.this);
                if (svc != null) {
                    authToken = serviceProvider.getAuthToken(StationRouteListActivity.this);
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
            }
        }.execute();
    }
*/

    /*public String getAuthToken() {
        return authToken;
    }*/

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
        finish();
    }

    @Subscribe
    public void onRestAdapterErrorEvent(RestAdapterErrorEvent event) {
        Snackbar.make(parentLayout, R.string.network_error, Snackbar.LENGTH_LONG).show();
    }


    @Override
    public void onBackPressed() {
        finish();
        return;
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


    public void selectStation(final StationRouteModel selectedItem) {
        stationRouteModel = selectedItem;
        final FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.container, new StationCardFragment(),SubStationFragment)
                .commit();
    }
    public void setRoute(final SubStationModel selectedItem) {

        showProgress();
        new SafeAsyncTask<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                if (authToken == null) {
                    serviceProvider.invalidateAuthToken();
                    authToken = serviceProvider.getAuthToken(StationRouteListActivity.this);
                }
                setRes = routeRequestService.setUserRoute(authToken, getRoute().StRouteId,selectedItem.StationId);
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
                new HandleApiMessagesBySnackbar(parentLayout, setRes).showMessages();
                Gson gson = new Gson();
                SubmitResult submitResult = new SubmitResult();
                for (String shareJson : setRes.Messages) {
                    submitResult = gson.fromJson(shareJson, SubmitResult.class);
                }
                if (submitResult.IsSubmited) {
                    finishIt();
                }
            }
        }.execute();
    }

    private void finishIt() {
        final Intent intent = getIntent();
        setResult(RESULT_OK, intent);
        finish();
    }

    public void gotoSuggestStation() {
        Intent i = new Intent(StationRouteListActivity.this, WebViewActivity.class);
        i.putExtra("URL", "https://goo.gl/forms/igP3kx2E3ilzGYDf1");
        startActivity(i);
    }

    public StationRouteModel getRoute() {
        return stationRouteModel;
    }

    public void removeSubStation() {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(SubStationFragment);
        if (fragment != null) {
            fragmentManager.beginTransaction().remove(fragment).commit();
        }

    }
}
