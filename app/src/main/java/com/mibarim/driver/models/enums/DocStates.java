package com.mibarim.driver.models.enums;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Alireza on 8/27/2017.
 */

public enum DocStates {


    @SerializedName("1")
    NotSent("NotSent", 1),
    @SerializedName("2")
    UnderChecking("UnderChecking", 2),
    @SerializedName("3")
    Accepted("Accepted", 3),
    @SerializedName("4")
    Rejected("Rejected", 4);




    private String stringValue;
    private int intValue;

    private DocStates(String toString, int value) {
        stringValue = toString;
        intValue = value;
    }

    @Override
    public String toString() {
        return stringValue;
    }


}
