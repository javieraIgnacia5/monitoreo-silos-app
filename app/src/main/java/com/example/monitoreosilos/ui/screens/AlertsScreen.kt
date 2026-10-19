
package com.example.monitoreosilos.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.monitoreosilos.domain.model.Silo
import com.example.monitoreosilos.domain.model.SiloStatus
import com.example.monitoreosilos.viewmodel.AlertsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertsScreen(
    onBack: () -> Unit,
    viewModel: AlertsViewModel = viewModel()
) {
    val silos by viewModel.silos.collectAsState()

    val alertas = silos.filter {
        it.status != SiloStatus.NORMAL
    }.sortedBy { it.percentage }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Alertas",
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Centro de notificaciones",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Text(
                "Monitorea los niveles de almacenamiento de tus silos.",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                "${alertas.size} alertas activas",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(4.dp))

            if (alertas.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF118C8C),
                            modifier = Modifier.size(44.dp)
                        )

                        Spacer(Modifier.height(12.dp))

                        Text(
                            "¡Todo está en orden!",
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            "No hay silos con niveles bajos o críticos."
                        )
                    }
                }
            } else {
                alertas.forEach { silo ->
                    AlertaSiloCard(silo)
                }
            }
        }
    }
}

@Composable
private fun AlertaSiloCard(silo: Silo) {

    val esCritico = silo.status == SiloStatus.CRITICAL

    val color = if (esCritico) {
        Color(0xFFB45309)
    } else {
        Color(0xFFF2BB16)
    }

    val titulo = if (esCritico) {
        "Nivel crítico - ${silo.name}"
    } else {
        "Nivel bajo - ${silo.name}"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        color.copy(alpha = 0.12f),
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.Warning,
                    contentDescription = null,
                    tint = color
                )
            }

            Spacer(Modifier.width(12.dp))

            Column {
                Text(
                    titulo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    "Almacenamiento actual: ${silo.percentage}%",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    "Grano: ${silo.grain}",
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    "Granja: ${silo.farm}",
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    "Última actualización: ${silo.updatedAt}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
