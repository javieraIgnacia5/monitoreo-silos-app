package com.example.monitoreosilos.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LoginScreen(onLogin: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Monitoreo de Silos", style = MaterialTheme.typography.displaySmall)
        Spacer(Modifier.height(24.dp))
        Button(onClick = onLogin) { Text("Ingresar") }
    }
}

@Composable
fun AlertsScreen() = CenterText("Alertas (próximamente)")

@Composable
fun SummaryScreen() = CenterText("Resumen jefatura (próximamente)")

@Composable
private fun CenterText(text: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(text) }
}