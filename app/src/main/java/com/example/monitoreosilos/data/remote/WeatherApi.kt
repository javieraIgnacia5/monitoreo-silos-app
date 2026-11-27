package com.example.monitoreosilos.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("v1/forecast")
    suspend fun getCurrentWeather(
        @Query("latitude") latitude: Double = -36.8201,
        @Query("longitude") longitude: Double = -73.0444,
        @Query("current") current: String = "temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m"
    ): WeatherResponse
}