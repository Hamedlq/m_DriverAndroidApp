package com.mibarim.driver.ui.fragments.userInfoFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mibarim.driver.BootstrapApplication;
import com.mibarim.driver.R;
import com.mibarim.driver.models.UserInfoModel;
import com.mibarim.driver.ui.activities.MainActivity0;

import butterknife.ButterKnife;

/**
 * Created by Hamed on 3/5/2016.
 */
public class UserInfoFragment extends Fragment {

    private RelativeLayout layout;
    private Tracker mTracker;

    public UserInfoFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BootstrapApplication.component().inject(this);
        // Obtain the shared Tracker instance.
        BootstrapApplication application = (BootstrapApplication) getActivity().getApplication();
        mTracker = application.getDefaultTracker();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = (RelativeLayout) inflater.inflate(R.layout.fragment_user_info, container, false);
        return layout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ButterKnife.bind(this, getView());
/*        SharedPreferences prefs = getActivity().getSharedPreferences(
                "com.mibarim.driver", Context.MODE_PRIVATE);
        String Mobile = prefs.getString("UserMobile", "");*/
        UserInfoModel userInfo = null;
        if (getActivity() instanceof MainActivity0) {
            userInfo = ((MainActivity0) getActivity()).getUserInfo();
        }

        mTracker.setScreenName("ProfileFragment");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        mTracker.send(new HitBuilders.EventBuilder().setCategory("Fragment").setAction("ProfileFragment").build());
    }

}
