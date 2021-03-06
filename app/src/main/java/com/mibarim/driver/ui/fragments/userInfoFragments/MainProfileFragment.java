package com.mibarim.driver.ui.fragments.userInfoFragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mibarim.driver.BootstrapApplication;
import com.mibarim.driver.R;
import com.mibarim.driver.models.ScoreModel;
import com.mibarim.driver.models.UserInfoModel;

import butterknife.ButterKnife;

/**
 * Created by Hamed on 3/5/2016.
 */
public class MainProfileFragment extends Fragment {

    private LinearLayout layout;
    private Tracker mTracker;
    private Context context;
    //private boolean isViewShown = false;

    /*@Bind(R.id.anim_toolbar)
    protected Toolbar toolbar;*/

/*    @Bind(R.id.user_image)
    protected BootstrapCircleThumbnail user_image;
    @Bind(R.id.fa_edit)
    protected AwesomeTextView fa_edit;
    @Bind(R.id.credit_money)
    protected TextView credit_money;
    @Bind(R.id.score_tree)
    protected TextView score_tree;
    @Bind(R.id.company_image)
    protected BootstrapCircleThumbnail company_image;
    @Bind(R.id.company_name)
    protected TextView company_name;
    @Bind(R.id.charge_account)
    protected BootstrapButton charge_account;

    @Bind(R.id.name_family)
    protected TextView name_family;*/

    public MainProfileFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BootstrapApplication.component().inject(this);
        // Obtain the shared Tracker instance.
        BootstrapApplication application = (BootstrapApplication) getActivity().getApplication();
        mTracker = application.getDefaultTracker();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = (LinearLayout) inflater.inflate(R.layout.fragment_profile_2, container, false);
        initScreen();
        return layout;
    }


    private void initScreen() {
        final FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        /*fragmentManager.beginTransaction()
                .replace(R.id.main_container, new UserInfoMainFragment())
                .commitAllowingStateLoss();*/
        /*fragmentManager.beginTransaction()
                .replace(R.id.main_container, new ProfileFragment())
                .commit();*/
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity();
        ButterKnife.bind(this, getView());

        UserInfoModel userInfo = null;
        /*if (getActivity() instanceof MainActivity0) {
            userInfo = ((MainActivity0) getActivity()).getUserInfo();
        }*/
        if (userInfo != null && userInfo.Gender != null) {
            //user_image.setImageBitmap(((MainActivity0) getActivity()).getImageById(userInfo.UserImageId,R.mipmap.ic_camera));
        }

//        ((MainActivity0)getActivity()).setSupportActionBar(toolbar);

/*
        ActionBar actionBar = ((MainActivity0)getActivity()).getSupportActionBar();
        if (actionBar != null) {
            //actionBar.setHomeAsUpIndicator(R.mipmap.ic_menu_forward);
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setHomeButtonEnabled(false);
            actionBar.setTitle("salam");
        }
*/

/*        UserInfoModel userInfo = null;
        ((MainActivity0) getActivity()).getUserScore();
        if (getActivity() instanceof MainActivity0) {
            userInfo = ((MainActivity0) getActivity()).getUserInfo();
            ((MainActivity0) context).hideActionBar();
        }
        if (userInfo != null && userInfo.Gender != null) {
            name_family.setText(userInfo.Name + " " + userInfo.Family);
            user_image.setImageBitmap(((MainActivity0) getActivity()).getImageById(userInfo.UserImageId));
            *//*if (userInfo.Base64UserPic != null && !userInfo.Base64UserPic.equals("")) {
                byte[] decodedString = Base64.decode(userInfo.Base64UserPic, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                user_image.setImageBitmap(decodedByte);
            }*//*
        }
        company_image.setVisibility(View.GONE);
        company_name.setVisibility(View.GONE);
        if (userInfo != null && userInfo.CompanyName != null) {
            company_image.setVisibility(View.VISIBLE);
            company_name.setVisibility(View.VISIBLE);
            company_name.setText(userInfo.CompanyName);
            ((MainActivity0) context).getCompanyImage(userInfo.CompanyImageId);
        }
        fa_edit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    ((MainActivity0) getActivity()).goToUserActivity();
                    return true;
                }
                return false;
            }
        });
        charge_account.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    ((MainActivity0) getActivity()).gotoMibarimWebsite();
                    return true;
                }
                return false;
            }
        });*/
        mTracker.setScreenName("MainProfileFragment");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        mTracker.send(new HitBuilders.EventBuilder().setCategory("Fragment").setAction("MainProfileFragment").build());
    }
/*
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (context != null && context instanceof MainActivity0) {
            if (isVisibleToUser) {
                ((MainActivity0) context).profileVisible();
                ((MainActivity0) context).hideActionBar();
            } else {
                ((MainActivity0) context).profileInVisible();
                ((MainActivity0) context).showActionBar();
            }
        }
    }
*/

    public void SetScores(ScoreModel scoreModel) {
        /*credit_money.setText(scoreModel.CreditMoney);
        score_tree.setText(scoreModel.Score);*/
    }

    public void reloadUserImage() {
        /*UserInfoModel userInfo = ((MainActivity0) getActivity()).getUserInfo();
        if (userInfo != null && userInfo.Gender != null) {
            user_image.setImageBitmap(((MainActivity0) getActivity()).getImageById(userInfo.UserImageId));
        }*/
    }

    public void setCompanyImage(Bitmap decodedByte) {
        //company_image.setImageBitmap(decodedByte);
    }
}
