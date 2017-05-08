package com.mibarim.driver;

import android.accounts.AccountsException;
import android.app.Activity;

import com.mibarim.driver.services.AuthenticateService;

import java.io.IOException;

public interface BootstrapServiceProvider {
    AuthenticateService getService(Activity activity) throws IOException, AccountsException;

    String getAuthToken(Activity activity) throws IOException, AccountsException;

    void invalidateAuthToken();
}
