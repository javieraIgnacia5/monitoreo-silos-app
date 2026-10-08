package com.example.monitoreosilos.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.monitoreosilos.domain.model.Silo
import com.example.monitoreosilos.domain.model.SiloStatus
import com.example.monitoreosilos.ui.theme.StatusCritical
import com.example.monitoreosilos.ui.theme.StatusNormal
import com.example.monitoreosilos.ui.theme.StatusWarning

fun SiloStatus.color(): Color = when (this) {
    SiloStatus.NORMAL -> StatusNormal
    SiloStatus.WARNING -> StatusWarning
    SiloStatus.CRITICAL -> StatusCritical
}

@Composable
fun SiloCard(
    silo: Silo,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    var started by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { started = true }

    val progress by animateFloatAsState(
        targetValue = if (started) silo.percentage / 100f else 0f,
        animationSpec = tween(durationMillis = 900),
        label = "silo-progress"
    )

    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        silo.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "${silo.currentKg} / ${silo.capacityKg} kg",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Text(
                    "${silo.percentage}%",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = silo.status.color()
                )
            }
            Spacer(Modifier.height(10.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(8.dp),
                color = silo.status.color(),
                trackColor = MaterialTheme.colorScheme.outline
            )
            Spacer(Modifier.height(8.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    silo.status.label,
                    style = MaterialTheme.typography.labelMedium,
                    color = silo.status.color(),
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    "Actualizado: ${silo.updatedAt}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}