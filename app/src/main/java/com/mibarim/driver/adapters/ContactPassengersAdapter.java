package com.mibarim.driver.adapters;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapCircleThumbnail;
import com.mibarim.driver.R;
//import com.mibarim.driver.interfaces.ContactPassengersOnItemClicked;
import com.mibarim.driver.models.ContactPassengersModel;
import com.mibarim.driver.models.enums.PayingMethod;
import com.mibarim.driver.ui.activities.ContactPassengersActivity;

import java.util.List;

/**
 * Created by Alireza on 12/11/2017.
 */

public class ContactPassengersAdapter extends RecyclerView.Adapter<ContactPassengersAdapter.ContactsPassengerViewHolder> {

    List<ContactPassengersModel> items;
    ContactPassengersActivity.ContactPassengersOnItemClicked onItemClicked;
    List<Bitmap> images;

//    ContactPassengersOnItemClicked onItemClicked;


    public ContactPassengersAdapter(List<ContactPassengersModel> contactPassengersModelList, ContactPassengersActivity.ContactPassengersOnItemClicked contactPassengersOnItemClicked, List<Bitmap> bitmapList) {//ContactPassengersOnItemClicked onItemClicked) {
//        this.onItemClicked = onItemClicked;
        items = contactPassengersModelList;
        onItemClicked = contactPassengersOnItemClicked;
        images = bitmapList;
    }

    public class ContactsPassengerViewHolder extends RecyclerView.ViewHolder {

        ImageView passengerImageview;
        TextView passengerName;
        TextView payingMethod;
        TextView fare;
        TextView callPassenger;
        BootstrapCircleThumbnail passengerCircleImage;

        public ContactsPassengerViewHolder(View itemView) {
            super(itemView);

            passengerImageview = (ImageView) itemView.findViewById(R.id.passenger_imageview);
            passengerCircleImage = (BootstrapCircleThumbnail) itemView.findViewById(R.id.passenger_circle_image);
            passengerName = (TextView) itemView.findViewById(R.id.passenger_name);
            payingMethod = (TextView) itemView.findViewById(R.id.paying_method);
            fare = (TextView) itemView.findViewById(R.id.fare);
            callPassenger = (TextView) itemView.findViewById(R.id.call_passenger);


            callPassenger.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getPosition();
                    String tel = items.get(position).getPassengerMobile();
                    onItemClicked.onContactPassengersClicked(v, position, tel);

                }
            });


        }


    }


    @Override
    public ContactsPassengerViewHolder onCreateViewHolder(ViewGroup parent, int i) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_passengers_item, parent, false);

        ContactsPassengerViewHolder viewHolder = new ContactsPassengerViewHolder(v);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(ContactsPassengerViewHolder contactsPassengerViewHolder, int position) {

        contactsPassengerViewHolder.passengerName.setText(items.get(position).getPassengerName());

        PayingMethod i = items.get(position).getPayingMethod().InCash;
        String payMethod = items.get(position).getPayingMethod().toString();

        if (payMethod.equals("InCash")) {
            contactsPassengerViewHolder.payingMethod.setText(R.string.in_cash);
        } else
            contactsPassengerViewHolder.payingMethod.setText(R.string.on_line_payed);
//        contactsPassengerViewHolder.payingMethod.setText(items.get(position).getPayingMethod().InCash);

        if (items.get(position).getFare() != null) {
            contactsPassengerViewHolder.fare.setText(items.get(position).getFare() + " تومان");
        }


        if (images != null && images.size() != 0) {
            if (images.get(position) != null) {
                contactsPassengerViewHolder.passengerCircleImage.setImageBitmap(images.get(position));
            }
        }


    }

    @Override
    public int getItemCount() {
        return items.size();
    }


}
