package com.example.monitoreosilos.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monitoreosilos.data.remote.WeatherApi
import com.example.monitoreosilos.data.remote.WeatherRetrofitClient
import com.example.monitoreosilos.data.remote.mapWeatherCodeToDescription
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

data class WeatherUiState(
    val isLoading: Boolean = true,
    val temperature: String = "--",
    val humidity: String = "--",
    val windSpeed: String = "--",
    val description: String = "Consultando clima...",
    val error: String? = null
)

class WeatherViewModel(
    private val api: WeatherApi = WeatherRetrofitClient.weatherApi
) : ViewModel() {

    constructor() : this(WeatherRetrofitClient.weatherApi)

    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    init {
        fetchWeather()
    }

    fun fetchWeather() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val response = api.getCurrentWeather()
                val current = response.current
                if (current != null) {
                    _uiState.value = WeatherUiState(
                        isLoading = false,
                        temperature = "${current.temperature.roundToInt()}°",
                        humidity = "${current.humidity}%",
                        windSpeed = "${current.windSpeed.roundToInt()} km/h",
                        description = mapWeatherCodeToDescription(current.weatherCode),
                        error = null
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        description = "Sin datos disponibles",
                        error = "Respuesta vacía"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    description = "Sin conexión al servicio",
                    error = e.message ?: "Error al obtener clima"
                )
            }
        }
    }
}