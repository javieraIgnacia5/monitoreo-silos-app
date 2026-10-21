package com.example.monitoreosilos.viewmodel

import androidx.lifecycle.ViewModel
import com.example.monitoreosilos.data.repository.SiloRepository
import com.example.monitoreosilos.domain.model.MovementType
import com.example.monitoreosilos.domain.validator.MovementValidator
import com.example.monitoreosilos.util.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

import androidx.lifecycle.viewModelScope
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
    val success: Boolean = false
)

class MovementViewModel : ViewModel() {

    private val _state = MutableStateFlow(MovementUiState())
    val state: StateFlow<MovementUiState> = _state.asStateFlow()

    fun preselect(siloId: String) {
        if (_state.value.siloId.isNotEmpty() || siloId.isBlank()) return
        val silo = SiloRepository.siloById(siloId) ?: return
        _state.update { it.copy(siloId = siloId, grain = silo.grain) }
    }

    fun onSiloChange(id: String) {
        val silo = SiloRepository.siloById(id)
        _state.update { it.copy(siloId = id, grain = silo?.grain ?: it.grain, siloError = null) }
    }

    fun onTypeChange(type: MovementType) = _state.update {
        it.copy(
            type = type,
            tonsError = if (it.tonsError != null)
                MovementValidator.validateTons(it.tons, type, SiloRepository.siloById(it.siloId))
            else null
        )
    }

    fun onTonsChange(value: String) = _state.update {
        it.copy(
            tons = value,
            tonsError = if (it.tonsError != null)
                MovementValidator.validateTons(value, it.type, SiloRepository.siloById(it.siloId))
            else null
        )
    }

    fun onDateChange(value: String) = _state.update {
        it.copy(date = value, dateError = MovementValidator.validateDate(value))
    }

    fun onGrainChange(value: String) = _state.update { it.copy(grain = value, grainError = null) }

    fun onNotesChange(value: String) = _state.update { it.copy(notes = value) }


    fun onSubmit() {
        val s = _state.value
        val silo = SiloRepository.siloById(s.siloId)

        val siloError = MovementValidator.validateSilo(s.siloId)
        val tonsError = MovementValidator.validateTons(
            s.tons,
            s.type,
            silo
        )
        val dateError = MovementValidator.validateDate(s.date)
        val grainError = MovementValidator.validateGrain(s.grain)

        val ok = listOf(
            siloError,
            tonsError,
            dateError,
            grainError
        ).all { it == null }

        _state.update {
            it.copy(
                siloError = siloError,
                tonsError = tonsError,
                dateError = dateError,
                grainError = grainError,
                success = false
            )
        }

        if (!ok) return

        viewModelScope.launch {
            try {
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

                _state.update {
                    it.copy(success = true)
                }

            } catch (e: Exception) {
                android.util.Log.e(
                    "MovementViewModel",
                    "Error al registrar movimiento",
                    e
                )

                _state.update {
                    it.copy(success = false)
                }
            }
        }
    }

}