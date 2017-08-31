package com.mibarim.driver.models;

import com.mibarim.driver.models.enums.DocStates;

/**
 * Created by Alireza on 8/30/2017.
 */

public class ImageDescription {
    private DocStates State;
    private String RejectionDescription;

    public DocStates getState() {
        return State;
    }

    public String getRejectionDescription() {
        return RejectionDescription;
    }
}
