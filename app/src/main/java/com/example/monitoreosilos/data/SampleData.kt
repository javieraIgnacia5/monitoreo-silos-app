package com.example.monitoreosilos.data

import com.example.monitoreosilos.domain.model.Farm
import com.example.monitoreosilos.domain.model.Silo

object SampleData {
    val farms = listOf(
        Farm(
            id = "F1", name = "Granja Las Palmas", location = "Melipilla",
            silos = listOf(
                Silo("S1", "F1", "Silo 1", 20000, 17000, "06/10/2026 08:30"),
                Silo("S2", "F1", "Silo 2", 20000, 5200, "06/10/2026 08:30"),
                Silo("S3", "F1", "Silo 3", 15000, 1800, "06/10/2026 08:25")
            )
        ),
        Farm(
            id = "F2", name = "Granja El Roble", location = "San Pedro",
            silos = listOf(
                Silo("S4", "F2", "Silo 1", 25000, 21000, "06/10/2026 08:10"),
                Silo("S5", "F2", "Silo 2", 25000, 7000, "06/10/2026 08:10"),
                Silo("S6", "F2", "Silo 3", 18000, 2400, "06/10/2026 08:05")
            )
        )
    )

    val allSilos: List<Silo> get() = farms.flatMap { it.silos }
}