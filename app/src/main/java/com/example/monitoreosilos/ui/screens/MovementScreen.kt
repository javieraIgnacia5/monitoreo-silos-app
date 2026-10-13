package com.example.monitoreosilos.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.monitoreosilos.data.repository.SiloRepository
import com.example.monitoreosilos.domain.model.MovementType
import com.example.monitoreosilos.ui.components.AppTextField
import com.example.monitoreosilos.viewmodel.MovementViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovementScreen(
    initialSiloId: String,
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: MovementViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val silos by SiloRepository.silos.collectAsState()
    val grains = listOf("Maíz", "Trigo", "Soja", "Avena", "Cebada")

    LaunchedEffect(initialSiloId) { viewModel.preselect(initialSiloId) }
    LaunchedEffect(state.success) { if (state.success) onSaved() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrar movimiento", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp)
        ) {
            SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
                MovementType.values().forEachIndexed { index, type ->
                    SegmentedButton(
                        selected = state.type == type,
                        onClick = { viewModel.onTypeChange(type) },
                        shape = SegmentedButtonDefaults.itemShape(index, MovementType.values().size)
                    ) { Text(type.label) }
                }
            }
            Spacer(Modifier.height(16.dp))

            Dropdown(
                label = "Silo",
                selected = silos.firstOrNull { it.id == state.siloId }?.let { "${it.name} - ${it.grain}" } ?: "",
                options = silos.map { it.id to "${it.name} - ${it.grain}" },
                error = state.siloError,
                onSelect = viewModel::onSiloChange
            )
            Spacer(Modifier.height(8.dp))

            AppTextField(
                value = state.tons,
                onValueChange = viewModel::onTonsChange,
                label = "Cantidad (toneladas)",
                leadingIcon = Icons.Filled.Scale,
                error = state.tonsError,
                keyboardType = KeyboardType.Decimal
            )
            Spacer(Modifier.height(8.dp))
            AppTextField(
                value = state.date,
                onValueChange = viewModel::onDateChange,
                label = "Fecha (dd/mm/aaaa)",
                leadingIcon = Icons.Filled.CalendarMonth,
                error = state.dateError
            )
            Spacer(Modifier.height(8.dp))

            Dropdown(
                label = "Tipo de grano",
                selected = state.grain,
                options = grains.map { it to it },
                error = state.grainError,
                onSelect = viewModel::onGrainChange
            )
            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = state.notes,
                onValueChange = viewModel::onNotesChange,
                label = { Text("Observaciones (opcional)") },
                leadingIcon = { Icon(Icons.Filled.Notes, contentDescription = null) },
                minLines = 3,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(20.dp))

            Button(
                onClick = viewModel::onSubmit,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                ),
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) { Text("Guardar movimiento", fontWeight = FontWeight.Bold) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Dropdown(
    label: String,
    selected: String,
    options: List<Pair<String, String>>,
    error: String?,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            isError = error != null,
            trailingIcon = {
                if (error != null) Icon(Icons.Filled.Error, "Error", tint = MaterialTheme.colorScheme.error)
                else ExposedDropdownMenuDefaults.TrailingIcon(expanded)
            },
            supportingText = { if (error != null) Text(error) },
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { (value, text) ->
                DropdownMenuItem(
                    text = { Text(text) },
                    onClick = { onSelect(value); expanded = false }
                )
            }
        }
    }
}