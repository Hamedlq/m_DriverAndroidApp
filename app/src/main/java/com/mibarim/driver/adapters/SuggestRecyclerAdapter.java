package com.mibarim.driver.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.mibarim.driver.ui.activities.MainActivity;

/**
 * Created by Arya on 11/25/2017.
 */

public class SuggestRecyclerAdapter extends RecyclerView.Adapter<SuggestRecyclerAdapter.ViewHolder> {

    @Override
    public SuggestRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return null;
    }

    @Override
    public void onBindViewHolder(SuggestRecyclerAdapter.ViewHolder viewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

}
