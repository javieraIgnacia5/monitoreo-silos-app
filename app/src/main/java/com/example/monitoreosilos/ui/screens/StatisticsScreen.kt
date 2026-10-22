
package com.example.monitoreosilos.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.monitoreosilos.data.repository.SiloRepository
import com.example.monitoreosilos.domain.model.MovementType
import com.example.monitoreosilos.domain.model.SiloStatus
import com.example.monitoreosilos.ui.components.BarChart
import java.util.Locale
import com.example.monitoreosilos.ui.components.BarChart

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    onBack: () -> Unit
) {
    val silos by SiloRepository.silos.collectAsState()
    val movements by SiloRepository.movements.collectAsState()
    val isReady by SiloRepository.isReady.collectAsState()

    val totalSilos = silos.size

    val totalCapacity = silos.sumOf {
        it.capacityTon.toDouble()
    }

    val totalStored = silos.sumOf {
        it.currentTon
    }

    val occupancy = if (totalCapacity > 0) {
        totalStored / totalCapacity * 100
    } else {
        0.0
    }

    val criticalSilos = silos.count {
        it.status == SiloStatus.CRITICAL
    }

    val warningSilos = silos.count {
        it.status == SiloStatus.WARNING
    }

    val totalEntries = movements.count {
        it.type == MovementType.INGRESO
    }

    val totalExits = movements.count {
        it.type == MovementType.SALIDA
    }

    fun format(value: Double): String =
        String.format(Locale.getDefault(), "%.1f", value)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Estadísticas") },
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
    ) { padding ->

        if (!isReady) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Text(
                    "Resumen general",
                    style = MaterialTheme.typography.titleLarge
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatisticCard(
                        title = "Silos registrados",
                        value = "$totalSilos",
                        modifier = Modifier.weight(1f)
                    )

                    StatisticCard(
                        title = "Ocupación total",
                        value = "${format(occupancy)}%",
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatisticCard(
                        title = "Capacidad total",
                        value = "${format(totalCapacity)} t",
                        modifier = Modifier.weight(1f)
                    )

                    StatisticCard(
                        title = "Grano almacenado",
                        value = "${format(totalStored)} t",
                        modifier = Modifier.weight(1f)
                    )
                }

                HorizontalDivider()

                Text(
                    "Nivel de almacenamiento",
                    style = MaterialTheme.typography.titleMedium
                )

                if (silos.isEmpty()) {
                    Text("Todavía no hay silos registrados.")
                } else {
                    silos.forEach { silo ->
                        Column(
                            verticalArrangement =
                                Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                "${silo.name}: ${silo.percentage}%",
                                style = MaterialTheme.typography.bodyMedium
                            )

                            LinearProgressIndicator(
                                progress = {
                                    (silo.percentage / 100f)
                                        .coerceIn(0f, 1f)
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                if (silos.isNotEmpty()) {
                    Text(
                        "Comparación de almacenamiento",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        "Porcentaje de capacidad utilizada por silo",
                        style = MaterialTheme.typography.bodySmall
                    )

                    BarChart(
                        values = silos.map { silo ->
                            if (silo.capacityTon > 0) {
                                (silo.currentTon / silo.capacityTon)
                                    .toFloat()
                                    .coerceIn(0f, 1f)
                            } else {
                                0f
                            }
                        },
                        labels = silos.map { it.name },
                        modifier = Modifier.fillMaxWidth()
                    )
                }


                HorizontalDivider()

                Text(
                    "Movimientos registrados",
                    style = MaterialTheme.typography.titleMedium
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatisticCard(
                        title = "Ingresos",
                        value = "$totalEntries",
                        modifier = Modifier.weight(1f)
                    )

                    StatisticCard(
                        title = "Salidas",
                        value = "$totalExits",
                        modifier = Modifier.weight(1f)
                    )
                }

                Text(
                    "Total de movimientos: ${movements.size}",
                    style = MaterialTheme.typography.bodyMedium
                )

                HorizontalDivider()

                Text(
                    "Estado de los silos",
                    style = MaterialTheme.typography.titleMedium
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatisticCard(
                        title = "Críticos",
                        value = "$criticalSilos",
                        modifier = Modifier.weight(1f)
                    )

                    StatisticCard(
                        title = "Advertencias",
                        value = "$warningSilos",
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun StatisticCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor =
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                title,
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                value,
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}
