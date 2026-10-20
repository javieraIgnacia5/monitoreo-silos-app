
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
        updatedAt = updatedAt
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
        updatedAt = updatedAt
    )
}
