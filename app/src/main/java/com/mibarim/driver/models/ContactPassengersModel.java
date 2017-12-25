package com.mibarim.driver.models;

import com.mibarim.driver.models.enums.PayingMethod;

import java.io.Serializable;

/**
 * Created by Alireza on 12/12/2017.
 */

public class ContactPassengersModel implements Serializable {
    String ImageId;
    String PassengerName;
    PayingMethod PayingMethod;
    String Fare;
    String PassengerMobile;
    String Gender;

    public String getGender() {
        return Gender;
    }

    public String getImageId() {
        return ImageId;
    }

    public String getPassengerName() {
        return PassengerName;
    }

    public PayingMethod getPayingMethod() {
        return PayingMethod;
    }

    public String getFare() {
        return Fare;
    }

    public String getPassengerMobile() {
        return PassengerMobile;
    }



}
