package com.mibarim.driver.RestInterfaces;

import com.mibarim.driver.core.Constants;
import com.mibarim.driver.models.ApiResponse;

import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

public interface RegistrationService {

    @POST(Constants.Http.URL_REGISTER_FRAG)
    @FormUrlEncoded
    ApiResponse register(@Field(Constants.Http.PARAM_REG_USERNAME) String mobile);
}
