package com.mibarim.driver.adapters;

import android.app.Activity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.beardedhen.androidbootstrap.BootstrapCircleThumbnail;
import com.mibarim.driver.R;
import com.mibarim.driver.models.Plus.DriverRouteModel;
import com.mibarim.driver.models.Plus.PassRouteModel;
import com.mibarim.driver.ui.activities.MainActivity;
import com.mibarim.driver.ui.fragments.DriverFragments.DriverCardFragment;

import java.util.List;

import cn.nekocode.badge.BadgeDrawable;

/**
 * Created by Hamed on 10/16/2016.
 */
public class DriverRouteRecyclerAdapter extends RecyclerView.Adapter<DriverRouteRecyclerAdapter.ViewHolder> {
    private List<DriverRouteModel> items;
    private Activity _activity;
    private DriverCardFragment.ItemTouchListener onItemTouchListener;
    //private RelativeLayout lastLayout;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        //public RelativeLayout row_layout;
        //public TextView username;
        public TextView timing;
        //public TextView seats;
        public TextView src_address;
        public TextView dst_address;
        public TextView carString;
        public TextView seats;
        public Switch switch_trip;
        public LinearLayout delete_btn;
        public AwesomeTextView fa_trash;
        /*public TextView src_distance;
        public TextView dst_distance;*/
        //public BootstrapCircleThumbnail userimage;
        //public AppCompatButton book_trip;


        public ViewHolder(View v) {
            super(v);
            //row_layout = (RelativeLayout) v.findViewById(R.id.row_layout);
            //username = (TextView) v.findViewById(R.id.username);
            timing = (TextView) v.findViewById(R.id.timing);
            //seats = (TextView) v.findViewById(R.id.seats);
            src_address = (TextView) v.findViewById(R.id.src_address);
            dst_address = (TextView) v.findViewById(R.id.dst_address);
            carString = (TextView) v.findViewById(R.id.carString);
            seats = (TextView) v.findViewById(R.id.seats);
            switch_trip = (Switch) v.findViewById(R.id.switch_trip);
            delete_btn = (LinearLayout) v.findViewById(R.id.delete_btn);
            fa_trash = (AwesomeTextView) v.findViewById(R.id.fa_trash);
            /*src_distance = (TextView) v.findViewById(R.id.src_distance);
            dst_distance = (TextView) v.findViewById(R.id.dst_distance);*/
//            userimage = (BootstrapCircleThumbnail) v.findViewById(R.id.userimage);
            //book_trip = (AppCompatButton) v.findViewById(R.id.book_trip);
            /*book_trip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemTouchListener.onBookBtnClick(v, getPosition());
                }
            });*/


            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemTouchListener.onCardViewTap(v, getPosition());
                }
            });

            switch_trip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemTouchListener.onSwitchCard(v, getPosition());
                }
            });
            delete_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemTouchListener.onDeleteCard(v, getPosition());
                }
            });
            fa_trash.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemTouchListener.onDeleteCard(v, getPosition());
                }
            });
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public DriverRouteRecyclerAdapter(Activity activity, List<DriverRouteModel> list, DriverCardFragment.ItemTouchListener onItemTouchListener) {
        _activity = activity;
        items = list;
        this.onItemTouchListener = onItemTouchListener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public DriverRouteRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                    int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.driver_card_item, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.timing.setText(items.get(position).TimingString);
        holder.src_address.setText(items.get(position).SrcAddress);
        holder.dst_address.setText(items.get(position).DstAddress);
        holder.carString.setText(items.get(position).CarString);
        holder.switch_trip.setChecked(items.get(position).HasTrip);
        if (items.get(position).HasTrip) {
            holder.seats.setText("رزرو: " + items.get(position).FilledSeats + " از " + items.get(position).CarSeats);
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return items.size();
    }
}
