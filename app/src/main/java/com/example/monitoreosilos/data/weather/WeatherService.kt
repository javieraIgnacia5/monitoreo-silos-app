
package com.example.monitoreosilos.data.weather

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Query

// Datos que devuelve Open-Meteo
data class WeatherResponse(
    @SerializedName("current")
    val current: CurrentWeather
)

data class CurrentWeather(
    @SerializedName("temperature_2m")
    val temperature: Double,

    @SerializedName("relative_humidity_2m")
    val humidity: Int,

    @SerializedName("weather_code")
    val weatherCode: Int
)

// Servicio para consultar el clima real
interface WeatherService {

    @GET("v1/forecast")
    suspend fun getCurrentWeather(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current")
        current: String =
            "temperature_2m,relative_humidity_2m,weather_code",
        @Query("timezone")
        timezone: String = "auto"
    ): WeatherResponse
}
