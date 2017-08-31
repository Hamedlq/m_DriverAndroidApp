package com.mibarim.driver.services;

import com.mibarim.driver.RestInterfaces.SaveUserImageService;
import com.mibarim.driver.core.Constants;
import com.mibarim.driver.core.RestAdapterImageInterceptor;
import com.mibarim.driver.core.RestErrorHandler;
import com.mibarim.driver.models.ApiResponse;
import com.mibarim.driver.util.DynamicJsonConverter;

import retrofit.RestAdapter;
import retrofit.mime.TypedFile;

//import com.squareup.okhttp.RequestBody;

/**
 * Created by Hamed on 3/10/2016.
 */
public class UserImageService {

    private RestAdapter restAdapter;

    public UserImageService(RestErrorHandler restErrorHandler) {
        this.restAdapter = new RestAdapter.Builder()
                .setEndpoint(Constants.Http.URL_BASE)
                .setErrorHandler(restErrorHandler)
                .setRequestInterceptor(new RestAdapterImageInterceptor())
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setConverter(new DynamicJsonConverter())
                .build();
    }

    public RestAdapter getRestAdapter() {
        return restAdapter;
    }

    private SaveUserImageService getService() {
        return getRestAdapter().create(SaveUserImageService.class);
    }

    public ApiResponse SaveUserImage(String authToken, TypedFile pic) {
        ApiResponse res = getService().saveUserImage("Bearer " + authToken, pic);
        return res;
    }
    public ApiResponse SaveLicenseImage(String authToken, TypedFile pic) {
        ApiResponse res = getService().saveLicenseImage("Bearer " + authToken, pic);
        return res;
    }
    public ApiResponse SaveCarImage(String authToken, TypedFile pic) {
        ApiResponse res = getService().saveCarImage("Bearer " + authToken, pic);
        return res;
    }
    public ApiResponse SaveCarBckImage(String authToken, TypedFile pic) {
        ApiResponse res = getService().saveCarBckImage("Bearer " + authToken, pic);
        return res;
    }
    public ApiResponse SaveNationalCardImage(String authToken, TypedFile pic) {
        ApiResponse res = getService().SaveNationalCardImage("Bearer " + authToken, pic);
        return res;
    }
    public ApiResponse SaveBankCardImage(String authToken, TypedFile pic) {
        ApiResponse res = getService().SaveBankCardImage("Bearer " + authToken, pic);
        return res;
    }

    public ApiResponse SaveImage(String authToken, String pic, int i) {
        ApiResponse res = getService().saveImage("Bearer " + authToken, pic, i);
        return res;
    }

}
