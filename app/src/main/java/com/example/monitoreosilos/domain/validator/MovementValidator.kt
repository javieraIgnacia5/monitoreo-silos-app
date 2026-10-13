package com.example.monitoreosilos.domain.validator

import com.example.monitoreosilos.domain.model.MovementType
import com.example.monitoreosilos.domain.model.Silo
import com.example.monitoreosilos.util.DateUtils
import com.example.monitoreosilos.util.formatTon
import java.util.Date

object MovementValidator {

    private val dateRegex = Regex("^\\d{2}/\\d{2}/\\d{4}$")

    fun validateSilo(siloId: String): String? =
        if (siloId.isBlank()) "Selecciona un silo" else null

    fun validateTons(text: String, type: MovementType, silo: Silo?): String? {
        if (text.isBlank()) return "Ingresa la cantidad"
        val value = text.trim().replace(',', '.').toDoubleOrNull()
            ?: return "Ingresa un número válido"
        if (value <= 0) return "Debe ser mayor a 0"
        if (silo != null) {
            when (type) {
                MovementType.INGRESO -> {
                    val free = silo.capacityTon - silo.currentTon
                    if (value > free) {
                        return "Supera la capacidad (espacio libre: ${free.formatTon()} ton)"
                    }
                }
                MovementType.SALIDA -> {
                    if (value > silo.currentTon) {
                        return "Supera el stock (disponible: ${silo.currentTon.formatTon()} ton)"
                    }
                }
            }
        }
        return null
    }

    fun validateDate(text: String): String? {
        if (text.isBlank()) return "Ingresa la fecha"
        if (!dateRegex.matches(text.trim())) return "Usa el formato dd/mm/aaaa"
        val parsed = DateUtils.parse(text.trim()) ?: return "La fecha no es válida"
        if (parsed.after(Date())) return "La fecha no puede ser futura"
        return null
    }

    fun validateGrain(grain: String): String? =
        if (grain.isBlank()) "Selecciona el tipo de grano" else null
}