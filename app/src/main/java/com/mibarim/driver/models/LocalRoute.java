package com.mibarim.driver.models;

import com.mibarim.driver.models.Address.LocationPoint;
import com.mibarim.driver.models.Address.PathPoint;
import com.mibarim.driver.models.enums.LocalRouteTypes;

import java.io.Serializable;

/**
 * Created by Hamed on 7/28/2016.
 */
public class LocalRoute implements Serializable {
    public String RouteUId;
    public LocalRouteTypes LocalRouteType;
    public LocationPoint SrcPoint ;
    public LocationPoint DstPoint ;
    public String RouteStartTime ;
    public PathPoint PathRoute;
    public String Name;
    public String Family;

}
