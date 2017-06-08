package com.mibarim.driver.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mibarim.driver.R;
import com.mibarim.driver.models.Plus.StationRouteModel;
import com.mibarim.driver.models.Plus.SubStationModel;
import com.mibarim.driver.ui.fragments.DriverFragments.RoutesCardFragment;
import com.mibarim.driver.ui.fragments.DriverFragments.StationCardFragment;

import java.util.List;

/**
 * Created by Hamed on 10/16/2016.
 */
public class StationsRecyclerAdapter extends RecyclerView.Adapter<StationsRecyclerAdapter.ViewHolder> {
    private List<SubStationModel> items;
    private Activity _activity;
    private StationCardFragment.ItemTouchListener onItemTouchListener;
    //private RelativeLayout lastLayout;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        //public RelativeLayout row_layout;
        //public TextView username;
        public TextView station_add;


        public ViewHolder(View v) {
            super(v);
            station_add = (TextView) v.findViewById(R.id.station_add);


            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemTouchListener.onCardViewTap(v, getPosition());
                }
            });
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public StationsRecyclerAdapter(Activity activity, List<SubStationModel> list, StationCardFragment.ItemTouchListener onItemTouchListener) {
        _activity = activity;
        items = list;
        this.onItemTouchListener = onItemTouchListener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public StationsRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                 int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.station_card_item, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.station_add.setText(items.get(position).StAdd);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return items.size();
    }
}
