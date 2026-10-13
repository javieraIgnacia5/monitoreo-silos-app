package com.example.monitoreosilos.data

import com.example.monitoreosilos.domain.model.Movement
import com.example.monitoreosilos.domain.model.MovementType
import com.example.monitoreosilos.domain.model.Silo

object SampleData {
    val silos = listOf(
        Silo("S1", "Silo 1", "Maíz", "Granja Las Palmas", 320, 240.0, "10/10/2026 14:32"),
        Silo("S2", "Silo 2", "Trigo", "Granja Las Palmas", 320, 144.0, "10/10/2026 14:32"),
        Silo("S3", "Silo 3", "Soja", "Granja El Roble", 320, 64.0, "10/10/2026 14:30"),
        Silo("S4", "Silo 4", "Avena", "Granja El Roble", 320, 288.0, "10/10/2026 14:28"),
        Silo("S5", "Silo 5", "Cebada", "Granja El Roble", 320, 200.0, "10/10/2026 14:25")
    )

    fun initialMovements() = listOf(
        Movement(4, "S2", "Silo 2", "Trigo", MovementType.INGRESO, 50.0, "28/09/2026 - 14:32", "Carga desde camión 1"),
        Movement(3, "S2", "Silo 2", "Trigo", MovementType.SALIDA, 30.0, "25/09/2026 - 10:15", "Consumo semanal"),
        Movement(2, "S2", "Silo 2", "Trigo", MovementType.INGRESO, 80.0, "20/09/2026 - 16:20", "Reposición programada"),
        Movement(1, "S2", "Silo 2", "Trigo", MovementType.SALIDA, 40.0, "18/09/2026 - 09:10", "Despacho a planta")
    )
}