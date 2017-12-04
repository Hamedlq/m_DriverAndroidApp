package com.mibarim.driver.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.mibarim.driver.R;
import com.mibarim.driver.google.mapDetails;
import com.mibarim.driver.models.Plus.DriverRouteModel;
import com.mibarim.driver.models.enums.TripStates;
import com.mibarim.driver.ui.fragments.DriverFragments.DriverCardFragment;
import com.mibarim.driver.ui.fragments.messagingFragments.ToggleContactTripFragment;
import com.mibarim.driver.ui.fragments.userInfoFragments.InviteFragment;

import java.util.Calendar;
import java.util.List;

import retrofit.RestAdapter;

/**
 * Created by Hamed on 10/16/2016.
 */
public class DriverRouteRecyclerAdapter extends RecyclerView.Adapter<DriverRouteRecyclerAdapter.ViewHolder> {
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private List<DriverRouteModel> items;
    private Activity _activity;
    private DriverCardFragment.ItemTouchListener onItemTouchListener;
    private static final int MAX_CLICK_DURATION = 200;
    private long startClickTime;

    private float mDownX;
    private float mDownY;
    private final float SCROLL_THRESHOLD = 10;
    private boolean isOnClick;

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
        public TextView driverCardPrice;
        public TextView carString;
        public TextView seats;
        public ToggleButton switch_trip;
        public RelativeLayout fa_trash;
        public AppCompatButton show_trip;
        public ImageView go_to_map;
        public ImageView  src_station;
        public ImageView  st_destination;
        public ImageView dots;
        /*public TextView src_distance;
        public TextView dst_distance;*/
        //public BootstrapCircleThumbnail userimage;
        //public AppCompatButton book_trip;


        public ViewHolder(View v) {
            super(v);
            //row_layout = (RelativeLayout) v.findViewById(R.id.row_layout);
            //username = (TextView) v.findViewById(R.id.username);
            timing = (TextView) v.findViewById(R.id.timing);
            driverCardPrice = (TextView) v.findViewById(R.id.driver_card_price);
            //seats = (TextView) v.findViewById(R.id.seats);
            src_address = (TextView) v.findViewById(R.id.src_address);
            dst_address = (TextView) v.findViewById(R.id.dst_address);
            carString = (TextView) v.findViewById(R.id.carString);
            seats = (TextView) v.findViewById(R.id.seats);
            switch_trip = (ToggleButton) v.findViewById(R.id.switch_trip);
            fa_trash = (RelativeLayout) v.findViewById(R.id.deleteCard);
            show_trip = (AppCompatButton) v.findViewById(R.id.show_trip);
            go_to_map = (ImageView) v.findViewById(R.id.goToMap);
            src_station = (ImageView)v.findViewById(R.id.src_station);
            st_destination = (ImageView)v.findViewById(R.id.st_destination);
            dots = (ImageView)v.findViewById(R.id.dots);
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


            v.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN: {
                            startClickTime = Calendar.getInstance().getTimeInMillis();
                            break;
                        }
                        case MotionEvent.ACTION_UP: {
                            long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
                            if(clickDuration < MAX_CLICK_DURATION) {
                                onItemTouchListener.onCardViewTap(v, getPosition());
                            }
                            break;
                        }
                        default:
                            break;
                    }
                    return true;
                }
            });


            switch_trip.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        onItemTouchListener.onSwitchCard(v, getPosition());
                        return true;
                    }
                    return false;
                }
            });


            fa_trash.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        onItemTouchListener.onDeleteCard(v, getPosition());
                        return true;
                    }
                    return false;
                }
            });

            go_to_map.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN: {
                            startClickTime = Calendar.getInstance().getTimeInMillis();
                            break;
                        }
                        case MotionEvent.ACTION_UP: {
                            long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
                            if(clickDuration < MAX_CLICK_DURATION) {
                                onItemTouchListener.onSrcLinkClick(v, getPosition(), src_address.getText().toString(), dst_address.getText().toString());
                            }
                            break;
                        }
                        default:
                            break;
                    }
                    return true;
                }
                /*@Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        onItemTouchListener.onSrcLinkClick(v, getPosition());
                        return true;
                    }
                    return false;
                }*/
            });

            /*src_address.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN: {
                            startClickTime = Calendar.getInstance().getTimeInMillis();
                            break;
                        }
                        case MotionEvent.ACTION_UP: {
                            long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
                            if(clickDuration < MAX_CLICK_DURATION) {
                                onItemTouchListener.onSrcLinkClick(v, getPosition());
                            }
                            break;
                        }
                        default:
                            break;
                    }
                    return true;
                }
            });*/
            /*dst_address.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN: {
                            startClickTime = Calendar.getInstance().getTimeInMillis();
                            break;
                        }
                        case MotionEvent.ACTION_UP: {
                            long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
                            if(clickDuration < MAX_CLICK_DURATION) {
                                onItemTouchListener.onDstLinkClick(v, getPosition());
                            }
                            break;
                        }
                        default:
                            break;
                    }
                    return true;

                }
            });*/
            st_destination.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN: {
                            startClickTime = Calendar.getInstance().getTimeInMillis();
                            break;
                        }
                        case MotionEvent.ACTION_UP: {
                            long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
                            if(clickDuration < MAX_CLICK_DURATION) {
                                onItemTouchListener.onDstLinkClick(v, getPosition());
                            }
                            break;
                        }
                        default:
                            break;
                    }
                    return true;

                }
            });
            show_trip.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        onItemTouchListener.onBtnClick(v, getPosition());
                        return true;
                    }
                    return false;
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
        holder.src_address.setText(items.get(position).SrcMainAddress +"، "+ items.get(position).SrcAddress);
        holder.dst_address.setText(items.get(position).DstAddress);
        holder.carString.setText(items.get(position).CarString);
        holder.driverCardPrice.append(items.get(position).PricingString + " ");
        holder.switch_trip.setChecked(items.get(position).HasTrip);
        holder.show_trip.setVisibility(View.GONE);
        if (items.get(position).HasTrip) {
            holder.seats.setText("رزرو: " + items.get(position).FilledSeats + " از " + items.get(position).CarSeats);
            if(items.get(position).TripState== TripStates.InPreTripTime.toInt()
                    || items.get(position).TripState== TripStates.InTripTime.toInt()
                    || items.get(position).TripState== TripStates.InRiding.toInt()){
                holder.show_trip.setVisibility(View.VISIBLE);
            }


            holder.src_address.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),R.color.actionbar_background_end));
            holder.src_address.setTypeface(holder.src_address.getTypeface(), Typeface.BOLD);
            holder.dst_address.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),R.color.actionbar_background_end));
            holder.dst_address.setTypeface(holder.dst_address.getTypeface(),Typeface.BOLD);

            holder.src_station.setImageResource(R.drawable.src_icon);
            holder.st_destination.setImageResource(R.drawable.dst_icon);
            holder.dots.setImageResource(R.drawable.three_dots);

        }

        if (!items.get(position).HasTrip) {

            holder.timing.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),R.color.table_text_light));
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return items.size();
    }
    public boolean servicesOK() {

        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(_activity);

        int isAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(_activity);

        if (isAvailable == ConnectionResult.SUCCESS)
            return true;
        else if (GooglePlayServicesUtil.isUserRecoverableError(isAvailable)) {
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(isAvailable, _activity, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(_activity , "با عرض پوزش سرویس گوگل در دسترس نمی باشد.", Toast.LENGTH_SHORT).show();
        }

        return false;
    }




}
