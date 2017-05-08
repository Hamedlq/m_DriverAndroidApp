package com.mibarim.driver.services;

import com.mibarim.driver.RestInterfaces.GetEventService;
import com.mibarim.driver.models.ApiResponse;

import retrofit.RestAdapter;

/**
 * Created by Hamed on 3/10/2016.
 */
public class EventService {

    private RestAdapter restAdapter;

    public EventService(RestAdapter restAdapter) {
        this.restAdapter = restAdapter;
    }

    public RestAdapter getRestAdapter() {
        return restAdapter;
    }

    private GetEventService getService() {
        return getRestAdapter().create(GetEventService.class);
    }

    public ApiResponse GetEvents() {
        ApiResponse res = getService().GetEvents("7");
        return res;
    }
}
