package com.mibarim.driver.models;

import java.io.Serializable;

/**
 * Created by Alireza on 10/17/2017.
 */

public class PresentViewModel implements Serializable {

    public String PresentUrl;
    public String WebViewPageUrl;

    public String getPresentUrl() {
        return PresentUrl;
    }

    public void setPresentUrl(String presentUrl) {
        PresentUrl = presentUrl;
    }

    public String getWebViewPageUrl() {
        return WebViewPageUrl;
    }

    public void setWebViewPageUrl(String webViewPageUrl) {
        WebViewPageUrl = webViewPageUrl;
    }
}
