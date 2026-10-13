package com.example.monitoreosilos.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.monitoreosilos.domain.model.Silo
import com.example.monitoreosilos.domain.model.SiloStatus
import com.example.monitoreosilos.ui.theme.StatusCritical
import com.example.monitoreosilos.ui.theme.StatusNormal
import com.example.monitoreosilos.ui.theme.StatusWarning
import com.example.monitoreosilos.util.formatTon

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
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Row(
            Modifier.padding(10.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SiloThumb(silo.id, Modifier.width(72.dp).height(92.dp))
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(silo.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(silo.grain, style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier
                            .size(width = 34.dp, height = 44.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(silo.status.color()),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.Grain, contentDescription = silo.status.label, tint = Color.White)
                    }
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(
                            "${silo.percentage}%",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "${silo.currentTon.formatTon()} / ${silo.capacityTon} ton",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
        }
    }
}