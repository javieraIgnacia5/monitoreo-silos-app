
package com.example.monitoreosilos.data

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.util.UUID

object SiloPhotoStorage {

    private fun photoDirectory(context: Context): File {
        return File(context.filesDir, "silo_photos").apply {
            if (!exists()) mkdirs()
        }
    }

    private fun photoFile(context: Context, siloId: String): File {
        val safeId = siloId.replace(
            Regex("[^a-zA-Z0-9_-]"),
            "_"
        )

        return File(
            photoDirectory(context),
            "silo_$safeId.jpg"
        )
    }

    // Crear archivo temporal para la cámara
    fun createCameraPhoto(
        context: Context
    ): Pair<File, Uri> {

        val file = File(
            photoDirectory(context),
            "camera_${UUID.randomUUID()}.jpg"
        )

        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        return file to uri
    }

    // Guardar fotografía asociada a un silo
    fun savePhoto(
        context: Context,
        siloId: String,
        sourceUri: Uri
    ): Boolean {

        return try {
            val destination = photoFile(context, siloId)
            val temporary = File.createTempFile(
                "saving_",
                ".jpg",
                photoDirectory(context)
            )

            try {
                context.contentResolver
                    .openInputStream(sourceUri)
                    ?.use { input ->
                        temporary.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    } ?: return false

                if (destination.exists() && !destination.delete()) {
                    return false
                }

                temporary.copyTo(destination, overwrite = true)
                true
            } finally {
                temporary.delete()
            }

        } catch (e: Exception) {
            false
        }
    }

    // Obtener fotografía guardada
    fun getPhoto(
        context: Context,
        siloId: String
    ): File? {

        val file = photoFile(context, siloId)

        return if (file.exists()) file else null
    }

    // Eliminar fotografía personalizada
    fun deletePhoto(
        context: Context,
        siloId: String
    ): Boolean {

        val file = photoFile(context, siloId)

        return !file.exists() || file.delete()
    }
}
