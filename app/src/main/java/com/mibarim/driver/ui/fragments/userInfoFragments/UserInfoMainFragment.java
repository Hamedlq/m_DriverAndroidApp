package com.mibarim.driver.ui.fragments.userInfoFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
public class UserInfoMainFragment extends Fragment {

    private RelativeLayout layout;
    private Tracker mTracker;

    public UserInfoMainFragment() {
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
        layout = (RelativeLayout) inflater.inflate(R.layout.fragment_user_main_info, container, false);
        initScreen();
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

        mTracker.setScreenName("UserInfoMainFragment");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        mTracker.send(new HitBuilders.EventBuilder().setCategory("Fragment").setAction("UserInfoMainFragment").build());
    }


    private void initScreen() {
        final FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

        /*fragmentManager.beginTransaction()
                .replace(R.id.user_person_fragment, new UserPersonFragment())
                .commit();
        fragmentManager.beginTransaction()
                .replace(R.id.license_fragment, new LicenseFragment())
                .commit();
        fragmentManager.beginTransaction()
                .replace(R.id.car_info_fragment, new CarInfoFragment())
                .commit();
        fragmentManager.beginTransaction()
                .replace(R.id.bank_info_fragment, new BankFragment())
                .commit();*/
        fragmentManager.beginTransaction()
                .replace(R.id.user_info_card_fragment, new UserInfoCardFragment())
                .commit();
    }

    public UserInfoModel getNewUserInfo() {
        UserInfoModel newModel = new UserInfoModel();
        final FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.user_person_fragment);
        UserInfoModel model = ((UserPersonFragment) fragment).getUserInfo();
        newModel.Gender = model.Gender;
        newModel.Name = model.Name;
        newModel.Family = model.Family;
        newModel.Email = model.Email;
        newModel.Code = model.Code;
        newModel.NationalCode = model.NationalCode;
        Fragment carfragment = fragmentManager.findFragmentById(R.id.car_info_fragment);
        model = ((CarInfoFragment) carfragment).getCarInfo();
        newModel.CarType = model.CarType;
        newModel.CarPlateNo = model.CarPlateNo;
        newModel.CarColor = model.CarColor;
        Fragment bankfragment = fragmentManager.findFragmentById(R.id.bank_info_fragment);
        model = ((BankFragment) bankfragment).getBankInfo();
        newModel.BankShaba = model.BankShaba;
        newModel.BankName = model.BankName;
        newModel.BankAccountNo = model.BankAccountNo;
        return newModel;
    }

/*    public void setImage(ImageResponse imageResponse) {
        switch (imageResponse.ImageType) {
            case UserPic:
                ((UserInfoActivity) getActivity()).setHeadImage(imageResponse);
                break;
        }
    }*/
}
