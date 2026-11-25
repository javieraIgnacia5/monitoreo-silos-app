package com.example.monitoreosilos.data.remote

import com.example.monitoreosilos.domain.model.Silo
import retrofit2.Response
import retrofit2.http.*

interface SiloApi {

    // Consultar todos los silos
    @GET("api/silos")
    suspend fun obtenerSilos(): List<Silo>

    // Consultar un silo por ID
    @GET("api/silos/{id}")
    suspend fun obtenerSilo(
        @Path("id") id: String
    ): Response<Silo>

    // Registrar un silo
    @POST("api/silos")
    suspend fun crearSilo(
        @Body silo: Silo
    ): Response<Silo>

    // Actualizar un silo
    @PUT("api/silos/{id}")
    suspend fun actualizarSilo(
        @Path("id") id: String,
        @Body silo: Silo
    ): Response<Silo>

    // Eliminar un silo
    @DELETE("api/silos/{id}")
    suspend fun eliminarSilo(
        @Path("id") id: String
    ): Response<Unit>
}
