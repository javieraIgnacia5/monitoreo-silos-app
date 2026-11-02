
package com.example.monitoreosilos.data.local

import com.example.monitoreosilos.domain.model.Silo

fun Silo.toEntity(): SiloEntity {
    return SiloEntity(
        id = id,
        name = name,
        grain = grain,
        farm = farm,
        capacityTon = capacityTon,
        currentTon = currentTon,
        updatedAt = updatedAt,

        // Guardar ubicación manual
        locationAddress = locationAddress,

        // Guardar coordenadas GPS
        latitude = latitude,
        longitude = longitude
    )
}

fun SiloEntity.toDomain(): Silo {
    return Silo(
        id = id,
        name = name,
        grain = grain,
        farm = farm,
        capacityTon = capacityTon,
        currentTon = currentTon,
        updatedAt = updatedAt,

        // Recuperar ubicación manual
        locationAddress = locationAddress,

        // Recuperar coordenadas GPS
        latitude = latitude,
        longitude = longitude
    )
}
