
package com.example.monitoreosilos.notifications

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.monitoreosilos.R
import com.example.monitoreosilos.domain.model.Silo

object SiloNotificationHelper {

    private const val CANAL_ID = "alertas_silos"

    fun mostrarAlertaCritica(context: Context, silo: Silo) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permiso = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            )

            if (permiso != PackageManager.PERMISSION_GRANTED) {
                return
            }
        }

        val notificacion = NotificationCompat.Builder(
            context,
            CANAL_ID
        )
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("⚠️ Alerta crítica: ${silo.name}")
            .setContentText(
                "El silo tiene solo ${silo.percentage}% de almacenamiento."
            )
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(
                        "${silo.name} llegó al ${silo.percentage}% de su capacidad. " +
                                "Revisa el nivel de almacenamiento."
                    )
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        val administrador = context.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager

        administrador.notify(
            silo.id.hashCode(),
            notificacion
        )
    }
}
