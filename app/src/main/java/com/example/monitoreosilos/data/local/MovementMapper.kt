
package com.example.monitoreosilos.data.local

import com.example.monitoreosilos.domain.model.Movement
import com.example.monitoreosilos.domain.model.MovementType

fun Movement.toEntity(): MovementEntity {
    return MovementEntity(
        id = id.toLong(),
        siloId = siloId,
        siloName = siloName,
        grain = grain,
        type = type.name,
        tons = tons,
        date = date,
        notes = notes
    )
}

fun MovementEntity.toDomain(): Movement {
    return Movement(
        id = id,
        siloId = siloId,
        siloName = siloName,
        grain = grain,
        type = MovementType.valueOf(type),
        tons = tons,
        date = date,
        notes = notes
    )
}
