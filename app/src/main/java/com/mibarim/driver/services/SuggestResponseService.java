package com.mibarim.driver.services;

import com.mibarim.driver.RestInterfaces.GetSuggestResponseService;
import com.mibarim.driver.core.Constants;
import com.mibarim.driver.models.ApiResponse;

import retrofit.RestAdapter;

/**
 * Created by Arya on 11/26/2017.
 */

public class SuggestResponseService {

    private RestAdapter restAdapter;

    public SuggestResponseService() {
        restAdapter = new RestAdapter.Builder()
                .setEndpoint(Constants.Http.URL_BASE)
                .build();
    }

    public RestAdapter getRestAdapter() {
        return restAdapter;
    }

    private GetSuggestResponseService getService() {
        return getRestAdapter().create(GetSuggestResponseService.class);
    }

    public ApiResponse getSuggestRoutes() {
        ApiResponse response = getService().getSuggested(new FakeBody());
        return response;
    }

    public ApiResponse sendFilterId(String authToken, long filterId, int seates){
        ApiResponse response = getService().sendFilterId("Bearer " + authToken, filterId, seates);
        return response;
    }

    public class FakeBody {

        FakeBody(){

        }
    }

}
