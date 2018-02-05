package com.mibarim.driver.RestInterfaces;

import com.mibarim.driver.core.Constants;
import com.mibarim.driver.models.ApiResponse;
import com.mibarim.driver.models.Trip.LocationDataBaseModel;

import java.util.List;

import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.Header;
import retrofit.http.POST;

/**
 * Created by Hamed on 3/10/2016.
 */
public interface GetTripService {
    @POST(Constants.Http.URL_USER_TRIP_LOCATION)
    @FormUrlEncoded
    ApiResponse SendUserLocation(@Field("Mobile") String mobile, @Field("Lat") String latitude,
                                 @Field("Lng") String longitude
    );

    /*@POST(Constants.Http.URL_GET_DRIVE_ROUTE_INFO)
    @FormUrlEncoded
    ApiResponse GetTripInfo(@Header(Constants.Http.PARAM_AUTHORIZATION) String authToken,
                             @Field("DriverRouteId") String driverRouteId
    );*/

    @POST(Constants.Http.URL_END_TRIP)
    @FormUrlEncoded
    ApiResponse EndTrip(@Header(Constants.Http.PARAM_AUTHORIZATION) String authToken,
                        @Field("TripId") String tripId
    );

    @POST(Constants.Http.URL_SET_TRIP_LOCATION)
    @FormUrlEncoded
    ApiResponse SetTripPoint(@Header(Constants.Http.PARAM_AUTHORIZATION) String authToken,
                             @Field("DriverLat") String lat,
                             @Field("DriverLng") String lng,
                             @Field("TripId") long tripId,
                             @Field("TripState") int tripState
    );

    @POST(Constants.Http.URL_SET_TRIP_LOCATION_LIST)
    ApiResponse SetTripPoints(@Header(Constants.Http.PARAM_AUTHORIZATION) String authToken,
                              @Body List<LocationDataBaseModel> locations
    );

    @POST(Constants.Http.URL_GET_TRIP)
    @FormUrlEncoded
    ApiResponse GetUserTrip(@Header(Constants.Http.PARAM_AUTHORIZATION) String authToken,
                            @Field("Id") String Id
    );

}
