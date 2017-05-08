package com.mibarim.driver.models;


import com.mibarim.driver.models.enums.EventTypes;

import java.io.Serializable;

public class EventResponse implements Serializable {

    public long EventId;
    public EventTypes EventType;
    public String Conductor;
    public String Name;
    public String Address;
    public String Latitude;
    public String Longitude;
    public String StartTimeString;
    public String EndTimeString;
    public String Description;
    public String ExternalLink;


}
