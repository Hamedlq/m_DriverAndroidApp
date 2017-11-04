package com.mibarim.driver.models.Plus;

import java.io.Serializable;

/**
 * Created by Hamed on 4/6/2016.
 */
public class DriverRouteModel implements Serializable {
    public long  DriverRouteId;
    public String  TimingString;
    public int  TimingHour;
    public int  TimingMin;
    public String  PricingString;
    public String  CarString;
    public String SrcMainAddress;
    public String SrcAddress;
    public String SrcLink;
    public String SrcLat;
    public String SrcLng;
    public String DstAddress;
    public String DstLink;
    public String DstLat;
    public String DstLng;
    public short FilledSeats;
    public short CarSeats;
    public boolean HasTrip;
    public int TripState;
    public long TripId;
}
