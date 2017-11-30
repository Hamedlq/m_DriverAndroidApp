package com.mibarim.driver.RestInterfaces;

import com.mibarim.driver.models.ApiResponse;
import com.mibarim.driver.services.SuggestResponseService;

import retrofit.http.Body;
import retrofit.http.POST;

/**
 * Created by Arya on 11/26/2017.
 */

public interface GetSuggestResponseService {

    String SUGGEST_URL = "/GetSuggestedRoutes";

    @POST(GetSuggestResponseService.SUGGEST_URL)
    ApiResponse getSuggested(@Body SuggestResponseService.FakeBody body);
}
