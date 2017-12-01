package com.mibarim.driver.RestInterfaces;

import com.mibarim.driver.core.Constants;
import com.mibarim.driver.models.ApiResponse;
import com.mibarim.driver.services.SuggestResponseService;

import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.Header;
import retrofit.http.POST;

/**
 * Created by Arya on 11/26/2017.
 */

public interface GetSuggestResponseService {

    String SUGGEST_URL = "/GetSuggestedRoutes";
    String FILTER_ID_URL = "/AcceptSuggestRoute";

    @POST(GetSuggestResponseService.SUGGEST_URL)
    ApiResponse getSuggested(@Body SuggestResponseService.FakeBody body);

    @POST(GetSuggestResponseService.FILTER_ID_URL)
    @FormUrlEncoded
    ApiResponse sendFilterId(@Header(Constants.Http.PARAM_AUTHORIZATION) String authToken,
                             @Field("FilterId") long Id,
                             @Field("CarSeats") int seats);
}
