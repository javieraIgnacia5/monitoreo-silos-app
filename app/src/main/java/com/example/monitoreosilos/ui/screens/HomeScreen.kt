package com.example.monitoreosilos.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.monitoreosilos.data.repository.UserRepository
import com.example.monitoreosilos.domain.model.Silo
import com.example.monitoreosilos.domain.model.SiloStatus
import com.example.monitoreosilos.ui.components.CircleGauge
import com.example.monitoreosilos.ui.components.color
import com.example.monitoreosilos.viewmodel.SilosViewModel

@Composable
fun HomeScreen(
    onSiloClick: (String) -> Unit,
    onSeeAll: () -> Unit,
    onBellClick: () -> Unit,
    viewModel: SilosViewModel = viewModel()
) {
    val silos by viewModel.silos.collectAsState()
    val user by UserRepository.currentUser.collectAsState()
    val firstName = user?.name?.trim()?.split(" ")?.firstOrNull().orEmpty()

    val normal = silos.count { it.status == SiloStatus.NORMAL }
    val warning = silos.count { it.status == SiloStatus.WARNING }
    val critical = silos.count { it.status == SiloStatus.CRITICAL }
    val totalCap = silos.sumOf { it.capacityTon }
    val totalCur = silos.sumOf { it.currentTon }
    val totalPct = if (totalCap == 0) 0 else ((totalCur * 100) / totalCap).toInt()

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)
    ) {
        // Saludo y campana
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                if (firstName.isNotEmpty()) "¡Hola, $firstName! \uD83D\uDC4B" else "¡Hola! \uD83D\uDC4B",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            IconButton(onClick = onBellClick) {
                Icon(Icons.Filled.Notifications, contentDescription = "Alertas")
            }
        }
        Spacer(Modifier.height(12.dp))

        // Tarjeta del clima (dato ficticio)
        Box(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Brush.horizontalGradient(listOf(Color(0xFF2C7A8C), Color(0xFF7FB6D6))))
                .padding(16.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.LocationOn, null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Text(" Concepción", color = Color.White, style = MaterialTheme.typography.bodySmall)
                }
                Text(
                    "18°",
                    color = Color.White,
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold
                )
                Text("Parcialmente nublado", color = Color.White, style = MaterialTheme.typography.bodySmall)
            }
            Box(Modifier.align(Alignment.CenterEnd).size(84.dp)) {
                Icon(
                    Icons.Filled.WbSunny, null, tint = Color(0xFFFFC83D),
                    modifier = Modifier.size(52.dp).align(Alignment.TopEnd)
                )
                Icon(
                    Icons.Filled.Cloud, null, tint = Color.White,
                    modifier = Modifier.size(64.dp).align(Alignment.BottomStart)
                )
            }
        }

        // Estado general
        Spacer(Modifier.height(16.dp))
        Text("Estado general", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatusBox("Normales", normal, SiloStatus.NORMAL.color(), Modifier.weight(1f))
            StatusBox("Advertencia", warning, SiloStatus.WARNING.color(), Modifier.weight(1f))
            StatusBox("Críticos", critical, SiloStatus.CRITICAL.color(), Modifier.weight(1f))
        }

        // Capacidad total
        Spacer(Modifier.height(16.dp))
        Card(
            Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Column(Modifier.padding(16.dp)) {
                Text("Capacidad total", style = MaterialTheme.typography.bodySmall)
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "$totalCap toneladas",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("$totalPct%", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { totalPct / 100f },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.outline
                )
            }
        }

        // Silos: 3 tarjetas a todo el ancho
        Spacer(Modifier.height(16.dp))
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Silos", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            TextButton(onClick = onSeeAll) {
                Text("Ver todos")
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            silos.take(3).forEach { silo ->
                MiniSilo(silo, Modifier.weight(1f), onClick = { onSiloClick(silo.id) })
            }
        }
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun StatusBox(label: String, count: Int, color: Color, modifier: Modifier) {
    Column(
        modifier
            .clip(RoundedCornerShape(16.dp))
            .background(color.copy(alpha = 0.15f))
            .padding(vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("$count", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = color)
        Text(label, style = MaterialTheme.typography.labelMedium, color = color)
    }
}

@Composable
private fun MiniSilo(silo: Silo, modifier: Modifier, onClick: () -> Unit) {
    Column(
        modifier
            .clip(RoundedCornerShape(18.dp))
            .background(silo.status.color().copy(alpha = 0.13f))
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Filled.Inventory2,
            contentDescription = null,
            tint = silo.status.color(),
            modifier = Modifier.size(26.dp)
        )
        Spacer(Modifier.height(2.dp))
        Text(silo.name, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        CircleGauge(
            percent = silo.percentage,
            color = silo.status.color(),
            modifier = Modifier.size(72.dp),
            strokeWidth = 7.dp
        ) {
            Text("${silo.percentage}%", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        }
    }
}