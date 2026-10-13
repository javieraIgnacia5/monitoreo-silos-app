package com.example.monitoreosilos.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** Pantalla provisoria. Cada persona la reemplaza por su pantalla real. */
@Composable
fun PlaceholderScreen(
    title: String,
    actions: List<Pair<String, () -> Unit>> = emptyList()
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(title, style = MaterialTheme.typography.headlineMedium)
        Text("(pantalla en construcción)", style = MaterialTheme.typography.bodySmall)
        Spacer(Modifier.height(24.dp))
        actions.forEach { (label, onClick) ->
            Button(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth()
            ) { Text(label) }
            Spacer(Modifier.height(8.dp))
        }
    }
}