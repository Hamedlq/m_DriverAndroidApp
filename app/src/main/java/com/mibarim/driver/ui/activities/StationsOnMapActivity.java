package com.mibarim.driver.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mibarim.driver.R;
import com.mibarim.driver.core.Constants;
import com.mibarim.driver.models.ApiResponse;
import com.mibarim.driver.models.Plus.SubStationModel;
import com.mibarim.driver.models.StationModel;
import com.mibarim.driver.ui.fragments.StationsOnMapFragment;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import static com.mibarim.driver.core.Constants.GlobalConstants.ALL_SUBSTATIONS_INTENT_TAG;
import static com.mibarim.driver.core.Constants.GlobalConstants.MAIN_STATIONS_INTENT_TAG;

/**
 * Created by Alireza on 10/7/2017.
 */

public class StationsOnMapActivity extends AppCompatActivity {


    JSONArray jsonArray;
    public List<StationModel> stationList;
    public List<SubStationModel> substationList;

    Button chooseButton;

    long stationId;

    long originMainStId;
    long destMainStId;


    int state;
    ApiResponse apiResponse;

    public int getState() {
        return state;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stations_on_map);

        stationList = new ArrayList<>();
        substationList = new ArrayList<>();

        state = getIntent().getIntExtra(Constants.GlobalConstants.STATE_OF_WHETHER_CHOOSING_ORIGIN_OR_DESTINATION, -1);
        originMainStId = getIntent().getLongExtra(Constants.GlobalConstants.ORIGIN_MAIN_STATION_ID_INTENT_TAG,-4);
        destMainStId = getIntent().getLongExtra(Constants.GlobalConstants.DESTINATION_MAIN_STATION_ID_INTENT_TAG,-5);


        chooseButton = (Button) findViewById(R.id.choose_button);
        setChooseButtonCondition();


        Gson gson = new GsonBuilder().create();

        if (state == 0) {
            apiResponse = (ApiResponse) getIntent().getExtras().getSerializable(ALL_SUBSTATIONS_INTENT_TAG);


            if (apiResponse != null) {
                for (String json : apiResponse.Messages) {
                    substationList.add(gson.fromJson(json, SubStationModel.class));
                }
            }
        }

        if (state == 1) {
            apiResponse = (ApiResponse) getIntent().getExtras().getSerializable(MAIN_STATIONS_INTENT_TAG);
            for (String json : apiResponse.Messages) {
                stationList.add(gson.fromJson(json, StationModel.class));
            }
        }


        chooseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (stationId > 0) {
                    if (originMainStId != destMainStId) {

                        if (state == 0){
                            Toast.makeText(StationsOnMapActivity.this, R.string.origin_selected, Toast.LENGTH_SHORT).show();
                        }

                        if (state == 1){
                            Toast.makeText(StationsOnMapActivity.this, R.string.destination_selected, Toast.LENGTH_SHORT).show();
                        }

                        Intent intent = getIntent();
                        intent.putExtra("OrigOrDestStId", stationId);
                        intent.putExtra("StateOfOrigOrDest", state);
                        setResult(RESULT_OK, intent);
                        finish();
                    }

                    else{
                        Toast.makeText(StationsOnMapActivity.this,"مبدا و مقصد نمی تواند یکسان باشد",Toast.LENGTH_LONG).show();
                    }

                }

                else{
                    Toast.makeText(StationsOnMapActivity.this,"لطفا ایستگاه مورد نظر خود را انتخاب کنید",Toast.LENGTH_LONG).show();
                }

            }
        });

        /*try {
            jsonArray = new JSONArray(getIntent().getStringExtra("MainStationsIntentTag"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Gson gson = new GsonBuilder().create();

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                stationList.add(jsonArray.getString(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }*/


        FragmentManager fragmentManager = getSupportFragmentManager();

        fragmentManager
                .beginTransaction()
                .add(R.id.stations_on_map_fragment_holder, new StationsOnMapFragment(), "StationsOnMapFragmentTag")
                .commitAllowingStateLoss();


    }

    public List<StationModel> getMainStations() {
        return stationList;
    }

    public List<SubStationModel> getSubStations() {
        return substationList;
    }


    public void setStationId(long StId) {
        stationId = StId;
        destMainStId = StId;
    }


    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    public void setChooseButtonCondition() {
        if (state == 0) {
            chooseButton.setBackgroundColor(getResources().getColor(R.color.defining_route_origin_color));
            chooseButton.setText("انتخاب مبدا");

        }

        if (state == 1) {
            chooseButton.setBackgroundColor(getResources().getColor(R.color.defining_route_destination_color));
            chooseButton.setText("انتخاب مقصد");
        }


    }

    public void setOriginMainStationId(long origSubStId){
        originMainStId = origSubStId;
    }

}
