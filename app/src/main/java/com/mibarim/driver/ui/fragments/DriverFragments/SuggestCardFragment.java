package com.mibarim.driver.ui.fragments.DriverFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.brnunes.swipeablerecyclerview.SwipeableRecyclerViewTouchListener;
import com.mibarim.driver.R;
import com.mibarim.driver.adapters.SuggestRecyclerAdapter;
import com.mibarim.driver.ui.activities.MainActivity;

/**
 * Created by Arya on 11/25/2017.
 */


public class SuggestCardFragment extends Fragment implements LoaderManager.LoaderCallbacks<Object> {

    private View mRecycler;
    private RecyclerView mRecyclerView;
    SwipeRefreshLayout mSwipeRefreshLayout;
    private SuggestRecyclerAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mRecycler = inflater.inflate(R.layout.reload_suggest_card_list, null);

        mRecyclerView = (RecyclerView) mRecycler.findViewById(android.R.id.list);
        mSwipeRefreshLayout = (SwipeRefreshLayout) mRecycler.findViewById(R.id.swipe_refresh_layout_tab);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
                ((MainActivity) getActivity()).getUserScore();
            }
        });
        mRecyclerView.setLayoutManager(mLayoutManager);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public void refresh() {

        ((MainActivity) getActivity()).getRoutesListFromServer();

        getLoaderManager().restartLoader(0, null, this);
        //showState(1);
        mAdapter = new SuggestRecyclerAdapter();
        mRecyclerView.setAdapter(mAdapter);


    }

    @Override
    public Loader<Object> onCreateLoader(int i, Bundle bundle) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Object> loader, Object o) {

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int c = 0;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && c == 0) {
                    ((MainActivity) getActivity()).hidefab();
                    c = 1;
                }

                if (dy < 0 && c == 1) {
                    ((MainActivity) getActivity()).showFab();
                    c = 0;
                }


            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
//                    ((MainActivity)getActivity()).hidefab();
                }
            }


        });

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

        ((MainActivity) getActivity()).showSecondGuideTest();
    }

    @Override
    public void onLoaderReset(Loader<Object> loader) {

    }

}
