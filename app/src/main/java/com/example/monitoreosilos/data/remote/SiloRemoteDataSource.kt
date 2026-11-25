
package com.example.monitoreosilos.data.remote

import com.example.monitoreosilos.domain.model.Silo

class SiloRemoteDataSource(
    private val api: SiloApi = RetrofitClient.siloApi
) {

    // Obtener todos los silos desde MySQL
    suspend fun obtenerSilos(): List<Silo> {
        return api.obtenerSilos()
    }

    // Registrar un silo en MySQL
    suspend fun crearSilo(silo: Silo): Silo {
        val respuesta = api.crearSilo(silo)

        if (!respuesta.isSuccessful) {
            throw Exception(
                "Error al registrar silo: ${respuesta.code()}"
            )
        }

        return respuesta.body()
            ?: throw Exception("El servidor no devolvió el silo")
    }

    // Actualizar un silo en MySQL
    suspend fun actualizarSilo(silo: Silo): Silo {
        val respuesta = api.actualizarSilo(silo.id, silo)

        if (!respuesta.isSuccessful) {
            throw Exception(
                "Error al actualizar silo: ${respuesta.code()}"
            )
        }

        return respuesta.body()
            ?: throw Exception("El servidor no devolvió el silo")
    }

    // Eliminar un silo desde MySQL
    suspend fun eliminarSilo(id: String) {
        val respuesta = api.eliminarSilo(id)

        if (!respuesta.isSuccessful) {
            throw Exception(
                "Error al eliminar silo: ${respuesta.code()}"
            )
        }
    }
}
