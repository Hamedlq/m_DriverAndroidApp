package com.mibarim.driver.adapters;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.mibarim.driver.R;
import com.mibarim.driver.models.Plus.StationRouteModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alireza on 7/17/2017.
 */

public class NewRoutesAdapter extends ArrayAdapter<StationRouteModel> {
    private RouteArrayFilter mFilter;
    private ArrayList<StationRouteModel> mOriginalValues;
    private List<StationRouteModel> mObjects;
    private final Object mLock = new Object();
    private final LayoutInflater mInflater;
    private final int mResource;

    public NewRoutesAdapter(@NonNull Context context, int resource, ArrayList<StationRouteModel> list) {
        super(context, resource, list);
        mInflater = LayoutInflater.from(context);
        mObjects = list;
        mResource = resource;
    }
/*
    public Filter getSrcFilter() {
        if (mFilter == null) {
            mFilter = new RouteArrayFilter(true);
        }
        return mFilter;
        //return super.getFilter();
    }*/



    public Filter getFilter(String src, String dst) {
        if (mFilter != null) {
            mFilter = null;
        }
        mFilter = new RouteArrayFilter(src, dst);
        return mFilter;
    }
/*
    @NonNull
    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new RouteArrayFilter();
        }
        return mFilter;
        //return super.getFilter();
    }*/

/*    public Filter getDstFilter() {
        if (mFilter == null) {
            mFilter = new RouteArrayFilter();
        }
        return mFilter;
//        return super.getFilter();

    }\*/


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final View view;
        final TextView srctext;
        final TextView dsttext;
        final TextView prtext;
        final StationRouteModel item;

        if (convertView == null) {
            view = mInflater.inflate(mResource, parent, false);
        } else {
            view = convertView;
        }

        try {
            item = getItem(position);

            srctext = (TextView) view.findViewById(R.id.src_address);
            dsttext = (TextView) view.findViewById(R.id.dst_address);
            prtext = (TextView) view.findViewById(R.id.price);
            srctext.setText(item.SrcStAdd);
            dsttext.setText(item.DstStAdd);
            prtext.setText(item.StRoutePrice);

        } catch (ClassCastException e) {

        }
        return view;
        //return super.getView(position, convertView, parent);
    }


    @Override
    public int getPosition(@Nullable StationRouteModel item) {
        return super.getPosition(item);
    }

    @Override
    public int getCount() {
        return super.getCount();
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
                        dstText = dstText.replace('\u200C',' ').replaceAll(" ","").replaceAll("،","");





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
                        srcText = srcText.replace('\u200C',' ').replaceAll(" ","").replaceAll("،","");;

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
                        srcText = srcText.replace('\u200C',' ').replaceAll(" ","").replaceAll("،","");

                        String dstText = value.DstStAdd.toString().toLowerCase();
                        dstText = dstText.replace('\u200C',' ').replaceAll(" ","").replaceAll("،","");

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

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            //noinspection unchecked
            mObjects = (List<StationRouteModel>) results.values;
            clear();
            addAll(mObjects);
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
}