package com.mibarim.driver.models.Plus;

import com.mibarim.driver.models.enums.TripStates;

import java.io.Serializable;

/**
 * Created by Hamed on 4/6/2016.
 */
public class DriverTripModel implements Serializable {
    public long DriverRouteId;
    public long TripId;
    public String StAddress;
    public String StLink;
    public String StLat;
    public String StLng;
    public int FilledSeats;
    public int TripState;
}
