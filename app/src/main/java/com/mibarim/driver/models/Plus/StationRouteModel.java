package com.mibarim.driver.models.Plus;

import java.io.Serializable;

/**
 * Created by Hamed on 4/6/2016.
 */
public class StationRouteModel implements Serializable {
    public long StRouteId ;
    public long SrcStId;
    public String SrcStAdd;
    public String SrcStLat ;
    public String SrcStLng ;
    public long DstStId ;
    public String DstStAdd ;
    public String DstStLat ;
    public String DstStLng ;
    public String StRoutePrice;
    public String StRouteDuration;
}
