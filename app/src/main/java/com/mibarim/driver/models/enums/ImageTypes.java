package com.mibarim.driver.models.enums;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Hamed on 3/8/2016.
 */
public enum ImageTypes {

    @SerializedName("1")
    UserPic("UserPic", 1),
    @SerializedName("2")
    UserNationalCard("UserNationalCard", 2),
    @SerializedName("3")
    LicensePic("LicensePic", 3),
    @SerializedName("4")
    CarCardPic("CarCardPic", 4),
    @SerializedName("5")
    CarBckPic("CarBckPic", 5),
    @SerializedName("6")
    BankPic("BankPic", 6),
    @SerializedName("8")
    CarImage("CarImage", 8);

    private String stringValue;
    private int intValue;

    private ImageTypes(String toString, int value) {
        stringValue = toString;
        intValue = value;
    }

    @Override
    public String toString() {
        return stringValue;
    }

    public int toInt() {
        return intValue;
    }
}


