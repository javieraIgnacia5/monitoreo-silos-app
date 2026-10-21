
package com.example.monitoreosilos.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.monitoreosilos.data.repository.SiloRepository
import com.example.monitoreosilos.ui.components.BarChart
import com.example.monitoreosilos.ui.components.CircleGauge
import com.example.monitoreosilos.ui.components.SiloHeaderImage
import com.example.monitoreosilos.ui.components.color
import com.example.monitoreosilos.util.formatTon
import com.example.monitoreosilos.viewmodel.SiloDetailViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SiloDetailScreen(
    siloId: String,
    onBack: () -> Unit,
    onRegister: (String) -> Unit,
    onLocation: () -> Unit,
    onEdit: (String) -> Unit,
    onDeleted: () -> Unit,
    viewModel: SiloDetailViewModel = viewModel()
) {
    val flow = remember(siloId) {
        viewModel.silo(siloId)
    }
    val silo by flow.collectAsState()

    var showDeleteDialog by remember {
        mutableStateOf(false)
    }
    var deleteError by remember {
        mutableStateOf<String?>(null)
    }
    var isDeleting by remember {
        mutableStateOf(false)
    }

    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            silo?.name ?: "Silo",
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            silo?.grain ?: "",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { onEdit(siloId) }
                    ) {
                        Icon(
                            Icons.Filled.Edit,
                            contentDescription = "Editar silo"
                        )
                    }

                    IconButton(onClick = onLocation) {
                        Icon(
                            Icons.Filled.LocationOn,
                            contentDescription = "Ubicación"
                        )
                    }
                }
            )
        }
    ) { padding ->

        val s = silo

        if (s == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Silo no encontrado")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Imagen del silo y medidor circular
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                ) {
                    SiloHeaderImage(
                        siloId = s.id,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                    )

                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .size(180.dp)
                            .clip(CircleShape)
                            .background(
                                MaterialTheme.colorScheme.background
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        CircleGauge(
                            percent = s.percentage,
                            color = s.status.color(),
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(6.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    "${s.percentage}%",
                                    style = MaterialTheme.typography.displaySmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "${s.currentTon.formatTon()} / ${s.capacityTon} ton",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Información del silo
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    border = BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outline
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(
                            horizontal = 16.dp,
                            vertical = 8.dp
                        )
                    ) {
                        InfoRow(
                            Icons.Filled.Thermostat,
                            "Temperatura",
                            "22 °C"
                        )

                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outline
                        )

                        InfoRow(
                            Icons.Filled.WaterDrop,
                            "Humedad",
                            "14 %"
                        )

                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outline
                        )

                        InfoRow(
                            Icons.Filled.Schedule,
                            "Última actualización",
                            s.updatedAt
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                Text(
                    "Nivel en los últimos 7 días",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(Modifier.height(8.dp))

                BarChart(
                    values = listOf(
                        0.3f,
                        0.4f,
                        0.42f,
                        0.55f,
                        0.5f,
                        0.75f,
                        s.percentage / 100f
                    ),
                    labels = listOf(
                        "1", "2", "3", "4", "V", "S", "D"
                    )
                )

                Spacer(Modifier.height(20.dp))

                // Botón para registrar movimientos
                Button(
                    onClick = {
                        onRegister(s.id)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Icon(
                        Icons.Filled.AddCircle,
                        contentDescription = null
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Registrar movimiento",
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.height(12.dp))

                // Botón para eliminar el silo
                OutlinedButton(
                    onClick = {
                        deleteError = null
                        showDeleteDialog = true
                    },
                    enabled = !isDeleting,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(
                        Icons.Filled.Delete,
                        contentDescription = null
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Eliminar silo",
                        fontWeight = FontWeight.Bold
                    )
                }

                if (deleteError != null) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        deleteError ?: "",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }

    // Ventana de confirmación de eliminación
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = {
                if (!isDeleting) {
                    showDeleteDialog = false
                }
            },
            title = {
                Text("¿Eliminar silo?")
            },
            text = {
                Text(
                    "¿Estás segura de que deseas eliminar " +
                            "\"${silo?.name ?: "este silo"}\"? " +
                            "Solo se puede eliminar si está vacío " +
                            "y no tiene movimientos registrados."
                )
            },
            confirmButton = {
                TextButton(
                    enabled = !isDeleting,
                    onClick = {
                        isDeleting = true

                        scope.launch {
                            try {
                                SiloRepository.deleteSilo(siloId)
                                showDeleteDialog = false
                                onDeleted()
                            } catch (e: Exception) {
                                deleteError = e.message
                                    ?: "No se pudo eliminar el silo."
                                showDeleteDialog = false
                            } finally {
                                isDeleting = false
                            }
                        }
                    }
                ) {
                    Text(
                        if (isDeleting) {
                            "Eliminando..."
                        } else {
                            "Eliminar"
                        },
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(
                    enabled = !isDeleting,
                    onClick = {
                        showDeleteDialog = false
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun InfoRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary
        )

        Spacer(Modifier.width(12.dp))

        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )

        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}
