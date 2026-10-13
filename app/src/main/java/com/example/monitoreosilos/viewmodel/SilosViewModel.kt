package com.example.monitoreosilos.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.monitoreosilos.data.repository.SiloRepository
import com.example.monitoreosilos.domain.model.Silo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class SilosViewModel : ViewModel() {

    val silos: StateFlow<List<Silo>> = SiloRepository.silos

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    fun onQueryChange(value: String) {
        _query.value = value
    }

    val filtered: StateFlow<List<Silo>> = combine(SiloRepository.silos, _query) { list, q ->
        if (q.isBlank()) list
        else list.filter {
            it.name.contains(q, ignoreCase = true) || it.grain.contains(q, ignoreCase = true)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}