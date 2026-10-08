package com.example.monitoreosilos.domain.model

enum class UserRole(val label: String) {
    OPERARIO("Operario"),
    SUPERVISOR("Supervisor"),
    JEFATURA("Jefatura")
}

enum class SiloStatus(val label: String) {
    NORMAL("Normal"),
    WARNING("Advertencia"),
    CRITICAL("Crítico")
}

data class Silo(
    val id: String,
    val farmId: String,
    val name: String,
    val capacityKg: Int,
    val currentKg: Int,
    val updatedAt: String
) {
    val percentage: Int
        get() = if (capacityKg == 0) 0 else (currentKg * 100) / capacityKg

    val status: SiloStatus
        get() = when {
            percentage <= 15 -> SiloStatus.CRITICAL
            percentage <= 30 -> SiloStatus.WARNING
            else -> SiloStatus.NORMAL
        }
}

data class Farm(
    val id: String,
    val name: String,
    val location: String,
    val silos: List<Silo>
)