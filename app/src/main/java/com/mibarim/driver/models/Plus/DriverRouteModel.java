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
    public String SrcAddress;
    public String SrcLat;
    public String SrcLng;
    public String DstAddress;
    public String DstLat;
    public String DstLng;
    public short EmptySeats;
    public boolean HasTrip;
}
