package com.mibarim.driver.ui.fragments.messagingFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.mibarim.driver.BootstrapApplication;
import com.mibarim.driver.R;
import com.mibarim.driver.models.ContactModel;
import com.mibarim.driver.ui.activities.MessagingActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Hamed on 3/4/2016.
 */
public class ToggleContactTripFragment extends Fragment {


    @Bind(R.id.switch_for_trip_state)
    protected Switch switch_for_trip_state;
    @Bind(R.id.switch_text)
    protected TextView switch_text;
    @Bind(R.id.help)
    protected AwesomeTextView help;

    public ToggleContactTripFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BootstrapApplication.component().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.fragment_toggle_contact_trip, container, false);

        return layout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ButterKnife.bind(this, getView());
        ContactModel contactModel=((MessagingActivity)getActivity()).getContactModel();
        if(contactModel.IsDriver==1 && contactModel.IsRideAccepted==1){
            setSwitchState(true);
        }
        if(contactModel.IsDriver==0 && contactModel.IsPassengerAccepted==1){
            setSwitchState(true);
        }
        help.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    showHelp();
                        return true;
                }
                return false;
            }
        });
        switch_text.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    showHelp();
                    return true;
                }
                return false;
            }
        });
        switch_for_trip_state.setOnCheckedChangeListener(mListener);
    }
    CompoundButton.OnCheckedChangeListener mListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            ((MessagingActivity)getActivity()).toggleTripState(isChecked);
        }
    };

    private void showHelp() {
        ((MessagingActivity)getActivity()).showHelp();
    }

    public void setSwitchState(boolean state) {
        switch_for_trip_state.setOnCheckedChangeListener(null);
        switch_for_trip_state.setChecked(state);
        switch_for_trip_state.setOnCheckedChangeListener(mListener);
    }
}
