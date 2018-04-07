package com.alex.sunrisesunset.service;

import com.alex.sunrisesunset.model.Model;

import java.util.Date;

import io.reactivex.Flowable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SunsetSunriseService {

    @GET("json")
    Call<Model> getData(@Query("lat") String lat, @Query("lng") String lng);

    @GET("json")
    Call<Model> getDataWithDate(@Query("lat")String lat, @Query("lng")String lng,
                          @Query("date")Date date);

}
