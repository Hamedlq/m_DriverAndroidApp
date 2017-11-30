package com.mibarim.driver.ui.fragments.DriverFragments;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.mibarim.driver.R;
import com.mibarim.driver.adapters.SuggestRecyclerAdapter;
import com.mibarim.driver.data.UserData;
import com.mibarim.driver.google.mapDetails;
import com.mibarim.driver.models.ApiResponse;
import com.mibarim.driver.models.Plus.SuggestModel;
import com.mibarim.driver.services.SuggestResponseService;
import com.mibarim.driver.ui.ThrowableLoader;
import com.mibarim.driver.ui.activities.MainActivity;
import com.mibarim.driver.util.SafeAsyncTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by Arya on 11/25/2017.
 */


public class SuggestCardFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<SuggestModel>> {


    @Inject
    protected SuggestResponseService service;

    private View mRecycler;
    private RecyclerView mRecyclerView;
    SwipeRefreshLayout mSwipeRefreshLayout;
    private SuggestRecyclerAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    List<SuggestModel> items;
    List<SuggestModel> latest;
    ItemTouchListener itemTouchListener;
    private ApiResponse response;
    private Tracker mTracker;
    private Typeface customFont;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        service = new SuggestResponseService();
        BootstrapApplication application = (BootstrapApplication) getActivity().getApplication();
        mTracker = application.getDefaultTracker();
        customFont = Typeface.createFromAsset(getActivity().getAssets(), "fonts/IRANSans(FaNum)_Light.ttf");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mRecycler = inflater.inflate(R.layout.reload_suggest_card_list, null);

        mRecyclerView = (RecyclerView) mRecycler.findViewById(R.id.suggest_list);
        mSwipeRefreshLayout = (SwipeRefreshLayout) mRecycler.findViewById(R.id.swipe_refresh_layout_tab);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        itemTouchListener = new ItemTouchListener() {

            @Override
            public void onButtonClick(View view, int position) {
                if (getActivity() instanceof MainActivity) {
                    SuggestModel selectedItem = items.get(position);
                    ((MainActivity) getActivity()).acceptSuggestRouteFirst(selectedItem.FilterId);
                }
            }

            @Override
            public void onMapClick(View view, int position) {
                if (getActivity() instanceof MainActivity) {
                    SuggestModel selectedItem = items.get(position);
                    Intent intent = new Intent(view.getContext(), mapDetails.class);
                    Bundle params = new Bundle();
                    params.putDouble("SRC_LAT", Double.parseDouble(selectedItem.SrcStLat));
                    params.putDouble("SRC_LNG", Double.parseDouble(selectedItem.SrcStLng));
                    params.putDouble("DST_LAT", Double.parseDouble(selectedItem.DstStLat));
                    params.putDouble("DST_LNG", Double.parseDouble(selectedItem.DstStLng));
                    intent.putExtras(params);
                    view.getContext().startActivity(intent);
                }
            }

        };

        return mRecycler;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mTracker.setScreenName("SuggestRouteCardFragment");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        mTracker.send(new HitBuilders.EventBuilder().setCategory("Fragment").setAction("SuggestRouteCardFragment").build());
        getLoaderManager().initLoader(0, null, this);
    }

    public void refresh() {

        getLoaderManager().restartLoader(0, null, this);
        //showState(1);
        mAdapter = new SuggestRecyclerAdapter(customFont, getActivity(), items, itemTouchListener);
        mRecyclerView.setAdapter(mAdapter);


    }

    @Override
    public Loader<List<SuggestModel>> onCreateLoader(int id, Bundle bundle) {

        mSwipeRefreshLayout.setRefreshing(true);
        items = new ArrayList<>();
        return new ThrowableLoader<List<SuggestModel>>(getActivity(), items) {
            @Override
            public List<SuggestModel> loadData() throws Exception {
                latest = new ArrayList<>();
                if (getActivity() instanceof MainActivity) {
                    Gson gson = new Gson();
                    if (getActivity() != null) {
                        response = service.getSuggestRoutes();
                        if (response != null) {
                            for (String routeJson : response.Messages) {
                                SuggestModel route = gson.fromJson(routeJson, SuggestModel.class);
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
    public void onLoadFinished(Loader<List<SuggestModel>> loader, List<SuggestModel> data) {
        items = data;
        mAdapter = new SuggestRecyclerAdapter(customFont, getActivity(), items, itemTouchListener);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addOnItemTouchListener(new SwipeableRecyclerViewTouchListener(mRecyclerView,
                new SwipeableRecyclerViewTouchListener.SwipeListener() {
                    @Override
                    public boolean canSwipeLeft(int position) {
                        return false;
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
    public void onLoaderReset(Loader<List<SuggestModel>> loader) {

    }

    public interface ItemTouchListener {

        public void onButtonClick(View view, int position);

        public void onMapClick(View view, int position);

    }

}
