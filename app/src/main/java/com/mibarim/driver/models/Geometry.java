package com.mibarim.driver.models;


import com.mibarim.driver.models.Address.Location;

import java.io.Serializable;

/**
 * Created by Hamed on 3/15/2016.
 */
public class Geometry implements Serializable {
    public Location location;
    public String location_type;
}
