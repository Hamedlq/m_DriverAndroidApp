package com.mibarim.driver.models.Plus;

import java.io.Serializable;

/**
 * Created by Arya on 11/26/2017.
 */

public class SuggestModel implements Serializable {

    public long FilterId ;
    public long SrcStationId;
    public long DstStationId;
    public String SrcStation ;
    public String SrcStLat ;
    public String SrcStLng ;
    public String DstStation ;
    public String DstStLat ;
    public String DstStLng ;
    public int TimeHour ;
    public int TimeMinute ;
    public long Price;
    public String TimingString;
    public String PriceString;
    public int PairPassengers;
}
