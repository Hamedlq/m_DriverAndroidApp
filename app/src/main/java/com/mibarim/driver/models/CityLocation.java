package com.mibarim.driver.models;

import com.mibarim.driver.models.Address.LocationPoint;
import com.mibarim.driver.models.enums.CityLocationTypes;

import java.io.Serializable;

/**
 * Created by Hamed on 7/28/2016.
 */
public class CityLocation implements Serializable {
    public CityLocationTypes CityLocationType;
    public String ShortName;
    public String FullName;
    public LocationPoint CityLocationPoint;
}
