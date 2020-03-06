package com.weather.logger.retrofit

import com.weather.logger.model.WeatherData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {

    @GET("weather?")
    fun getWeatherData(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("appid") appid: String
    ): Call<WeatherData>
}