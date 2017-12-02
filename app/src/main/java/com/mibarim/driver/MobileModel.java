package com.mibarim.driver;

import java.io.Serializable;

/**
 * Created by mohammad hossein on 30/11/2017.
 */

public class MobileModel implements Serializable {
    String mobile;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
