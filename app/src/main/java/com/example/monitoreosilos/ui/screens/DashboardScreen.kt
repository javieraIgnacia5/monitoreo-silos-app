package com.example.monitoreosilos.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.monitoreosilos.data.SampleData
import com.example.monitoreosilos.ui.components.SiloCard

@Composable
fun DashboardScreen() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SampleData.farms.forEach { farm ->
            item {
                Text(farm.name, style = MaterialTheme.typography.headlineSmall)
                Text(farm.location, style = MaterialTheme.typography.bodyMedium)
            }
            items(farm.silos) { silo -> SiloCard(silo) }
        }
    }
}