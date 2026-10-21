
package com.example.monitoreosilos.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.monitoreosilos.data.repository.SiloRepository
import com.example.monitoreosilos.domain.model.MovementType
import com.example.monitoreosilos.ui.components.SelectField
import com.example.monitoreosilos.ui.theme.StatusCritical
import com.example.monitoreosilos.ui.theme.StatusNormal
import com.example.monitoreosilos.util.formatTon

@Composable
fun HistoryScreen() {

    val movements by SiloRepository.movements.collectAsState()
    val silos by SiloRepository.silos.collectAsState()

    // Filtro por tipo de movimiento
    var filter by remember {
        mutableStateOf("ALL")
    }

    // Filtro por silo
    var selectedSiloId by remember {
        mutableStateOf("ALL")
    }

    // Aplicar ambos filtros
    val filteredMovements = movements.filter { movement ->

        val matchesType = when (filter) {
            "INGRESO" -> movement.type == MovementType.INGRESO
            "SALIDA" -> movement.type == MovementType.SALIDA
            else -> true
        }

        val matchesSilo =
            selectedSiloId == "ALL" ||
                    movement.siloId == selectedSiloId

        matchesType && matchesSilo
    }

    val filterLabel = when (filter) {
        "INGRESO" -> "Ingresos"
        "SALIDA" -> "Salidas"
        else -> "Todos"
    }

    val siloLabel = if (selectedSiloId == "ALL") {
        "Todos los silos"
    } else {
        silos.firstOrNull {
            it.id == selectedSiloId
        }?.name ?: "Todos los silos"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {

        Spacer(Modifier.height(16.dp))

        Text(
            text = "Historial",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Movimientos de todos los silos",
            style = MaterialTheme.typography.bodySmall
        )

        Spacer(Modifier.height(12.dp))

        // ---------- Filtro por tipo ----------

        SelectField(
            label = null,
            selected = filterLabel,
            placeholder = "Todos",
            options = listOf(
                "ALL" to "Todos",
                "INGRESO" to "Ingresos",
                "SALIDA" to "Salidas"
            ),
            onSelect = {
                filter = it
            }
        )

        Spacer(Modifier.height(8.dp))

        // ---------- Filtro por silo ----------

        SelectField(
            label = null,
            selected = siloLabel,
            placeholder = "Todos los silos",
            options = listOf(
                "ALL" to "Todos los silos"
            ) + silos.map { silo ->
                silo.id to silo.name
            },
            onSelect = {
                selectedSiloId = it
            }
        )

        Spacer(Modifier.height(12.dp))

        // ---------- Cantidad de resultados ----------

        Text(
            text = "Movimientos encontrados: ${filteredMovements.size}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(8.dp))

        // ---------- Historial ----------

        if (filteredMovements.isEmpty()) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No hay movimientos para los filtros seleccionados.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

        } else {

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {

                items(
                    items = filteredMovements,
                    key = { it.id }
                ) { m ->

                    val isIn = m.type == MovementType.INGRESO

                    val color = if (isIn) {
                        StatusNormal
                    } else {
                        StatusCritical
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {

                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(
                                        color.copy(alpha = 0.15f)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {

                                Icon(
                                    imageVector = if (isIn) {
                                        Icons.Filled.ArrowUpward
                                    } else {
                                        Icons.Filled.ArrowDownward
                                    },
                                    contentDescription = m.type.label,
                                    tint = color
                                )
                            }

                            Spacer(Modifier.width(12.dp))

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {

                                Text(
                                    text = m.type.label,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleMedium
                                )

                                Text(
                                    text = "${m.tons.formatTon()} ton · ${m.siloName}",
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                Text(
                                    text = m.date,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
