package com.example.weatherv3

import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherForcastService {
    @GET("forecast")
    suspend fun getWeatherForcast(
        @Query("q") city: String,
        @Query("units") units: String,
        @Query("appid") apiKey: String
    ): WeatherDataForcast
}
