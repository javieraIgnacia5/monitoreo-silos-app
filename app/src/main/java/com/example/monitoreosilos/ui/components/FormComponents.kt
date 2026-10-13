package com.example.monitoreosilos.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun FieldLabel(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.Medium,
        modifier = Modifier.padding(bottom = 4.dp)
    )
}

@Composable
fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = MaterialTheme.colorScheme.surface,
    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
    unfocusedBorderColor = MaterialTheme.colorScheme.outline
)

/** Lista desplegable. options = pares (valor, texto a mostrar). */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectField(
    label: String?,
    selected: String,
    placeholder: String,
    options: List<Pair<String, String>>,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    error: String? = null
) {
    var expanded by remember { mutableStateOf(false) }
    Column(modifier) {
        if (label != null) FieldLabel(label)
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
            OutlinedTextField(
                value = selected,
                onValueChange = {},
                readOnly = true,
                placeholder = { Text(placeholder) },
                isError = error != null,
                trailingIcon = {
                    if (error != null) {
                        Icon(Icons.Filled.Error, "Error", tint = MaterialTheme.colorScheme.error)
                    } else {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                    }
                },
                supportingText = if (error != null) { { Text(error) } } else null,
                shape = MaterialTheme.shapes.medium,
                colors = fieldColors(),
                modifier = Modifier
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth()
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
}