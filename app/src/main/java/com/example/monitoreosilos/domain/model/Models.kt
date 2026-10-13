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

enum class MovementType(val label: String) {
    INGRESO("Ingreso"),
    SALIDA("Salida")
}

data class Silo(
    val id: String,
    val name: String,
    val grain: String,
    val farm: String,
    val capacityTon: Int,
    val currentTon: Double,
    val updatedAt: String
) {
    val percentage: Int
        get() = if (capacityTon == 0) 0 else ((currentTon * 100) / capacityTon).toInt()

    val status: SiloStatus
        get() = when {
            percentage <= 20 -> SiloStatus.CRITICAL
            percentage <= 50 -> SiloStatus.WARNING
            else -> SiloStatus.NORMAL
        }
}

data class Movement(
    val id: Long,
    val siloId: String,
    val siloName: String,
    val grain: String,
    val type: MovementType,
    val tons: Double,
    val date: String,
    val notes: String
)