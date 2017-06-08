package com.mibarim.driver.RestInterfaces;

import com.mibarim.driver.core.Constants;
import com.mibarim.driver.models.ApiResponse;
import com.mibarim.driver.models.PersonalInfoModel;
import com.mibarim.driver.models.UserInfo.UserRouteModel;

import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.Header;
import retrofit.http.POST;

/**
 * Created by Hamed on 3/10/2016.
 */
public interface GetRouteResponseService {
    @POST(Constants.Http.URL_GET_ROUTE)
    @FormUrlEncoded
    ApiResponse GetUserRoutes(@Header(Constants.Http.PARAM_AUTHORIZATION) String authToken,
                                      @Field("UserId") String Id);

    @POST(Constants.Http.ROUTE_IMAGE_URL)
    @FormUrlEncoded
    PersonalInfoModel GetRouteUserImage(@Header(Constants.Http.PARAM_AUTHORIZATION) String authToken,
                              @Field("RouteRequestId") long Id);

    @POST(Constants.Http.SUGGEST_ROUTE_URL)
    @FormUrlEncoded
    ApiResponse GetRouteSuggest(@Header(Constants.Http.PARAM_AUTHORIZATION) String authToken,
                                        @Field("RouteRequestId") long Id);
    @POST(Constants.Http.GET_PASSENGER_ROUTE_URL)
    @FormUrlEncoded
    ApiResponse GetPassengerRoutes(@Header(Constants.Http.PARAM_AUTHORIZATION) String authToken,
                                @Field("FilterId") long Id);

    @POST(Constants.Http.GET_DRIVER_ROUTE_URL)
    @FormUrlEncoded
    ApiResponse GetDriverRoutes(@Header(Constants.Http.PARAM_AUTHORIZATION) String authToken,
                                   @Field("FilterId") long Id);

    @POST(Constants.Http.GET_STATION_ROUTE_URL)
    @FormUrlEncoded
    ApiResponse GetStationRoutes(@Field("Id") long Id);

    @POST(Constants.Http.SIMILAR_SUGGEST_ROUTE_URL)
    @FormUrlEncoded
    ApiResponse GetRouteSimilarSuggests(@Header(Constants.Http.PARAM_AUTHORIZATION) String authToken,
                                         @Field("ContactId") long Id);

    @POST(Constants.Http.TRIP_PROFILE_URL)
    @FormUrlEncoded
    UserRouteModel GetTripProfileInfo(@Header(Constants.Http.PARAM_AUTHORIZATION) String authToken,
                                      @Field("RouteRequestId") long Id);

    @POST(Constants.Http.GET_STATION_URL)
    @FormUrlEncoded
    ApiResponse GetStations(@Field("StRouteId") long stRouteId);

}
