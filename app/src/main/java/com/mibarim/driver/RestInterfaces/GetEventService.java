package com.mibarim.driver.RestInterfaces;

import com.mibarim.driver.core.Constants;
import com.mibarim.driver.models.ApiResponse;

import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by Hamed on 3/10/2016.
 */
public interface GetEventService {
    @POST(Constants.Http.URL_GET_EVENT)
    @FormUrlEncoded
    ApiResponse GetEvents(@Field("UserId") String s);
}
