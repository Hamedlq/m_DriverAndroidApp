package com.mibarim.driver.adapters;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mibarim.driver.R;
import com.mibarim.driver.models.Plus.SuggestModel;
import com.mibarim.driver.models.StructNote;
import com.mibarim.driver.ui.activities.MainActivity;
import com.mibarim.driver.ui.fragments.DriverFragments.SuggestCardFragment;

import java.util.Calendar;
import java.util.List;

/**
 * Created by Arya on 11/25/2017.
 */

public class SuggestRecyclerAdapter extends RecyclerView.Adapter<SuggestRecyclerAdapter.ViewHolder> {

    private List<SuggestModel> items;
    private Activity _activity;
    private SuggestCardFragment.ItemTouchListener onItemTouchListener;
    private long startClickTime;
    private static final int MAX_CLICK_DURATION = 200;
    private Typeface customFont;

    public SuggestRecyclerAdapter(Typeface customFont, Activity activity, List<SuggestModel> list, SuggestCardFragment.ItemTouchListener onItemTouchListener) {
        _activity = activity;
        items = list;
        this.onItemTouchListener = onItemTouchListener;
        this.customFont = customFont;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public Button acceptSuggestRoute;
        public TextView srcAddress;
        public TextView dstAddress;
        public TextView time;
        public ImageView gotoMap;
        private TextView suggestPrice;
        private TextView requests;

        public ViewHolder(View itemView) {
            super(itemView);

            srcAddress = (TextView) itemView.findViewById(R.id.src_suggest_address);
            dstAddress = (TextView) itemView.findViewById(R.id.dst_suggest_address);
            acceptSuggestRoute = (Button) itemView.findViewById(R.id.accept_suggest_route);
            time = (TextView) itemView.findViewById(R.id.suggest_time);
            gotoMap = (ImageView) itemView.findViewById(R.id.goToMap_suggest);
            suggestPrice = (TextView) itemView.findViewById(R.id.suggest_price);
            requests = (TextView) itemView.findViewById(R.id.requests);

            acceptSuggestRoute.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN: {
                            startClickTime = Calendar.getInstance().getTimeInMillis();
                            break;
                        }
                        case MotionEvent.ACTION_UP: {
                            long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
                            if (clickDuration < MAX_CLICK_DURATION) {
                                onItemTouchListener.onButtonClick(v, getPosition());
                            }
                            break;
                        }
                        default:
                            break;
                    }
                    return true;
                }
            });

            gotoMap.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN: {
                            startClickTime = Calendar.getInstance().getTimeInMillis();
                            break;
                        }
                        case MotionEvent.ACTION_UP: {
                            long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
                            if (clickDuration < MAX_CLICK_DURATION) {
                                onItemTouchListener.onMapClick(v, getPosition());
                            }
                            break;
                        }
                        default:
                            break;
                    }
                    return true;
                }
            });
        }

    }

    @Override
    public SuggestRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.suggest_card_list, viewGroup, false);

        ViewHolder vh = new ViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {

        viewHolder.srcAddress.setText(items.get(i).SrcStation);
        viewHolder.dstAddress.setText(items.get(i).DstStation);
        viewHolder.suggestPrice.setText(items.get(i).PriceString + " " + "تومان");
        viewHolder.requests.setText("درخواست‌ها" + " : " + String.valueOf(items.get(i).PairPassengers) + " نفر");
        viewHolder.time.setTypeface(customFont);
        viewHolder.time.setText(items.get(i).TimingString);

    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
