
package com.example.monitoreosilos.ui.navigation

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

import com.example.monitoreosilos.ui.screens.AddSiloScreen
import com.example.monitoreosilos.ui.screens.AlertsScreen
import com.example.monitoreosilos.ui.screens.EditSiloScreen
import com.example.monitoreosilos.ui.screens.HistoryScreen
import com.example.monitoreosilos.ui.screens.HomeScreen
import com.example.monitoreosilos.ui.screens.LoginScreen
import com.example.monitoreosilos.ui.screens.MovementScreen
import com.example.monitoreosilos.ui.screens.PlaceholderScreen
import com.example.monitoreosilos.ui.screens.RegisterScreen
import com.example.monitoreosilos.ui.screens.SiloDetailScreen
import com.example.monitoreosilos.ui.screens.SiloLocationScreen
import com.example.monitoreosilos.ui.screens.SilosScreen
import com.example.monitoreosilos.ui.screens.SplashScreen
import com.example.monitoreosilos.ui.screens.SiloCameraScreen
import com.example.monitoreosilos.ui.screens.StatisticsScreen
import com.example.monitoreosilos.ui.screens.ProfileScreen
import com.example.monitoreosilos.data.repository.UserRepository
import com.example.monitoreosilos.ui.screens.SettingsScreen
import com.example.monitoreosilos.ui.screens.StorageScreen
object Routes {

    // Pantallas principales
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val SILOS = "silos"

    // Gestión de silos
    const val ADD_SILO = "add_silo"
    const val EDIT_SILO = "edit_silo"
    const val EDIT_SILO_ROUTE = "edit_silo/{siloId}"

    fun editSilo(id: String) = "edit_silo/$id"

    const val DETAIL = "detail"
    const val DETAIL_ROUTE = "detail/{siloId}"

    fun detail(id: String) = "detail/$id"

    // Movimientos
    const val MOVEMENT = "movement"
    const val MOVEMENT_ROUTE = "movement?siloId={siloId}"

    fun movement(id: String = "") = "movement?siloId=$id"

    const val HISTORY = "history"

    // Alertas
    const val ALERTS = "alerts"

    // Ubicación de silos
    const val LOCATION = "location"
    const val LOCATION_ROUTE = "location/{siloId}"

    fun location(id: String) = "location/$id"

    const val CAMERA = "camera"
    const val CAMERA_ROUTE = "camera/{siloId}"

    fun camera(id: String) = "camera/$id"
    const val STATS = "stats"
    const val PROFILE = "profile"
    const val SETTINGS = "settings"
    const val STORAGE = "storage"
}

private data class BottomItem(
    val route: String,
    val title: String,
    val icon: ImageVector
)

private val bottomItems = listOf(
    BottomItem(
        Routes.HOME,
        "Inicio",
        Icons.Filled.Home
    ),
    BottomItem(
        Routes.SILOS,
        "Silos",
        Icons.Filled.Inventory2
    ),
    BottomItem(
        Routes.HISTORY,
        "Movimientos",
        Icons.Filled.SwapHoriz
    ),
    BottomItem(
        Routes.ALERTS,
        "Alertas",
        Icons.Filled.Notifications
    ),
    BottomItem(
        Routes.PROFILE,
        "Perfil",
        Icons.Filled.Person
    )
)

private fun NavController.goTo(route: String) = navigate(route)

private fun tabFor(route: String?): String? = when {
    route == null -> null
    bottomItems.any { it.route == route } -> route
    route.startsWith(Routes.DETAIL) -> Routes.SILOS
    route.startsWith(Routes.EDIT_SILO) -> Routes.SILOS
    route.startsWith(Routes.LOCATION) -> Routes.SILOS
    route.startsWith(Routes.MOVEMENT) -> Routes.HISTORY
    else -> null
}

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    val backStackEntry by
    navController.currentBackStackEntryAsState()

    val currentRoute = backStackEntry?.destination?.route

    val activeTab = tabFor(currentRoute)

    Scaffold(
        bottomBar = {
            if (activeTab != null) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    bottomItems.forEach { item ->

                        NavigationBarItem(
                            selected = activeTab == item.route,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(
                                        navController.graph
                                            .findStartDestination().id
                                    ) {
                                        saveState = true
                                    }

                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    item.icon,
                                    contentDescription = item.title
                                )
                            },
                            label = {
                                Text(item.title)
                            },
                            colors =
                                NavigationBarItemDefaults.colors(
                                    selectedIconColor =
                                        MaterialTheme.colorScheme.onPrimary,
                                    selectedTextColor =
                                        MaterialTheme.colorScheme.primary,
                                    indicatorColor =
                                        MaterialTheme.colorScheme.primary
                                )
                        )
                    }
                }
            }
        }
    ) { padding ->

        NavHost(
            navController = navController,
            startDestination = Routes.SPLASH,
            modifier = Modifier
                .padding(padding)
                .consumeWindowInsets(padding)
        ) {

            // ---------- Splash ----------

            composable(Routes.SPLASH) {
                SplashScreen(
                    onFinished = {

                        navController.navigate(Routes.LOGIN) {
                            popUpTo(navController.graph.id) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }

                    }
                )
            }

            // ---------- Inicio de sesión ----------

            composable(Routes.LOGIN) {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.LOGIN) {
                                inclusive = true
                            }
                        }
                    },
                    onRegisterClick = {
                        navController.goTo(Routes.REGISTER)
                    }
                )
            }

            // ---------- Registro ----------

            composable(Routes.REGISTER) {
                RegisterScreen(
                    onBack = {
                        navController.popBackStack()
                    },
                    onSuccess = {
                        navController.popBackStack()
                    }
                )
            }

            // ---------- Inicio ----------

            composable(Routes.HOME) {
                HomeScreen(
                    onSiloClick = {
                        navController.goTo(Routes.detail(it))
                    },
                    onSeeAll = {
                        navController.goTo(Routes.SILOS)
                    },
                    onBellClick = {
                        navController.goTo(Routes.ALERTS)
                    }
                )
            }

            // ---------- Lista de silos ----------

            composable(Routes.SILOS) {
                SilosScreen(
                    onSiloClick = {
                        navController.goTo(Routes.detail(it))
                    },
                    onAddClick = {
                        navController.goTo(Routes.ADD_SILO)
                    }
                )
            }

            // ---------- Agregar silo ----------

            composable(Routes.ADD_SILO) {
                AddSiloScreen(
                    onBack = {
                        navController.popBackStack()
                    },
                    onSaved = { _, _, _, _, _ ->
                        navController.popBackStack()
                    }
                )
            }

            // ---------- Detalle del silo ----------

            composable(
                route = Routes.DETAIL_ROUTE,
                arguments = listOf(
                    navArgument("siloId") {
                        type = NavType.StringType
                    }
                )
            ) { entry ->

                SiloDetailScreen(
                    siloId =
                        entry.arguments?.getString("siloId") ?: "",
                    onBack = {
                        navController.popBackStack()
                    },
                    onRegister = {
                        navController.goTo(Routes.movement(it))
                    },
                    onLocation = {
                        val id =
                            entry.arguments?.getString("siloId") ?: ""

                        navController.goTo(Routes.location(id))
                    },

                    onCamera = {
                        val id = entry.arguments?.getString("siloId") ?: ""
                        navController.goTo(Routes.camera(id))
                    },

                    onEdit = {
                        navController.goTo(Routes.editSilo(it))
                    },
                    onDeleted = {
                        navController.popBackStack()
                    }
                )
            }

            // ---------- Editar silo ----------

            composable(
                route = Routes.EDIT_SILO_ROUTE,
                arguments = listOf(
                    navArgument("siloId") {
                        type = NavType.StringType
                    }
                )
            ) { entry ->

                EditSiloScreen(
                    siloId =
                        entry.arguments?.getString("siloId") ?: "",
                    onBack = {
                        navController.popBackStack()
                    },
                    onSaved = {
                        navController.popBackStack()
                    }
                )
            }

            // ---------- Ubicación manual y GPS ----------

            composable(
                route = Routes.LOCATION_ROUTE,
                arguments = listOf(
                    navArgument("siloId") {
                        type = NavType.StringType
                    }
                )
            ) { entry ->

                SiloLocationScreen(
                    siloId =
                        entry.arguments?.getString("siloId") ?: "",
                    onBack = {
                        navController.popBackStack()
                    },
                    onSaved = {
                        navController.popBackStack()
                    }
                )
            }

            // ---------- Registrar movimiento ----------

            composable(
                route = Routes.MOVEMENT_ROUTE,
                arguments = listOf(
                    navArgument("siloId") {
                        type = NavType.StringType
                        defaultValue = ""
                    }
                )
            ) { entry ->

                MovementScreen(
                    initialSiloId =
                        entry.arguments?.getString("siloId") ?: "",
                    onBack = {
                        navController.popBackStack()
                    },
                    onSaved = {
                        navController.navigate(Routes.HISTORY) {
                            popUpTo(Routes.HOME)
                        }
                    }
                )
            }

            // ---------- Historial ----------

            composable(Routes.HISTORY) {
                HistoryScreen()
            }

            // ---------- Alertas ----------

            composable(Routes.ALERTS) {
                AlertsScreen(
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }

            // ---------- Cámara (pendiente) ----------


            composable(
                route = Routes.CAMERA_ROUTE,
                arguments = listOf(
                    navArgument("siloId") {
                        type = NavType.StringType
                    }
                )
            ) { entry ->

                SiloCameraScreen(
                    siloId = entry.arguments?.getString("siloId") ?: "",
                    onBack = {
                        navController.popBackStack()
                    },
                    onSaved = {
                        navController.popBackStack()
                    }
                )
            }


            // ---------- Estadísticas (pendiente) ----------


            composable(Routes.STATS) {
                StatisticsScreen(
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }



            // ---------- Perfil de usuario ----------

            composable(Routes.PROFILE) {
                ProfileScreen(
                    onStatistics = {
                        navController.goTo(Routes.STATS)
                    },
                    onSettings = {
                        navController.goTo(Routes.SETTINGS)
                    },
                    onLogout = {
                        UserRepository.logout()

                        navController.navigate(Routes.LOGIN) {
                            popUpTo(Routes.HOME) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                )
            }


            // ---------- Configuración (pendiente) ----------


            composable(Routes.SETTINGS) {
                SettingsScreen(
                    onBack = {
                        navController.popBackStack()
                    },
                    onStorage = {
                        navController.goTo(Routes.STORAGE)
                    }
                )
            }


            // ---------- Almacenamiento local (pendiente) ----------


            composable(Routes.STORAGE) {
                StorageScreen(
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }

        }
    }
}
