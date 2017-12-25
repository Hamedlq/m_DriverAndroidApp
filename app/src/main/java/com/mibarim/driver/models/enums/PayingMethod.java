package com.mibarim.driver.models.enums;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Alireza on 12/13/2017.
 */

public enum PayingMethod {

    @SerializedName("1")
    InCash("InCash", 1),
    @SerializedName("2")
    OnLinePayed("OnLinePayed", 2);

    private String stringValue;
    private int intValue;

    PayingMethod(String toString, int value) {

        stringValue = toString;
        intValue = value;
    }

    @Override
    public String toString() {
        return stringValue;
    }

    public int toInt(){
        return intValue;
    }


}
