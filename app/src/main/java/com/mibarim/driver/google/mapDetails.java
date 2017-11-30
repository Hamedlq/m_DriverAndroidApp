package com.mibarim.driver.google;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mibarim.driver.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mohammad hossein on 23/11/2017.
 */

public class mapDetails extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int ERROR_DIALOG_REQUEST = 9001;
    GoogleMap mMap;
    private double srcLat,srcLng,dstLat,dstLng;
    ImageView backInMap;

    public double getSrcLat() {
        return srcLat;
    }

    public void setSrcLat(double srcLat) {
        this.srcLat = srcLat;
    }

    public double getSrcLng() {
        return srcLng;
    }

    public void setSrcLng(double srcLng) {
        this.srcLng = srcLng;
    }

    public double getDstLat() {
        return dstLat;
    }

    public void setDstLat(double dstLat) {
        this.dstLat = dstLat;
    }

    public double getDstLng() {
        return dstLng;
    }

    public void setDstLng(double dstLng) {
        this.dstLng = dstLng;
    }

    List<Marker> markers = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.map_details);

        backInMap = (ImageView)findViewById(R.id.backInMap);

        setParams();
        initMap();

        backInMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void setParams(){
        Bundle bundle = getIntent().getExtras();
        setSrcLat(bundle.getDouble("SRC_LAT"));
        setSrcLng(bundle.getDouble("SRC_LNG"));
        setDstLat(bundle.getDouble("DST_LAT"));
        setDstLng(bundle.getDouble("DST_LNG"));

    }

    private void initMap() {
        if (mMap == null) {
            SupportMapFragment mapFragment =
                    (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

            mapFragment.getMapAsync(this);

        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {


        mMap = googleMap;

        if (mMap != null) {

//            //info window
//            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
//                @Override
//                public View getInfoWindow(Marker marker) {
//                    return null;
//                }
//
//                @Override
//                public View getInfoContents(Marker marker) {
//                    return null;
//                }
//            });

            MarkerOptions options = new MarkerOptions()
                    .position(new LatLng(getSrcLat(), getSrcLng()))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.mabda));
            MarkerOptions options1 = new MarkerOptions()
                    .position(new LatLng(getDstLat(), getDstLng()))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.maghsad));


            markers.add(mMap.addMarker(options));
            markers.add(mMap.addMarker(options1));

            calculateBound();

        } else {
            Toast.makeText(getApplicationContext(), "mmap is null", Toast.LENGTH_SHORT).show();
        }

    }

    public void calculateBound() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (int i = 0; i <= 1; i++) {
            Marker marker = markers.get(i);
            builder.include(marker.getPosition());
        }
        LatLngBounds bounds = builder.build();
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.20); // offset from edges of the map 10% of screen
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
        mMap.moveCamera(cu);
    }

    private void goToLocation(double lat, double lng, float zoom) {
        LatLng latLng = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
        mMap.moveCamera(update);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void setLocations(){

    }


}
