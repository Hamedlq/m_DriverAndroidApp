package com.mibarim.driver.ui.fragments.routeFragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.brnunes.swipeablerecyclerview.SwipeableRecyclerViewTouchListener;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.mibarim.driver.BootstrapApplication;
import com.mibarim.driver.BootstrapServiceProvider;
import com.mibarim.driver.R;
import com.mibarim.driver.adapters.SuggestRouteRecyclerAdapter;
import com.mibarim.driver.authenticator.LogoutService;
import com.mibarim.driver.data.UserData;
import com.mibarim.driver.models.ApiResponse;
import com.mibarim.driver.models.ContactModel;
import com.mibarim.driver.models.Route.BriefRouteModel;
import com.mibarim.driver.models.Route.RouteResponse;
import com.mibarim.driver.services.RouteResponseService;
import com.mibarim.driver.ui.ThrowableLoader;
import com.mibarim.driver.ui.activities.SuggestRouteCardActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

public class SuggestRouteCardFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<List<BriefRouteModel>> {

    @Inject
    UserData userData;
    @Inject
    protected BootstrapServiceProvider serviceProvider;
    @Inject
    protected RouteResponseService routeResponseService;
    @Inject
    protected LogoutService logoutService;

    private int RELOAD_REQUEST = 1234;
    List<BriefRouteModel> items;
    List<BriefRouteModel> latest;
    private Tracker mTracker;
    private RouteResponse routeResponse;
    private ApiResponse suggestRouteResponse;

    private View mRecycler;
    private RecyclerView mRecyclerView;
    private TextView mEmptyView;
    private ProgressBar mProgressView;
    private SuggestRouteRecyclerAdapter mAdapter;
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
        mRecycler = inflater.inflate(R.layout.card_list, null);

        mRecyclerView = (RecyclerView) mRecycler.findViewById(android.R.id.list);
        mEmptyView = (TextView) mRecycler.findViewById(android.R.id.empty);
        mProgressView = (ProgressBar) mRecycler.findViewById(R.id.pb_loading);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        showState(1);

        itemTouchListener = new ItemTouchListener() {
            @Override
            public void onCardViewTap(View view, int position) {
                BriefRouteModel selectedItem = ((BriefRouteModel) items.get(position));
                /*String srcLat = latest.get(position).SrcLatitude;
                String srcLng = latest.get(position).SrcLongitude;
                String dstLat = latest.get(position).DstLatitude;
                String dstLng = latest.get(position).DstLongitude;
                PathPoint pathRoute= latest.get(position).PathRoute;*/
                /*((SuggestRouteActivity) getActivity()).setSelectedRoute(selectedItem);
                ((SuggestRouteActivity) getActivity()).setRouteSrcDst(srcLat, srcLng, dstLat, dstLng,pathRoute, position);*/
                ((SuggestRouteCardActivity) getActivity()).gotoTripProfile(selectedItem);

            }

            @Override
            public void onUserImageClick(View view, int position) {
                BriefRouteModel model=items.get(position);
                ContactModel contactModel = new ContactModel();
                //dirty coding-It's temporary Used
                contactModel.ContactId=model.RouteId;
                contactModel.Name=model.Name;
                contactModel.Family=model.Family;
                contactModel.UserImageId=model.UserImageId;
                //((SuggestRouteActivity) getActivity()).goToContactActivity(contactModel);

            }

        };


        return mRecycler;
    }

    public void refresh() {
        getLoaderManager().restartLoader(0, null, this);
        showState(1);
        mAdapter = new SuggestRouteRecyclerAdapter(getActivity(),items, itemTouchListener);
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

    public void showState(int showState) {
        mEmptyView.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.GONE);
        mProgressView.setVisibility(View.GONE);
        //1 progress
        //2 show result list
        //3 no result
       switch (showState){
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
    }

    @Override
    public Loader<List<BriefRouteModel>> onCreateLoader(int id, Bundle args) {
        items = new ArrayList<BriefRouteModel>();
        return new ThrowableLoader<List<BriefRouteModel>>(getActivity(), items) {
            @Override
            public List<BriefRouteModel> loadData() throws Exception {
                latest = new ArrayList<BriefRouteModel>();
                if (getActivity() instanceof SuggestRouteCardActivity) {
                    Gson gson = new Gson();
                    routeResponse = ((SuggestRouteCardActivity) getActivity()).getRoute();
                    String authToken = serviceProvider.getAuthToken(getActivity());
                    if (getActivity() != null) {
                        suggestRouteResponse = routeResponseService.GetRouteSuggests(authToken, routeResponse.RouteId);
                        if (suggestRouteResponse != null) {
                            for (String routeJson : suggestRouteResponse.Messages) {
                                BriefRouteModel route = gson.fromJson(routeJson, BriefRouteModel.class);
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
    public void onLoadFinished(Loader<List<BriefRouteModel>> loader, List<BriefRouteModel> data) {
        items = data;
        if (items.size() == 0) {
            showState(3);
        } else {
            showState(2);
        }

        // specify an adapter (see also next example)
        mAdapter = new SuggestRouteRecyclerAdapter(getActivity(),items, itemTouchListener);
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

    }

    @Override
    public void onLoaderReset(Loader<List<BriefRouteModel>> loader) {

    }

    public interface ItemTouchListener {
        public void onCardViewTap(View view, int position);

        public void onUserImageClick(View view, int position);

    }


}
