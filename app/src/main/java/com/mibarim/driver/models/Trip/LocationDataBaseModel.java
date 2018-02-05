package com.mibarim.driver.models.Trip;

import java.io.Serializable;

/**
 * Created by Arya on 1/24/2018.
 */

public class LocationDataBaseModel implements Serializable {

    private int TripState;
    private long TripId;
    private String StLat, StLng;

    public LocationDataBaseModel(String StLat, String StLng, long tripId, int tripState) {
        this.StLat = StLat;
        this.StLng = StLng;
        this.TripId = tripId;
        this.TripState = tripState;
    }
}
