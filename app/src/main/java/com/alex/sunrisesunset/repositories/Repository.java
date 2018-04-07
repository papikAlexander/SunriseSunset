package com.alex.sunrisesunset.repositories;

import android.util.Log;

import com.alex.sunrisesunset.model.Model;
import com.alex.sunrisesunset.service.SunsetSunriseService;

import java.io.IOException;
import java.util.Date;

import io.reactivex.Flowable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Repository {

    private final String BASE_URL = "https://api.sunrise-sunset.org/";

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    SunsetSunriseService service = retrofit.create(SunsetSunriseService.class);

    public Call<Model> getData(String lat, String lng){

        return service.getData(lat, lng);
    }

    public Call<Model> getDataWithDate(String lat, String lng, Date data){
        return service.getDataWithDate(lat, lng, data);
    }

}
