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
    var filter by remember { mutableStateOf("ALL") }
    val list = when (filter) {
        "INGRESO" -> movements.filter { it.type == MovementType.INGRESO }
        "SALIDA" -> movements.filter { it.type == MovementType.SALIDA }
        else -> movements
    }
    val filterLabel = when (filter) {
        "INGRESO" -> "Ingresos"
        "SALIDA" -> "Salidas"
        else -> "Todos"
    }

    Column(Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Spacer(Modifier.height(16.dp))
        Text("Historial", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("Movimientos de todos los silos", style = MaterialTheme.typography.bodySmall)
        Spacer(Modifier.height(12.dp))
        SelectField(
            label = null,
            selected = filterLabel,
            placeholder = "Todos",
            options = listOf("ALL" to "Todos", "INGRESO" to "Ingresos", "SALIDA" to "Salidas"),
            onSelect = { filter = it }
        )
        Spacer(Modifier.height(8.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(list, key = { it.id }) { m ->
                val isIn = m.type == MovementType.INGRESO
                val color = if (isIn) StatusNormal else StatusCritical
                Card(
                    Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier.size(48.dp).clip(CircleShape).background(color.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                if (isIn) Icons.Filled.ArrowUpward else Icons.Filled.ArrowDownward,
                                contentDescription = m.type.label,
                                tint = color
                            )
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(m.type.label, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            Text(
                                "${m.tons.formatTon()} ton · ${m.siloName}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(m.date, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}