
package com.example.monitoreosilos.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.monitoreosilos.data.repository.SiloRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StorageScreen(
    onBack: () -> Unit
) {
    val silos by SiloRepository.silos.collectAsState()
    val movements by SiloRepository.movements.collectAsState()
    val isReady by SiloRepository.isReady.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Almacenamiento local")
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {

            Text(
                text = "Datos almacenados",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Consulta la información guardada en este dispositivo.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(20.dp))

            if (!isReady) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                Text("Esperando la carga de los datos...")

            } else {

                StorageInfoCard(
                    title = "Silos registrados",
                    value = silos.size.toString(),
                    description = "Cantidad de silos guardados en la aplicación."
                )

                Spacer(Modifier.height(12.dp))

                StorageInfoCard(
                    title = "Movimientos registrados",
                    value = movements.size.toString(),
                    description = "Registros de ingresos y salidas de granos."
                )

                Spacer(Modifier.height(12.dp))

                StorageInfoCard(
                    title = "Estado de la base de datos",
                    value = "Cargada",
                    description = "Los datos se consultan desde la base de datos local Room."
                )
            }

            Spacer(Modifier.height(24.dp))

            HorizontalDivider()

            Spacer(Modifier.height(20.dp))

            Text(
                text = "Información importante",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Los silos y sus movimientos se almacenan localmente en este dispositivo. Esta pantalla es informativa y no modifica ni elimina registros.",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun StorageInfoCard(
    title: String,
    value: String,
    description: String
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
