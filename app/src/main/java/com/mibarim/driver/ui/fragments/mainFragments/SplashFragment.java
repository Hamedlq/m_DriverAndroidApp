package com.mibarim.driver.ui.fragments.mainFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mibarim.driver.BootstrapApplication;
import com.mibarim.driver.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Hamed on 3/4/2016.
 */
public class SplashFragment extends Fragment {

    @Bind(R.id.retry_btn)
    protected TextView retry_btn;
    @Bind(R.id.pb_loading)
    protected ProgressBar pb_loading;

    public SplashFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BootstrapApplication.component().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.initial_splash, container, false);
        return layout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ButterKnife.bind(this, getView());
        retry_btn.setVisibility(View.GONE);
        pb_loading.setVisibility(View.VISIBLE);
    }
}
