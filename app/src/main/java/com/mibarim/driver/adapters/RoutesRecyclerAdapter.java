package com.mibarim.driver.adapters;

import android.app.Activity;
import android.database.DataSetObservable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;

import com.mibarim.driver.R;
import com.mibarim.driver.models.Plus.StationRouteModel;
import com.mibarim.driver.ui.fragments.DriverFragments.RoutesCardFragment;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.addAll;

/**
 * Created by Hamed on 10/16/2016.
 */
public class RoutesRecyclerAdapter extends RecyclerView.Adapter<RoutesRecyclerAdapter.ViewHolder> {
    private List<StationRouteModel> items;
    private Activity _activity;
    private RoutesCardFragment.ItemTouchListener onItemTouchListener;
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
        public TextView route_price;
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
            route_price = (TextView) v.findViewById(R.id.route_price);
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
            /*userimage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemTouchListener.onUserImageClick(v, getPosition());
                }
            });*/
        }
    }


    // Provide a suitable constructor (depends on the kind of dataset)
    public RoutesRecyclerAdapter(Activity activity, List<StationRouteModel> list, RoutesCardFragment.ItemTouchListener onItemTouchListener) {
        _activity = activity;
        items = list;
        mObjects = items;
        this.onItemTouchListener = onItemTouchListener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RoutesRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                               int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.station_route_card_item, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        /*if (items.get(position).IsBooked) {
            BadgeDrawable drawableBadge =
                    new BadgeDrawable.Builder()
                            .type(BadgeDrawable.TYPE_ONLY_ONE_TEXT)
                            .badgeColor(0xFF77FF00)
                            .text1("رزرو شد")
                            .build();
            SpannableString spannableString =
                    new SpannableString(TextUtils.concat(
                            items.get(position).TimingString,
                            " ",
                            drawableBadge.toSpannable()
                    ));
            holder.timing.setText(spannableString);
            //holder.book_trip.setVisibility(View.GONE);
        } else {
            if (items.get(position).EmptySeat == 0) {
                BadgeDrawable drawableBadge =
                        new BadgeDrawable.Builder()
                                .type(BadgeDrawable.TYPE_ONLY_ONE_TEXT)
                                .badgeColor(0xFFBF360C)
                                .text1("پرشد")
                                .build();
                SpannableString spannableString =
                        new SpannableString(TextUtils.concat(
                                items.get(position).TimingString,
                                " ",
                                drawableBadge.toSpannable()
                        ));
                holder.timing.setText(spannableString);
                //holder.book_trip.setVisibility(View.GONE);
            } else {
                holder.timing.setText(items.get(position).TimingString);
                *//*holder.book_trip.setVisibility(View.VISIBLE);
                holder.book_trip.setText("رزرو صندلی (" + items.get(position).PricingString + " تومان) ");*//*
            }
        }*/
        /*if (items.get(position).IsVerified) {
            BadgeDrawable drawableBadge =
                    new BadgeDrawable.Builder()
                            .type(BadgeDrawable.TYPE_ONLY_ONE_TEXT)
                            .badgeColor(0xFF77FF00)
                            .text1("✔")
                            .build();
            SpannableString spannableString =
                    new SpannableString(TextUtils.concat(
                            items.get(position).Name,
                            " ",
                            items.get(position).Family,
                            " ",
                            drawableBadge.toSpannable()
                    ));

            holder.username.setText(spannableString);
        } else {
            holder.username.setText(items.get(position).Name + " " + items.get(position).Family);
        }*/
        //holder.userimage.setImageBitmap(((MainActivity) _activity).getImageById(items.get(position).UserImageId, R.mipmap.ic_user_black));

        holder.src_address.setText(mObjects.get(position).SrcStAdd);
        holder.dst_address.setText(mObjects.get(position).DstStAdd);
        holder.route_price.setText(mObjects.get(position).StRoutePrice);
        /*holder.carString.setText(items.get(position).CarString);*/
        //holder.seats.setText("ظرفیت: " + items.get(position).EmptySeat + " از " + items.get(position).CarSeats);
        /*holder.src_distance.setText(items.get(position).SrcDistance);
        holder.dst_distance.setText(items.get(position).DstDistance);*/

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mObjects.size();
    }

    public List<StationRouteModel> getItems() {
        return mObjects;
    }

// added by me

    private RouteArrayFilter mFilter;
    private ArrayList<StationRouteModel> mOriginalValues;
    private final Object mLock = new Object();
    private List<StationRouteModel> mObjects;
    private boolean mNotifyOnChange = true;

    public Filter getFilter(String src, String dst) {
        if (mFilter != null) {
            mFilter = null;
        }

        mFilter = new RouteArrayFilter(src, dst);
        return mFilter;
    }


    private class RouteArrayFilter extends Filter {
        CharSequence srcPrefix;
        CharSequence dstPrefix;

        public RouteArrayFilter(CharSequence srcSearchWord, CharSequence dstSearchWord) {
            srcPrefix = srcSearchWord;
            dstPrefix = dstSearchWord;
        }

        @Override
        protected FilterResults performFiltering(CharSequence prefix1) {
            final FilterResults results = new FilterResults();
            //final String searchValue ;
            if (mOriginalValues == null) {
                synchronized (mLock) {
                    mOriginalValues = new ArrayList<>(mObjects);
                }
            }

            if ((srcPrefix == null || srcPrefix.length() == 0) && (dstPrefix == null || dstPrefix.length() == 0)) {
                final ArrayList<StationRouteModel> list;
                synchronized (mLock) {
                    list = new ArrayList<>(mOriginalValues);
                }
                results.values = list;
                results.count = list.size();
            } else {
                final ArrayList<StationRouteModel> values;
                synchronized (mLock) {
                    values = new ArrayList<StationRouteModel>(mOriginalValues);
                }
                final int count = values.size();
                final ArrayList<StationRouteModel> newValues = new ArrayList<>();

                if (srcPrefix == null || srcPrefix.length() == 0) {
                    final String dstString = dstPrefix.toString().toLowerCase();
                    for (int i = 0; i < count; i++) {
                        final StationRouteModel value = values.get(i);
                        String dstText = value.DstStAdd.toString().toLowerCase();//.replace('\u200C', ' ').replaceAll("\\s+","");
                        dstText = dstText.replace('\u200C', ' ').replaceAll(" ", "").replaceAll("،", "");


                        final String[] dstWords = dstText.split(" ");
                        for (String dstWord : dstWords) {
//                            if (dstWord.startsWith(dstString)) {
                            if (dstWord.toLowerCase().contains(dstString.toLowerCase())) {
                                newValues.add(value);
                                break;
                            }
                        }
                    }
                } else if (dstPrefix == null || dstPrefix.length() == 0) {
                    final String srcString = srcPrefix.toString().toLowerCase();

                    for (int i = 0; i < count; i++) {
                        final StationRouteModel value = values.get(i);

                        String srcText = value.SrcStAdd.toString().toLowerCase();
                        srcText = srcText.replace('\u200C', ' ').replaceAll(" ", "").replaceAll("،", "");
                        ;

                        final String[] srcWords = srcText.split(" ");
                        for (String srcWord : srcWords) {
//                            if (srcWord.startsWith(srcString)) {
                            if (srcWord.toLowerCase().contains(srcString.toLowerCase())) {
                                newValues.add(value);
                                break;
                            }
                        }

                    }
                } else {
                    final String srcString = srcPrefix.toString().toLowerCase();
                    final String dstString = dstPrefix.toString().toLowerCase();

                    for (int i = 0; i < count; i++) {
                        final StationRouteModel value = values.get(i);

                        String srcText = value.SrcStAdd.toString().toLowerCase();
                        srcText = srcText.replace('\u200C', ' ').replaceAll(" ", "").replaceAll("،", "");

                        String dstText = value.DstStAdd.toString().toLowerCase();
                        dstText = dstText.replace('\u200C', ' ').replaceAll(" ", "").replaceAll("،", "");

                        final String[] srcWords = srcText.split(" ");
                        final String[] dstWords = dstText.split(" ");
                        for (String srcWord : srcWords) {
                            for (String dstWord : dstWords) {
//                                if (srcWord.startsWith(srcString) && dstWord.startsWith(dstString)) {
                                if (srcWord.toLowerCase().contains(srcString.toLowerCase()) && dstWord.toLowerCase().contains(dstString.toLowerCase())) {
                                    newValues.add(value);
                                    break;
                                }
                            }
                        }

                    }
                }
                results.values = newValues;
                results.count = newValues.size();
            }

            return results;
        }




        public void clear() {
            synchronized (mLock) {
                if (mOriginalValues != null) {
                    mOriginalValues.clear();
                } else {
                    mObjects.clear();
                }
            }
            if (mNotifyOnChange) notifyDataSetChanged();
        }


        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            //noinspection unchecked
            mObjects = (List<StationRouteModel>) results.values;
            //clear();
            addAll(mObjects);
            //if (results.count > 0) {
            notifyDataSetChanged();
            /*} else {
                notifyDataSetInvalidated();
            }*/
        }
    }

}
