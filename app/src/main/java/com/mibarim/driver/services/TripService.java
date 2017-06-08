package com.mibarim.driver.services;

import com.mibarim.driver.RestInterfaces.GetTripService;
import com.mibarim.driver.models.ApiResponse;

import retrofit.RestAdapter;

/**
 * Created by Hamed on 3/10/2016.
 */
public class TripService {

    private RestAdapter restAdapter;

    public TripService(RestAdapter restAdapter) {
        this.restAdapter = restAdapter;
    }

    public RestAdapter getRestAdapter() {
        return restAdapter;
    }

    private GetTripService getService() {
        return getRestAdapter().create(GetTripService.class);
    }

    public ApiResponse sendUserLocation(String mobile, String latitude, String longitude) {
        ApiResponse res = getService().SendUserLocation(mobile, latitude, longitude);
        return res;
    }

    /*public ApiResponse getTripInfo(String authToken, Long driverRouteId) {
        ApiResponse res=getService().GetTripInfo("Bearer " + authToken, String.valueOf(driverRouteId));
        return res;
    }*/

    public ApiResponse endTrip(String authToken, long tripId) {
        ApiResponse res = getService().EndTrip("Bearer " + authToken, String.valueOf(tripId));
        return res;
    }

    public ApiResponse setTripPoint(String authToken, String lat, String lng, long tripId, int tripState) {
        ApiResponse res = getService().SetTripPoint("Bearer " + authToken, lat, lng, tripId, tripState);
        return res;
    }

    public ApiResponse getUserTrip(String authToken) {
        ApiResponse res = getService().GetUserTrip("Bearer " + authToken, "7");
        return res;
    }
}
