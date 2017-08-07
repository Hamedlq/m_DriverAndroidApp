
package com.mibarim.driver.services;

import com.mibarim.driver.core.UserService;
import com.mibarim.driver.models.ApiResponse;
import com.mibarim.driver.models.TokenResponse;

import retrofit.RestAdapter;

/**
 * Bootstrap API service
 */
public class
        AuthenticateService {

    private RestAdapter restAdapter;

    /**
     * Create bootstrap service
     * Default CTOR
     */
    public AuthenticateService() {
    }

    /**
     * Create bootstrap service
     *
     * @param restAdapter The RestAdapter that allows HTTP Communication.
     */
    public AuthenticateService(RestAdapter restAdapter) {
        this.restAdapter = restAdapter;
    }

    private UserService getUserService() {
        return getRestAdapter().create(UserService.class);
    }

    private RestAdapter getRestAdapter() {
        return restAdapter;
    }

    public TokenResponse authenticate(String mobile, String password,String grantType,String responseType) {
        return getUserService().authenticate(mobile, password, grantType, responseType);
    }

    public boolean confirmMobile(String authToken,String mobile) {
        return getUserService().validateMobile("Bearer " + authToken, mobile);
    }

    public ApiResponse confirmMobileSms(String authToken, String mobile, String vCode) {
        return getUserService().validateMobileSms("Bearer " + authToken, mobile, vCode);
    }

    public boolean sendValidateSms(String authToken,String mobile,int count) {
        return getUserService().sendValidateSms("Bearer " + authToken, mobile,count);
    }

}