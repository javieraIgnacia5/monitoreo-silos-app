
package com.example.monitoreosilos

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.monitoreosilos.data.repository.SiloRepository
import com.example.monitoreosilos.data.repository.UserRepository
import com.example.monitoreosilos.domain.model.SiloStatus
import com.example.monitoreosilos.notifications.SiloNotificationHelper
import com.example.monitoreosilos.settings.ThemeMode
import com.example.monitoreosilos.settings.ThemePreferences
import com.example.monitoreosilos.ui.navigation.AppNavigation
import com.example.monitoreosilos.ui.theme.MonitoreoSilosTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val solicitarPermiso = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { permitido ->
        if (permitido) {
            observarAlertasCriticas()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicialización de repositorios
        SiloRepository.init(this)
        UserRepository.init(this)

        // Cargar la preferencia de apariencia guardada
        ThemePreferences.init(this)

        // Configuración de notificaciones
        crearCanalNotificaciones()
        comprobarPermisoNotificaciones()

        // Observar los silos y sus alertas críticas
        observarAlertasCriticas()

        enableEdgeToEdge()

        setContent {

            val themeMode by ThemePreferences.themeMode.collectAsState()

            val darkTheme = when (themeMode) {
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
            }

            MonitoreoSilosTheme(
                darkTheme = darkTheme
            ) {
                AppNavigation()
            }
        }
    }

    private fun crearCanalNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val canal = NotificationChannel(
                "alertas_silos",
                "Alertas de silos",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description =
                    "Notificaciones sobre niveles críticos de almacenamiento"
            }

            val administrador = getSystemService(
                NotificationManager::class.java
            )

            administrador.createNotificationChannel(canal)
        }
    }

    private fun comprobarPermisoNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            val permisoConcedido = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!permisoConcedido) {
                solicitarPermiso.launch(
                    Manifest.permission.POST_NOTIFICATIONS
                )
            }
        }
    }

    private fun observarAlertasCriticas() {

        val prefsAlertas = getSharedPreferences(
            "alertas_notificadas",
            MODE_PRIVATE
        )

        lifecycleScope.launch {

            SiloRepository.silos.collect { silos ->

                silos.forEach { silo ->

                    val clave = "critico_${silo.id}"

                    if (silo.status == SiloStatus.CRITICAL) {

                        val yaNotificado = prefsAlertas.getBoolean(
                            clave,
                            false
                        )

                        if (!yaNotificado) {

                            val permisoConcedido =
                                Build.VERSION.SDK_INT <
                                        Build.VERSION_CODES.TIRAMISU ||
                                        ContextCompat.checkSelfPermission(
                                            this@MainActivity,
                                            Manifest.permission.POST_NOTIFICATIONS
                                        ) == PackageManager.PERMISSION_GRANTED

                            if (permisoConcedido) {

                                SiloNotificationHelper.mostrarAlertaCritica(
                                    this@MainActivity,
                                    silo
                                )

                                prefsAlertas.edit()
                                    .putBoolean(clave, true)
                                    .apply()
                            }
                        }

                    } else {

                        if (prefsAlertas.contains(clave)) {

                            prefsAlertas.edit()
                                .remove(clave)
                                .apply()
                        }
                    }
                }
            }
        }
    }
}
