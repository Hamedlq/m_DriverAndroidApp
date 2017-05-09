package com.mibarim.driver.ui.fragments.DriverFragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.brnunes.swipeablerecyclerview.SwipeableRecyclerViewTouchListener;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.mibarim.driver.BootstrapApplication;
import com.mibarim.driver.BootstrapServiceProvider;
import com.mibarim.driver.R;
import com.mibarim.driver.adapters.DriverRouteRecyclerAdapter;
import com.mibarim.driver.authenticator.LogoutService;
import com.mibarim.driver.data.UserData;
import com.mibarim.driver.models.ApiResponse;
import com.mibarim.driver.models.Plus.DriverRouteModel;
import com.mibarim.driver.models.Plus.PassRouteModel;
import com.mibarim.driver.models.Route.RouteResponse;
import com.mibarim.driver.services.RouteResponseService;
import com.mibarim.driver.ui.ThrowableLoader;
import com.mibarim.driver.ui.activities.MainActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

public class DriverCardFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<List<DriverRouteModel>> {

    @Inject
    UserData userData;
    @Inject
    protected BootstrapServiceProvider serviceProvider;
    @Inject
    protected RouteResponseService routeResponseService;
    @Inject
    protected LogoutService logoutService;

    private int RELOAD_REQUEST = 1234;
    List<DriverRouteModel> items;
    List<DriverRouteModel> latest;
    private Tracker mTracker;
    private RouteResponse routeResponse;
    private ApiResponse suggestRouteResponse;

    private View mRecycler;
    private RecyclerView mRecyclerView;
    SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView mEmptyView;
    //private ProgressBar mProgressView;
    private DriverRouteRecyclerAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private int selectedRow;
    ItemTouchListener itemTouchListener;


    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BootstrapApplication.component().inject(this);
        // Obtain the shared Tracker instance.
        BootstrapApplication application = (BootstrapApplication) getActivity().getApplication();
        mTracker = application.getDefaultTracker();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        mRecycler = inflater.inflate(R.layout.reload_card_list, null);

        mRecyclerView = (RecyclerView) mRecycler.findViewById(android.R.id.list);
        mSwipeRefreshLayout = (SwipeRefreshLayout) mRecycler.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        mEmptyView = (TextView) mRecycler.findViewById(android.R.id.empty);
        mEmptyView.setVisibility(View.GONE);
        /*mProgressView = (ProgressBar) mRecycler.findViewById(R.id.pb_loading);*/

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        //showState(1);

        itemTouchListener = new ItemTouchListener() {

            @Override
            public void onSwitchCard(View view, int position) {
                if (getActivity() instanceof MainActivity) {
                    DriverRouteModel selectedItem = ((DriverRouteModel) items.get(position));
                    ((MainActivity) getActivity()).ToggleTrip(selectedItem);
                }
            }

            @Override
            public void onCardViewTap(View view, int position) {
                if (getActivity() instanceof MainActivity) {
                    DriverRouteModel selectedItem = ((DriverRouteModel) items.get(position));
                    ((MainActivity) getActivity()).ToggleTrip(selectedItem);
                }
            }

            @Override
            public void onDeleteCard(View view, int position) {
                if (getActivity() instanceof MainActivity) {
                    DriverRouteModel selectedItem = ((DriverRouteModel) items.get(position));
                    ((MainActivity) getActivity()).deleteRoute(selectedItem.DriverRouteId);
                }
            }

            /*@Override
            public void onUserImageClick(View view, int position) {
                *//*PassRouteModel model = items.get(position);
                ContactModel contactModel = new ContactModel();
                //dirty coding-It's temporary Used
                contactModel.ContactId = model.TripId;
                contactModel.Name = model.Name;
                contactModel.Family = model.Family;
                contactModel.UserImageId = model.UserImageId;*//*
                //((SuggestRouteActivity) getActivity()).goToContactActivity(contactModel);

            }*/

        };


        return mRecycler;
    }

    public void refresh() {
        getLoaderManager().restartLoader(0, null, this);
        //showState(1);
        mAdapter = new DriverRouteRecyclerAdapter(getActivity(), items, itemTouchListener);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mTracker.setScreenName("SuggestRouteCardFragment");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        mTracker.send(new HitBuilders.EventBuilder().setCategory("Fragment").setAction("SuggestRouteCardFragment").build());
        getLoaderManager().initLoader(0, null, this);
        //setEmptyText(R.string.no_routes);
    }

    /*public void showState(int showState) {
        mEmptyView.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.GONE);
        mProgressView.setVisibility(View.GONE);
        //1 progress
        //2 show result list
        //3 no result
        switch (showState) {
            case 1:
                mProgressView.setVisibility(View.VISIBLE);
                break;
            case 2:
                mRecyclerView.setVisibility(View.VISIBLE);
                break;
            case 3:
                mEmptyView.setVisibility(View.VISIBLE);
                break;
            default:
                mEmptyView.setVisibility(View.VISIBLE);
        }
    }*/

    @Override
    public Loader<List<DriverRouteModel>> onCreateLoader(int id, Bundle args) {
        mEmptyView.setVisibility(View.GONE);
        mSwipeRefreshLayout.setRefreshing(true);
        items = new ArrayList<DriverRouteModel>();
        return new ThrowableLoader<List<DriverRouteModel>>(getActivity(), items) {
            @Override
            public List<DriverRouteModel> loadData() throws Exception {
                latest = new ArrayList<DriverRouteModel>();
                if (getActivity() instanceof MainActivity) {
                    Gson gson = new Gson();
                    //routeResponse = ((MainActivity) getActivity()).getRoute();
                    String authToken = serviceProvider.getAuthToken(getActivity());
                    if (getActivity() != null) {
                        suggestRouteResponse = routeResponseService.GetDriverRoutes(authToken, 1);
                        if (suggestRouteResponse != null) {
                            for (String routeJson : suggestRouteResponse.Messages) {
                                DriverRouteModel route = gson.fromJson(routeJson, DriverRouteModel.class);
                                latest.add(route);
                            }
                        }
                    }
                }
                if (latest != null) {
                    return latest;
                } else {
                    return Collections.emptyList();
                }
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<List<DriverRouteModel>> loader, List<DriverRouteModel> data) {
        items = data;
        if (items.size() == 0) {
            mEmptyView.setVisibility(View.VISIBLE);
        }
        // specify an adapter (see also next example)
        mAdapter = new DriverRouteRecyclerAdapter(getActivity(), items, itemTouchListener);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnItemTouchListener(new SwipeableRecyclerViewTouchListener(mRecyclerView,
                new SwipeableRecyclerViewTouchListener.SwipeListener() {
                    @Override
                    public boolean canSwipeLeft(int position) {
                        return true;
                    }

                    @Override
                    public boolean canSwipeRight(int position) {
                        return false;
                    }

                    @Override
                    public void onDismissedBySwipeLeft(RecyclerView recyclerView, int[] reverseSortedPositions) {
                    }

                    @Override
                    public void onDismissedBySwipeRight(RecyclerView recyclerView, int[] reverseSortedPositions) {
                    }
                }));
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onLoaderReset(Loader<List<DriverRouteModel>> loader) {

    }

    public interface ItemTouchListener {
        public void onCardViewTap(View view, int position);

        public void onSwitchCard(View view, int position);
        public void onDeleteCard(View view, int position);

    }


}
