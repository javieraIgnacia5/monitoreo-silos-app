package com.example.monitoreosilos.data.remote

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    @SerializedName("current")
    val current: CurrentWeatherDto?
)

data class CurrentWeatherDto(
    @SerializedName("temperature_2m")
    val temperature: Double,
    @SerializedName("relative_humidity_2m")
    val humidity: Int,
    @SerializedName("weather_code")
    val weatherCode: Int,
    @SerializedName("wind_speed_10m")
    val windSpeed: Double
)

fun mapWeatherCodeToDescription(code: Int): String {
    return when (code) {
        0 -> "Despejado"
        1, 2, 3 -> "Parcialmente nublado"
        45, 48 -> "Neblina"
        51, 53, 55, 56, 57 -> "Llovizna"
        61, 63, 65, 66, 67 -> "Lluvia"
        71, 73, 75, 77 -> "Nieve"
        80, 81, 82 -> "Chubascos"
        95, 96, 99 -> "Tormenta eléctrica"
        else -> "Condición variable"
    }
}