
package com.example.monitoreosilos.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.monitoreosilos.data.repository.SiloRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SiloLocationScreen(
    siloId: String,
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val silos by SiloRepository.silos.collectAsState()
    val silo = silos.firstOrNull { it.id == siloId }

    var address by remember(siloId) {
        mutableStateOf("")
    }

    var latitude by remember(siloId) {
        mutableStateOf<Double?>(null)
    }

    var longitude by remember(siloId) {
        mutableStateOf<Double?>(null)
    }

    var initialized by remember(siloId) {
        mutableStateOf(false)
    }

    var message by remember {
        mutableStateOf<String?>(null)
    }

    var saving by remember {
        mutableStateOf(false)
    }

    var locating by remember {
        mutableStateOf(false)
    }

    // Cargar ubicación guardada
    LaunchedEffect(siloId, silo) {
        if (silo != null && !initialized) {
            address = silo.locationAddress
            latitude = silo.latitude
            longitude = silo.longitude
            initialized = true
        }
    }

    val locationManager = remember(context) {
        context.getSystemService(
            Context.LOCATION_SERVICE
        ) as LocationManager
    }

    // Comprobar permisos GPS
    fun hasLocationPermission(): Boolean {
        val finePermission =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        val coarsePermission =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        return finePermission || coarsePermission
    }

    // Convertir coordenadas en dirección
    fun findAddressFromCoordinates(
        lat: Double,
        lon: Double
    ) {
        if (!Geocoder.isPresent()) {
            locating = false
            message =
                "Coordenadas obtenidas, pero no hay servicio de direcciones."
            return
        }

        locating = true
        message = "Buscando dirección automáticamente..."

        val geocoder = Geocoder(
            context,
            Locale.forLanguageTag("es-CL")
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            try {
                geocoder.getFromLocation(
                    lat,
                    lon,
                    1,
                    object : Geocoder.GeocodeListener {

                        override fun onGeocode(
                            addresses: MutableList<android.location.Address>
                        ) {
                            locating = false

                            val detectedAddress =
                                addresses.firstOrNull()
                                    ?.getAddressLine(0)

                            if (!detectedAddress.isNullOrBlank()) {
                                address = detectedAddress

                                message =
                                    "Dirección encontrada. Revísala antes de guardar."
                            } else {
                                message =
                                    "GPS obtenido, pero no se encontró una dirección."
                            }
                        }

                        override fun onError(
                            errorMessage: String?
                        ) {
                            locating = false

                            message =
                                "GPS obtenido. No se pudo encontrar la dirección; puedes escribirla manualmente."
                        }
                    }
                )
            } catch (e: Exception) {
                locating = false
                message =
                    "No se pudo buscar la dirección: ${e.message}"
            }

        } else {

            // Android 12 o versiones anteriores:
            // búsqueda en segundo plano para no bloquear la pantalla.
            scope.launch {
                val detectedAddress = withContext(Dispatchers.IO) {
                    try {
                        @Suppress("DEPRECATION")
                        geocoder.getFromLocation(
                            lat,
                            lon,
                            1
                        )?.firstOrNull()?.getAddressLine(0)
                    } catch (e: Exception) {
                        null
                    }
                }

                locating = false

                if (!detectedAddress.isNullOrBlank()) {
                    address = detectedAddress

                    message =
                        "Dirección encontrada. Revísala antes de guardar."
                } else {
                    message =
                        "GPS obtenido, pero no se encontró una dirección. Puedes escribirla manualmente."
                }
            }
        }
    }

    // Obtener ubicación del dispositivo
    @SuppressLint("MissingPermission")
    fun obtainLocation() {

        if (!hasLocationPermission()) {
            message =
                "Debes permitir el acceso a la ubicación."
            return
        }

        val providers = try {
            locationManager.getProviders(true)
        } catch (e: Exception) {
            message =
                "No se pudo consultar el GPS: ${e.message}"
            return
        }

        val provider = when {
            providers.contains(
                LocationManager.GPS_PROVIDER
            ) -> LocationManager.GPS_PROVIDER

            providers.contains(
                LocationManager.NETWORK_PROVIDER
            ) -> LocationManager.NETWORK_PROVIDER

            else -> null
        }

        if (provider == null) {
            message =
                "Activa la ubicación del teléfono e inténtalo nuevamente."
            return
        }

        locating = true
        message = "Buscando ubicación actual..."

        try {
            locationManager.getCurrentLocation(
                provider,
                null,
                ContextCompat.getMainExecutor(context)
            ) { location ->

                if (location != null) {

                    latitude = location.latitude
                    longitude = location.longitude

                    // Buscar dirección usando las coordenadas
                    findAddressFromCoordinates(
                        location.latitude,
                        location.longitude
                    )

                } else {
                    locating = false

                    message =
                        "No se pudo obtener la ubicación. Inténtalo al aire libre."
                }
            }

        } catch (e: Exception) {
            locating = false

            message =
                "Error al obtener ubicación: ${e.message}"
        }
    }

    // Solicitar permisos
    val permissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->

            if (permissions.values.any { it }) {
                obtainLocation()
            } else {
                message =
                    "Permiso denegado. Puedes escribir la ubicación manualmente."
            }
        }

    // Pantalla
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Ubicación del silo")
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack
                    ) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),

            verticalArrangement =
                Arrangement.spacedBy(16.dp)
        ) {

            Text(
                text = silo?.name ?: "Cargando silo...",
                style =
                    MaterialTheme.typography.titleLarge
            )

            Text(
                text =
                    "Escribe una dirección o utiliza el GPS para detectarla automáticamente.",
                style =
                    MaterialTheme.typography.bodyMedium
            )

            // Dirección manual o automática
            OutlinedTextField(
                value = address,
                onValueChange = {
                    address = it
                },
                label = {
                    Text("Dirección o referencia")
                },
                placeholder = {
                    Text(
                        "Ej.: Fundo Santa María, Melipilla"
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            HorizontalDivider()

            Text(
                text = "Coordenadas GPS",
                style =
                    MaterialTheme.typography.titleMedium
            )

            Text(
                text =
                    "Latitud: ${latitude?.toString() ?: "Sin registrar"}"
            )

            Text(
                text =
                    "Longitud: ${longitude?.toString() ?: "Sin registrar"}"
            )

            // Botón GPS
            OutlinedButton(
                onClick = {

                    if (hasLocationPermission()) {
                        obtainLocation()
                    } else {
                        permissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }
                },

                enabled = !locating && !saving,
                modifier = Modifier.fillMaxWidth()
            ) {

                Icon(
                    Icons.Filled.LocationOn,
                    contentDescription = null
                )

                Spacer(
                    Modifier.width(8.dp)
                )

                Text(
                    if (locating) {
                        "Buscando ubicación..."
                    } else {
                        "Obtener ubicación con GPS"
                    }
                )
            }

            // Eliminar coordenadas
            if (
                latitude != null &&
                longitude != null
            ) {
                TextButton(
                    onClick = {
                        latitude = null
                        longitude = null

                        message =
                            "Coordenadas eliminadas del formulario."
                    }
                ) {
                    Text("Quitar coordenadas GPS")
                }
            }

            // Mensajes
            message?.let {
                Text(
                    text = it,
                    color =
                        MaterialTheme.colorScheme.primary
                )
            }

            HorizontalDivider()

            // Guardar ubicación
            Button(
                onClick = {

                    saving = true
                    message = null

                    scope.launch {

                        try {
                            SiloRepository.updateSiloLocation(
                                siloId = siloId,
                                address = address,
                                latitude = latitude,
                                longitude = longitude
                            )

                            onSaved()

                        } catch (e: Exception) {

                            message =
                                e.message
                                    ?: "No se pudo guardar la ubicación."

                        } finally {
                            saving = false
                        }
                    }
                },

                enabled =
                    silo != null &&
                            initialized &&
                            !saving &&
                            !locating,

                modifier =
                    Modifier.fillMaxWidth()
            ) {

                Text(
                    if (saving) {
                        "Guardando..."
                    } else {
                        "Guardar ubicación"
                    }
                )
            }
        }
    }
}
