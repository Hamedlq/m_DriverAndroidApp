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
import com.mibarim.driver.adapters.RoutesRecyclerAdapter;
import com.mibarim.driver.authenticator.LogoutService;
import com.mibarim.driver.data.UserData;
import com.mibarim.driver.models.ApiResponse;
import com.mibarim.driver.models.Plus.PassRouteModel;
import com.mibarim.driver.models.Plus.StationRouteModel;
import com.mibarim.driver.models.Route.RouteResponse;
import com.mibarim.driver.services.RouteResponseService;
import com.mibarim.driver.ui.ThrowableLoader;
import com.mibarim.driver.ui.activities.MainActivity;
import com.mibarim.driver.ui.activities.StationRouteListActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

public class RoutesCardFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<List<StationRouteModel>> {

/*    @Inject
    UserData userData;
    @Inject
    protected BootstrapServiceProvider serviceProvider;*/
    @Inject
    protected RouteResponseService routeResponseService;
/*    @Inject
    protected LogoutService logoutService;*/

    private int RELOAD_REQUEST = 1234;
    List<StationRouteModel> items;
    List<StationRouteModel> latest;
    private Tracker mTracker;
    private RouteResponse routeResponse;
    private ApiResponse stationRouteResponse;

    private View mRecycler;
    private RecyclerView mRecyclerView;
    SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView mEmptyView;
    //private ProgressBar mProgressView;
    private RoutesRecyclerAdapter mAdapter;
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
            public void onCardViewTap(View view, int position) {
                if (getActivity() instanceof StationRouteListActivity) {
                    StationRouteModel selectedItem = ((StationRouteModel) items.get(position));
                    ((StationRouteListActivity) getActivity()).setRoute(selectedItem);
                }
            }
            /*@Override
            public void onCardViewTap(View view, int position) {
                //PassRouteModel selectedItem = ((PassRouteModel) items.get(position));
                *//*String srcLat = latest.get(position).SrcLatitude;
                String srcLng = latest.get(position).SrcLongitude;
                String dstLat = latest.get(position).DstLatitude;
                String dstLng = latest.get(position).DstLongitude;
                PathPoint pathRoute= latest.get(position).PathRoute;*//*
                *//*((SuggestRouteActivity) getActivity()).setSelectedRoute(selectedItem);
                ((SuggestRouteActivity) getActivity()).setRouteSrcDst(srcLat, srcLng, dstLat, dstLng,pathRoute, position);*//*
                //((SuggestRouteCardActivity) getActivity()).gotoTripProfile(selectedItem);

            }*/

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
        mAdapter = new RoutesRecyclerAdapter(getActivity(), items, itemTouchListener);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mTracker.setScreenName("RoutesCardFragment");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        mTracker.send(new HitBuilders.EventBuilder().setCategory("Fragment").setAction("RoutesCardFragment").build());
        getLoaderManager().initLoader(0, null, this);
        //setEmptyText(R.string.no_routes);
    }

    @Override
    public Loader<List<StationRouteModel>> onCreateLoader(int id, Bundle args) {
        mEmptyView.setVisibility(View.GONE);
        mSwipeRefreshLayout.setRefreshing(true);
        items = new ArrayList<StationRouteModel>();
        return new ThrowableLoader<List<StationRouteModel>>(getActivity(), items) {
            @Override
            public List<StationRouteModel> loadData() throws Exception {
                latest = new ArrayList<StationRouteModel>();
                Gson gson = new Gson();
                if (getActivity() != null) {
                    stationRouteResponse = routeResponseService.GetStationRoutes( 1);
                    if (stationRouteResponse != null) {
                        for (String routeJson : stationRouteResponse.Messages) {
                            StationRouteModel route = gson.fromJson(routeJson, StationRouteModel.class);
                            latest.add(route);
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
    public void onLoadFinished(Loader<List<StationRouteModel>> loader, List<StationRouteModel> data) {
        items = data;
        /*if (items.size() == 0) {
            mEmptyView.setVisibility(View.VISIBLE);
        }*/
        // specify an adapter (see also next example)
        mAdapter = new RoutesRecyclerAdapter(getActivity(), items, itemTouchListener);
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
    public void onLoaderReset(Loader<List<StationRouteModel>> loader) {

    }

    public interface ItemTouchListener {
        public void onCardViewTap(View view, int position);
        //public void onBookBtnClick(View view, int position);
        //public void onUserImageClick(View view, int position);

    }
}