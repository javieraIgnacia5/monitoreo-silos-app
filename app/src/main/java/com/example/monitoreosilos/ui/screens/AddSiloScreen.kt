
package com.example.monitoreosilos.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.rememberCoroutineScope
import com.example.monitoreosilos.data.repository.SiloRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSiloScreen(
    onBack: () -> Unit,
    onSaved: (
        name: String,
        grain: String,
        farm: String,
        capacity: Int,
        current: Double
    ) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var grain by remember { mutableStateOf("") }
    var farm by remember { mutableStateOf("") }
    var capacity by remember { mutableStateOf("") }
    var current by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var isSaving by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Agregar silo",
                        fontWeight = FontWeight.Bold
                    )
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {

            Text(
                "Registrar nuevo silo",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre del silo") },
                placeholder = { Text("Ejemplo: Silo 6") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = grain,
                onValueChange = { grain = it },
                label = { Text("Tipo de grano") },
                placeholder = { Text("Ejemplo: Maíz") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = farm,
                onValueChange = { farm = it },
                label = { Text("Predio") },
                placeholder = { Text("Ejemplo: Predio Norte") },
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

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = current,
                onValueChange = { current = it },
                label = { Text("Cantidad actual (toneladas)") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            if (error != null) {
                Spacer(Modifier.height(12.dp))

                Text(
                    text = error ?: "",
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(Modifier.height(24.dp))

            Button(
                enabled = !isSaving,
                onClick = {
                    val capacityValue = capacity.trim().toIntOrNull()
                    val currentValue = current.trim()
                        .replace(',', '.')
                        .toDoubleOrNull()

                    error = when {
                        name.isBlank() ->
                            "Debes ingresar el nombre del silo."

                        grain.isBlank() ->
                            "Debes ingresar el tipo de grano."

                        farm.isBlank() ->
                            "Debes ingresar el predio."

                        capacityValue == null || capacityValue <= 0 ->
                            "La capacidad debe ser un número entero mayor que cero."

                        currentValue == null ||
                                !currentValue.isFinite() ||
                                currentValue < 0 ->
                            "La cantidad actual debe ser un número válido."

                        currentValue > capacityValue ->
                            "La cantidad actual no puede superar la capacidad."

                        else -> null
                    }

                    if (error == null &&
                        capacityValue != null &&
                        currentValue != null
                    ) {

                        if (!isSaving) {
                            isSaving = true

                            scope.launch {
                                try {
                                    SiloRepository.addSilo(
                                        name = name.trim(),
                                        grain = grain.trim(),
                                        farm = farm.trim(),
                                        capacity = capacityValue,
                                        current = currentValue
                                    )

                                    onSaved(
                                        name.trim(),
                                        grain.trim(),
                                        farm.trim(),
                                        capacityValue,
                                        currentValue
                                    )

                                } catch (e: Exception) {
                                    error = e.message
                                        ?: "No se pudo guardar el silo."
                                } finally {
                                    isSaving = false
                                }
                            }
                        }

                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                )
            ) {
                Text(
                    text = if (isSaving) "Guardando..." else "Guardar silo",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
