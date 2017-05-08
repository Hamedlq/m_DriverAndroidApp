package com.mibarim.driver.ui.fragments.userInfoFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.mibarim.driver.BootstrapApplication;
import com.mibarim.driver.R;
import com.mibarim.driver.models.InviteModel;
import com.mibarim.driver.ui.activities.UserInfoDetailActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Hamed on 3/5/2016.
 */
public class InviteFragment extends Fragment {

    private RelativeLayout layout;
    @Bind(R.id.share_btn)
    protected BootstrapButton share_btn;
    @Bind(R.id.inviteCode)
    protected TextView inviteCode;

    public InviteFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BootstrapApplication.component().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = (RelativeLayout) inflater.inflate(R.layout.fragment_invite, container, false);

        return layout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ButterKnife.bind(this, getView());
        reloadInvite();
        ((UserInfoDetailActivity) getActivity()).getInviteFromServer();
    }

    public void reloadInvite() {
        final InviteModel inviteModel=((UserInfoDetailActivity) getActivity()).getInviteCode();
        inviteCode.setText(inviteModel.InviteCode);
        share_btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    ((UserInfoDetailActivity) getActivity()).ShareInvite(inviteModel.InviteLink);
                    return true;
                }
                return false;
            }
        });
    }
}
