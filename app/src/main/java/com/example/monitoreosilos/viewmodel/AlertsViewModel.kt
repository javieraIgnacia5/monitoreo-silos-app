
package com.example.monitoreosilos.viewmodel

import androidx.lifecycle.ViewModel
import com.example.monitoreosilos.data.repository.SiloRepository
import com.example.monitoreosilos.domain.model.Silo
import kotlinx.coroutines.flow.StateFlow

class AlertsViewModel : ViewModel() {

    val silos: StateFlow<List<Silo>> = SiloRepository.silos

}
