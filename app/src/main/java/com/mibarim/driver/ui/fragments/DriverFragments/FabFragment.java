package com.mibarim.driver.ui.fragments.DriverFragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mibarim.driver.BootstrapApplication;
import com.mibarim.driver.R;
import com.mibarim.driver.ui.activities.DriveActivity;
import com.mibarim.driver.ui.activities.MainActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Hamed on 3/5/2016.
 */
public class FabFragment extends Fragment  {

    private RelativeLayout layout;

    @Bind(R.id.fab)
    protected FloatingActionButton fab;

    public FabFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BootstrapApplication.component().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = (RelativeLayout) inflater.inflate(R.layout.fragment_fab, container, false);
        return layout;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ButterKnife.bind(this, layout);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).gotoRouteLists();
            }
        });
    }



}
