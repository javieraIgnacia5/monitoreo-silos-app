
package com.example.monitoreosilos.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.monitoreosilos.data.repository.UserRepository
import com.example.monitoreosilos.data.weather.WeatherClient
import com.example.monitoreosilos.domain.model.Silo
import com.example.monitoreosilos.domain.model.SiloStatus
import com.example.monitoreosilos.ui.components.CircleGauge
import com.example.monitoreosilos.ui.components.color
import com.example.monitoreosilos.util.CityHelper
import com.example.monitoreosilos.util.LocationHelper
import com.example.monitoreosilos.viewmodel.SilosViewModel
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    onSiloClick: (String) -> Unit,
    onSeeAll: () -> Unit,
    onBellClick: () -> Unit,
    viewModel: SilosViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val silos by viewModel.silos.collectAsState()
    val user by UserRepository.currentUser.collectAsState()

    val firstName = user?.name
        ?.trim()
        ?.split(" ")
        ?.firstOrNull()
        .orEmpty()

    val normal = silos.count { it.status == SiloStatus.NORMAL }
    val warning = silos.count { it.status == SiloStatus.WARNING }
    val critical = silos.count { it.status == SiloStatus.CRITICAL }

    val totalCap = silos.sumOf { it.capacityTon }
    val totalCur = silos.sumOf { it.currentTon }
    val totalPct = if (totalCap == 0) {
        0
    } else {
        ((totalCur * 100) / totalCap).toInt()
    }

    var city by remember { mutableStateOf("Detectando ciudad...") }
    var temperature by remember { mutableStateOf("--°") }
    var weatherDescription by remember {
        mutableStateOf("Consultando clima...")
    }
    var isLoadingWeather by remember { mutableStateOf(false) }
    var selectedStatus by remember {
        mutableStateOf<SiloStatus?>(null)
    }

    suspend fun loadWeather() {
        if (!LocationHelper.hasLocationPermission(context)) {
            city = "Ubicación no autorizada"
            temperature = "--°"
            weatherDescription = "Permite acceder a tu ubicación"
            return
        }

        isLoadingWeather = true

        try {
            val location = LocationHelper.getCurrentLocation(context)

            if (location == null) {
                city = "Ubicación no disponible"
                temperature = "--°"
                weatherDescription = "Activa la ubicación e intenta nuevamente"
                return
            }

            city = CityHelper.getCityName(
                context,
                location.latitude,
                location.longitude
            ) ?: "Ubicación actual"

            val response = WeatherClient.service.getCurrentWeather(
                latitude = location.latitude,
                longitude = location.longitude
            )

            temperature = "${response.current.temperature.toInt()}°"
            weatherDescription = weatherCodeDescription(
                response.current.weatherCode
            )

        } catch (e: Exception) {
            temperature = "--°"
            weatherDescription = "No se pudo consultar el clima"
        } finally {
            isLoadingWeather = false
        }
    }

    val locationPermissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            if (permissions.values.any { it }) {
                scope.launch { loadWeather() }
            } else {
                city = "Ubicación no autorizada"
                weatherDescription = "Se necesita permiso de ubicación"
                temperature = "--°"
            }
        }

    fun refreshWeather() {
        if (LocationHelper.hasLocationPermission(context)) {
            scope.launch { loadWeather() }
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    LaunchedEffect(Unit) {
        if (LocationHelper.hasLocationPermission(context)) {
            loadWeather()
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                if (firstName.isNotEmpty()) {
                    "¡Hola, $firstName! 👋"
                } else {
                    "¡Hola! 👋"
                },
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            IconButton(onClick = onBellClick) {
                Icon(
                    Icons.Filled.Notifications,
                    contentDescription = "Alertas"
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        // Tarjeta del clima real
        Box(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            Color(0xFF2C7A8C),
                            Color(0xFF7FB6D6)
                        )
                    )
                )
                .clickable { refreshWeather() }
                .padding(16.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.LocationOn,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        " $city",
                        color = Color.White,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Text(
                    temperature,
                    color = Color.White,
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    weatherDescription,
                    color = Color.White,
                    style = MaterialTheme.typography.bodySmall
                )

                if (isLoadingWeather) {
                    Spacer(Modifier.height(8.dp))
                    LinearProgressIndicator(
                        modifier = Modifier.width(120.dp),
                        color = Color.White
                    )
                } else {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Toca para actualizar",
                        color = Color.White.copy(alpha = 0.8f),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            Box(
                Modifier
                    .align(Alignment.CenterEnd)
                    .size(84.dp)
            ) {
                Icon(
                    Icons.Filled.WbSunny,
                    contentDescription = null,
                    tint = Color(0xFFFFC83D),
                    modifier = Modifier
                        .size(52.dp)
                        .align(Alignment.TopEnd)
                )

                Icon(
                    Icons.Filled.Cloud,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .size(64.dp)
                        .align(Alignment.BottomStart)
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Text(
            "Estado general",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(8.dp))

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatusBox(
                "Normales",
                normal,
                SiloStatus.NORMAL.color(),
                Modifier.weight(1f),
                selected = selectedStatus == SiloStatus.NORMAL,
                onClick = {
                    selectedStatus =
                        if (selectedStatus == SiloStatus.NORMAL) null
                        else SiloStatus.NORMAL
                }
            )

            StatusBox(
                "Advertencia",
                warning,
                SiloStatus.WARNING.color(),
                Modifier.weight(1f),
                selected = selectedStatus == SiloStatus.WARNING,
                onClick = {
                    selectedStatus =
                        if (selectedStatus == SiloStatus.WARNING) null
                        else SiloStatus.WARNING
                }
            )

            StatusBox(
                "Críticos",
                critical,
                SiloStatus.CRITICAL.color(),
                Modifier.weight(1f),
                selected = selectedStatus == SiloStatus.CRITICAL,
                onClick = {
                    selectedStatus =
                        if (selectedStatus == SiloStatus.CRITICAL) null
                        else SiloStatus.CRITICAL
                }
            )
        }

        if (selectedStatus != null) {
            val filteredSilos = silos.filter {
                it.status == selectedStatus
            }

            Spacer(Modifier.height(10.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(Modifier.padding(12.dp)) {
                    Text(
                        "Silos del estado seleccionado",
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(6.dp))

                    if (filteredSilos.isEmpty()) {
                        Text("No hay silos en este estado.")
                    } else {
                        filteredSilos.forEach { silo ->
                            Text(
                                text = silo.name,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onSiloClick(silo.id)
                                    }
                                    .padding(vertical = 10.dp),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Capacidad total
        Card(
            Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            border = BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.outline
            )
        ) {
            Column(Modifier.padding(16.dp)) {
                Text(
                    "Capacidad total",
                    style = MaterialTheme.typography.bodySmall
                )

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "$totalCap toneladas",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Box(
                        Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                MaterialTheme.colorScheme.primaryContainer
                            )
                            .padding(
                                horizontal = 10.dp,
                                vertical = 4.dp
                            )
                    ) {
                        Text(
                            "$totalPct%",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { totalPct / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.outline
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Silos",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            TextButton(onClick = onSeeAll) {
                Text("Ver todos")
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null
                )
            }
        }

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            silos.take(3).forEach { silo ->
                MiniSilo(
                    silo,
                    Modifier.weight(1f),
                    onClick = { onSiloClick(silo.id) }
                )
            }
        }

        Spacer(Modifier.height(8.dp))
    }
}

private fun weatherCodeDescription(code: Int): String {
    return when (code) {
        0 -> "Despejado"
        1 -> "Mayormente despejado"
        2 -> "Parcialmente nublado"
        3 -> "Nublado"
        45, 48 -> "Niebla"
        51, 53, 55, 56, 57 -> "Llovizna"
        61, 63, 65, 66, 67 -> "Lluvia"
        71, 73, 75, 77 -> "Nieve"
        80, 81, 82 -> "Chubascos"
        85, 86 -> "Chubascos de nieve"
        95, 96, 99 -> "Tormenta"
        else -> "Condición meteorológica desconocida"
    }
}

@Composable
private fun StatusBox(
    label: String,
    count: Int,
    color: Color,
    modifier: Modifier,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                color.copy(
                    alpha = if (selected) 0.30f else 0.15f
                )
            )
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "$count",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            color = color
        )
    }
}

@Composable
private fun MiniSilo(
    silo: Silo,
    modifier: Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier
            .clip(RoundedCornerShape(18.dp))
            .background(
                silo.status.color().copy(alpha = 0.13f)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Filled.Inventory2,
            contentDescription = null,
            tint = silo.status.color(),
            modifier = Modifier.size(26.dp)
        )

        Spacer(Modifier.height(2.dp))

        Text(
            silo.name,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(8.dp))

        CircleGauge(
            percent = silo.percentage,
            color = silo.status.color(),
            modifier = Modifier.size(72.dp),
            strokeWidth = 7.dp
        ) {
            Text(
                "${silo.percentage}%",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
