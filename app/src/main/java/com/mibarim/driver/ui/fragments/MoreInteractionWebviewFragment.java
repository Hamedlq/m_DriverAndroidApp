package com.mibarim.driver.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.mibarim.driver.R;
import com.mibarim.driver.core.Constants;
import com.mibarim.driver.ui.activities.MainActivity;
import com.mibarim.driver.ui.activities.WebViewActivity;

/**
 * Created by Alireza on 10/12/2017.
 */

public class MoreInteractionWebviewFragment extends Fragment {

    ImageView closeButton;
    WebView presentWebview;
    String presentURL;
    String webviewPageURL;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.more_interaction_webview_fragment, container, false);

        closeButton = (ImageView) view.findViewById(R.id.close_button);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((MainActivity) getActivity()).removeMoreInteractionWebviewFragment();

            }
        });


        presentWebview = (WebView) view.findViewById(R.id.more_interaction_webview);

        presentURL = ((MainActivity) getActivity()).getPresentWebview();

        presentWebview.loadUrl(presentURL);

        webviewPageURL = ((MainActivity) getActivity()).getWebviewPageURL();



        presentWebview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Intent intent = new Intent(getActivity(), WebViewActivity.class);
                    intent.putExtra("URL", webviewPageURL);
                    startActivity(intent);
                }
                return false;
            }
        });


        return view;
    }


}
