
package com.example.monitoreosilos.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monitoreosilos.data.repository.SiloRepository
import com.example.monitoreosilos.domain.model.MovementType
import com.example.monitoreosilos.domain.validator.MovementValidator
import com.example.monitoreosilos.util.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MovementUiState(
    val siloId: String = "",
    val type: MovementType = MovementType.INGRESO,
    val tons: String = "",
    val date: String = DateUtils.today(),
    val grain: String = "",
    val notes: String = "",
    val siloError: String? = null,
    val tonsError: String? = null,
    val dateError: String? = null,
    val grainError: String? = null,
    val success: Boolean = false,
    val isSaving: Boolean = false,
    val saveError: String? = null
)

class MovementViewModel : ViewModel() {

    private val _state = MutableStateFlow(MovementUiState())
    val state: StateFlow<MovementUiState> = _state.asStateFlow()

    // ---------- Selección de silo ----------

    fun preselect(siloId: String) {
        if (_state.value.siloId.isNotEmpty() || siloId.isBlank()) {
            return
        }

        val silo = SiloRepository.siloById(siloId) ?: return

        _state.update {
            it.copy(
                siloId = siloId,
                grain = silo.grain
            )
        }
    }

    fun onSiloChange(id: String) {
        val silo = SiloRepository.siloById(id)

        _state.update {
            it.copy(
                siloId = id,
                grain = silo?.grain ?: it.grain,
                siloError = null
            )
        }
    }

    // ---------- Tipo de movimiento ----------

    fun onTypeChange(type: MovementType) {
        _state.update {
            it.copy(
                type = type,
                tonsError = if (it.tonsError != null) {
                    MovementValidator.validateTons(
                        it.tons,
                        type,
                        SiloRepository.siloById(it.siloId)
                    )
                } else {
                    null
                }
            )
        }
    }

    // ---------- Cantidad de toneladas ----------

    fun onTonsChange(value: String) {
        _state.update {
            it.copy(
                tons = value,
                tonsError = if (it.tonsError != null) {
                    MovementValidator.validateTons(
                        value,
                        it.type,
                        SiloRepository.siloById(it.siloId)
                    )
                } else {
                    null
                }
            )
        }
    }

    // ---------- Fecha ----------

    fun onDateChange(value: String) {
        _state.update {
            it.copy(
                date = value,
                dateError = MovementValidator.validateDate(value)
            )
        }
    }

    // ---------- Tipo de grano ----------

    fun onGrainChange(value: String) {
        _state.update {
            it.copy(
                grain = value,
                grainError = null
            )
        }
    }

    // ---------- Observaciones ----------

    fun onNotesChange(value: String) {
        _state.update {
            it.copy(notes = value)
        }
    }

    // ---------- Registrar movimiento ----------

    fun onSubmit() {

        // Evitar registros duplicados.
        if (_state.value.isSaving) {
            return
        }

        // Esperar a que Room termine de cargar.
        if (!SiloRepository.isReady.value) {
            Log.w(
                "MovementViewModel",
                "No se puede registrar: Room todavía está cargando"
            )
            return
        }

        val s = _state.value
        val silo = SiloRepository.siloById(s.siloId)

        // Validar los campos.
        val siloError = MovementValidator.validateSilo(
            s.siloId
        )

        val tonsError = MovementValidator.validateTons(
            s.tons,
            s.type,
            silo
        )

        val dateError = MovementValidator.validateDate(
            s.date
        )

        val grainError = MovementValidator.validateGrain(
            s.grain
        )

        val ok = listOf(
            siloError,
            tonsError,
            dateError,
            grainError
        ).all { it == null }

        // Mostrar errores de validación si existen.
        _state.update {
            it.copy(
                siloError = siloError,
                tonsError = tonsError,
                dateError = dateError,
                grainError = grainError,
                success = false
            )
        }

        if (!ok) {
            return
        }

        // Bloquear nuevos registros mientras se guarda.
        _state.update {
            it.copy(
                isSaving = true,
                saveError = null
            )
        }

        viewModelScope.launch {
            try {

                // Registrar el movimiento en Room.
                SiloRepository.registerMovement(
                    siloId = s.siloId,
                    type = s.type,
                    tons = s.tons.trim()
                        .replace(',', '.')
                        .toDouble(),
                    date = "${s.date.trim()} - ${DateUtils.time()}",
                    notes = s.notes.trim(),
                    grain = s.grain
                )

                // Confirmar que el movimiento fue guardado.
                _state.update {
                    it.copy(
                        success = true,
                        isSaving = false
                    )
                }

                Log.d(
                    "MovementViewModel",
                    "Movimiento registrado correctamente"
                )

            } catch (e: Exception) {

                Log.e(
                    "MovementViewModel",
                    "Error al registrar movimiento",
                    e
                )

                _state.update {
                    it.copy(
                        success = false,
                        isSaving = false,
                        saveError = e.message
                            ?: "No se pudo guardar el movimiento. Inténtalo nuevamente."
                    )
                }
            }
        }
    }
}
