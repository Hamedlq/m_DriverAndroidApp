package com.mibarim.driver.models;


import android.graphics.drawable.Drawable;

import com.mibarim.driver.models.enums.UserCardTypes;

import java.io.Serializable;

public class UserCardModel implements Serializable {

    public String CardTitle;
    public Drawable CardIcon;
    public String CardDiscount;
    public UserCardTypes CardType;
}
