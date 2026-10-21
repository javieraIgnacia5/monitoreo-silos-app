
package com.example.monitoreosilos.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.monitoreosilos.data.repository.SiloRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditSiloScreen(
    siloId: String,
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    val silos by SiloRepository.silos.collectAsState()
    val silo = silos.firstOrNull { it.id == siloId }

    var name by remember(siloId) { mutableStateOf("") }
    var grain by remember(siloId) { mutableStateOf("") }
    var farm by remember(siloId) { mutableStateOf("") }
    var capacity by remember(siloId) { mutableStateOf("") }

    var error by remember { mutableStateOf<String?>(null) }
    var isSaving by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    LaunchedEffect(silo?.id) {
        silo?.let {
            name = it.name
            grain = it.grain
            farm = it.farm
            capacity = it.capacityTon.toString()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Editar silo", fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { padding ->

        if (silo == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                Text("Silo no encontrado")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {

                Text(
                    "Modificar información del silo",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre del silo") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = grain,
                    onValueChange = { grain = it },
                    label = { Text("Tipo de grano") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = farm,
                    onValueChange = { farm = it },
                    label = { Text("Predio") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = capacity,
                    onValueChange = { capacity = it },
                    label = { Text("Capacidad máxima (toneladas)") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    "Cantidad almacenada: ${silo.currentTon} toneladas",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    "Para modificar esta cantidad, registra un ingreso o salida.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (error != null) {
                    Spacer(Modifier.height(12.dp))
                    Text(
                        error ?: "",
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = {
                        val capacityValue = capacity.trim().toIntOrNull()

                        error = when {
                            name.isBlank() ->
                                "Debes ingresar el nombre del silo."

                            grain.isBlank() ->
                                "Debes ingresar el tipo de grano."

                            farm.isBlank() ->
                                "Debes ingresar el predio."

                            capacityValue == null || capacityValue <= 0 ->
                                "La capacidad debe ser un número entero mayor que cero."

                            capacityValue < silo.currentTon ->
                                "La capacidad no puede ser menor que las toneladas almacenadas."

                            else -> null
                        }

                        if (error == null && capacityValue != null) {
                            isSaving = true

                            scope.launch {
                                try {
                                    SiloRepository.updateSilo(
                                        siloId = siloId,
                                        name = name,
                                        grain = grain,
                                        farm = farm,
                                        capacity = capacityValue
                                    )
                                    onSaved()
                                } catch (e: Exception) {
                                    error = e.message
                                        ?: "No se pudo actualizar el silo."
                                } finally {
                                    isSaving = false
                                }
                            }
                        }
                    },
                    enabled = !isSaving,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary
                    )
                ) {
                    Text(
                        if (isSaving) "Guardando..." else "Guardar cambios",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
