package com.example.monitoreosilos.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monitoreosilos.data.repository.SiloRepository
import com.example.monitoreosilos.domain.model.Silo
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class SiloDetailViewModel : ViewModel() {

    fun silo(id: String): StateFlow<Silo?> =
        SiloRepository.silos
            .map { list -> list.firstOrNull { it.id == id } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SiloRepository.siloById(id))
}