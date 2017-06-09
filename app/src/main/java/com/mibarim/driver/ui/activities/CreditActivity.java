

package com.mibarim.driver.ui.activities;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mibarim.driver.BootstrapApplication;
import com.mibarim.driver.R;
import com.mibarim.driver.authenticator.TokenRefreshActivity;
import com.mibarim.driver.core.Constants;
import com.mibarim.driver.events.UnAuthorizedErrorEvent;
import com.mibarim.driver.models.ApiResponse;
import com.mibarim.driver.services.UserInfoService;
import com.mibarim.driver.ui.BootstrapActivity;
import com.mibarim.driver.ui.HandleApiMessagesBySnackbar;
import com.mibarim.driver.ui.fragments.addRouteFragments.DriveFragment;
import com.mibarim.driver.ui.fragments.userInfoFragments.WithdrawMainFragment;
import com.mibarim.driver.util.SafeAsyncTask;
import com.mibarim.driver.util.Strings;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;


/**
 * Initial activity for the application.
 * * <p/>
 * If you need to remove the authentication from the application please see
 */
public class CreditActivity extends BootstrapActivity {

    @Inject
    UserInfoService userInfoService;

    private Tracker mTracker;
    private CharSequence title;
    private Toolbar toolbar;
    private int RELOAD_REQUEST = 1234;
    protected ApiResponse response;
    private View parentLayout;
    private String authToken;
    private String remain;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        BootstrapApplication.component().inject(this);
        BootstrapApplication application = (BootstrapApplication) getApplication();
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName("CreditActivity");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        mTracker.send(new HitBuilders.EventBuilder().setCategory("Activity").setAction("CreditActivity").build());

        setContentView(R.layout.container_activity);
        parentLayout = findViewById(R.id.container_activity_root);
        // View injection with Butterknife
        ButterKnife.bind(this);

        if (getWindow().getDecorView().getLayoutDirection() == View.LAYOUT_DIRECTION_LTR) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }

        if (getIntent() != null && getIntent().getExtras() != null) {
            authToken = getIntent().getExtras().getString(Constants.Auth.AUTH_TOKEN);
            remain = getIntent().getExtras().getString(Constants.GlobalConstants.CREDIT_REMAIN);
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
        initScreen();
    }

    private void initScreen() {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.container, new WithdrawMainFragment())
                .commit();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Subscribe
    public void onUnAuthorizedErrorEvent(UnAuthorizedErrorEvent event) {
        finish();
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

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void done() {
        setResult(RESULT_OK);
        this.finish();
    }

    public String getRemain() {
        return remain;
    }

    public void submitWithdrawRequest(final String withdrawAmount) {
        if(withdrawAmount.equals(""))
        {
            Snackbar.make(parentLayout, R.string.no_amount, BaseTransientBottomBar.LENGTH_LONG);
        }else {
            showProgress();
            new SafeAsyncTask<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    response = userInfoService.submitWithdrawRequest(authToken, withdrawAmount);
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
                    if (state) {
                        new HandleApiMessagesBySnackbar(parentLayout, response).showMessages();
                        if (response.Messages != null && response.Messages.size() > 0) {
                            for (String msg : response.Messages) {
                                Snackbar.make(parentLayout, msg, BaseTransientBottomBar.LENGTH_LONG);
                                clearCode();
                            }
                        }
                    }
                }
            }.execute();

        }
    }

    private void clearCode() {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.container);
        ((WithdrawMainFragment) fragment).ClearCode();
    }
}
