

package com.mibarim.driver.ui.activities;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.mibarim.driver.BootstrapApplication;
import com.mibarim.driver.BootstrapServiceProvider;
import com.mibarim.driver.R;
import com.mibarim.driver.authenticator.LogoutService;
import com.mibarim.driver.core.Constants;
import com.mibarim.driver.data.UserData;
import com.mibarim.driver.events.NetworkErrorEvent;
import com.mibarim.driver.events.RestAdapterErrorEvent;
import com.mibarim.driver.events.UnAuthorizedErrorEvent;
import com.mibarim.driver.models.ApiResponse;
import com.mibarim.driver.models.Plus.StationRouteModel;
import com.mibarim.driver.models.Plus.SubStationModel;
import com.mibarim.driver.models.RoutesDatabase;
import com.mibarim.driver.models.SubmitResult;
import com.mibarim.driver.services.RouteRequestService;
import com.mibarim.driver.services.RouteResponseService;
import com.mibarim.driver.services.UserInfoService;
import com.mibarim.driver.ui.BootstrapActivity;
import com.mibarim.driver.ui.HandleApiMessagesBySnackbar;
import com.mibarim.driver.ui.fragments.DriverFragments.RoutesCardFragment;
import com.mibarim.driver.ui.fragments.DriverFragments.StationCardFragment;
import com.mibarim.driver.util.SafeAsyncTask;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

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


    SearchView s1;
    SearchView s2;


    private static final String SubStationFragment="SubStationFragment";
    private static final String RoutesCardFragment="RoutesCardFragment";

    private CharSequence title;
    private Toolbar toolbar;
    private String authToken;
    private Tracker mTracker;
    private ApiResponse setRes;
    private View parentLayout;
    private boolean netErrorMsg = false;
    private StationRouteModel stationRouteModel;

    String srcText = null;
    String dstText = null;

    char asym1 = '\u064A'; // y arabi noghte daar ok
    char asym2 = '\u0649'; // y arabi binoghte ok

    char asym4 = '\u0643'; // kaafe arabi

    char asym3 = '\u0629'; // taye arabi


    char asym5 = '\u0671'; // alefe vasl arabi
    char asym6 = '\u0625'; // alf ba hamzeye paayin arabi

    //farsi symbols

    char fsym6 = '\u06CC'; //ye
    char fsym7 = '\u0626'; // ye baa hamze

    char fsym1 = '\u06A9'; // kaafe farsi
    char fsym2 = '\u0648'; //vaave farsi
    char fsym3 = '\u0622'; //aa ba kolah farsi
    char fsym4 = '\u0627'; // alef
    char fsym5 = '\u0623'; //alef ba hamzeye bala

    char fsym8 = '\u0647'; // he

    List<StationRouteModel> items;


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


        setContentView(R.layout.search_activity);
        parentLayout = findViewById(R.id.container_activity_root);
        // View injection with Butterknife
        ButterKnife.bind(this);

        if (getWindow().getDecorView().getLayoutDirection() == View.LAYOUT_DIRECTION_LTR) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }

        if (getIntent() != null && getIntent().getExtras() != null) {
            authToken = getIntent().getExtras().getString(Constants.Auth.AUTH_TOKEN);
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar_search);
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

        s1 = (SearchView) findViewById(R.id.searchView);
        s2 = (SearchView) findViewById(R.id.searchView2);
        //come second to be focusable
        s2.onActionViewExpanded();
        s1.onActionViewExpanded();
//        s1.setIconified(false);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


//        Fragment fragment = fragmentManager.findFragmentByTag(SubStationFragment);


        s1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                s1.setIconified(false);

            }
        });
        s2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                s2.setIconified(false);

            }
        });

        s1.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String temp;

                temp = newText.replace(asym1, fsym6);  // for y
                temp = temp.replace(asym2, fsym6); // for y
                temp = temp.replace(asym4, fsym1); // for kaaf
                temp = temp.replaceAll(" ","");
                srcText = temp;
                FragmentManager fragmentManager = getSupportFragmentManager();
                final Fragment routesCardFrag = fragmentManager.findFragmentByTag(RoutesCardFragment);
                ((RoutesCardFragment)routesCardFrag).searchText(srcText, dstText);

                return false;
            }
        });


        s2.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String temp;

                temp = newText.replace(asym1, fsym6);  // for y
                temp = temp.replace(asym2, fsym6); // for y
                temp = temp.replace(asym4, fsym1); // for kaaf
                temp = temp.replaceAll(" ","");
                dstText = temp;
                FragmentManager fragmentManager = getSupportFragmentManager();
                final Fragment routesCardFrag = fragmentManager.findFragmentByTag(RoutesCardFragment);
                ((RoutesCardFragment)routesCardFrag).searchText(srcText, dstText);
                return false;
            }
        });
    }

    private void initScreen() {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.container, new RoutesCardFragment(),RoutesCardFragment)
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



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

/*


        // Accessing the SearchAutoComplete
        int queryTextViewId = getResources().getIdentifier("android:id/search_src_text", null, null);
        View autoComplete = s1.findViewById(queryTextViewId);

        Class clazz = Class.forName("android.widget.SearchView$SearchAutoComplete");

        SpannableStringBuilder stopHint = new SpannableStringBuilder("   ");
        stopHint.append(getString("my text"));

// Add the icon as an spannable
        Drawable searchIcon = getResources().getDrawable(R.drawable.ic_action_search);
        Method textSizeMethod = clazz.getMethod("getTextSize");
        Float rawTextSize = (Float)textSizeMethod.invoke(autoComplete);
        int textSize = (int) (rawTextSize * 1.25);
        searchIcon.setBounds(0, 0, textSize, textSize);
        stopHint.setSpan(new ImageSpan(searchIcon), 1, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

// Set the new hint text
        Method setHintMethod = clazz.getMethod("setHint", CharSequence.class);
        setHintMethod.invoke(autoComplete, stopHint);

*/


        return super.onCreateOptionsMenu(menu);
    }



    public ArrayList<StationRouteModel> getRoutesFromDatabase(){


        ArrayList<StationRouteModel> latest = new ArrayList<>();

        RoutesDatabase routesDatabase = new RoutesDatabase(this);
        items = routesDatabase.routeResponseListQuery();

        ArrayList<StationRouteModel> myArray = new ArrayList<>();
//        ArrayList<String> myArray2 = new ArrayList<>();

        for (int i = 0; i < items.size(); i++) {
            StationRouteModel stationRouteModel = items.get(i);//.SrcStAdd + " - " + list.get(i).DstStAdd;
            stationRouteModel.StRoutePrice = stationRouteModel.StRoutePrice + " تومان ";
            latest.add(stationRouteModel);
        }

        return latest;
    }







}
