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
import com.example.monitoreosilos.ui.screens.HistoryScreen
import com.example.monitoreosilos.ui.screens.HomeScreen
import com.example.monitoreosilos.ui.screens.LoginScreen
import com.example.monitoreosilos.ui.screens.MovementScreen
import com.example.monitoreosilos.ui.screens.PlaceholderScreen
import com.example.monitoreosilos.ui.screens.RegisterScreen
import com.example.monitoreosilos.ui.screens.SiloDetailScreen
import com.example.monitoreosilos.ui.screens.SilosScreen
import com.example.monitoreosilos.ui.screens.SplashScreen
import com.example.monitoreosilos.ui.screens.AlertsScreen
import com.example.monitoreosilos.ui.screens.AddSiloScreen

object Routes {
    // Pantallas 1 a 8 del collage
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val SILOS = "silos"
    const val ADD_SILO = "add_silo"
    const val DETAIL = "detail"
    const val MOVEMENT = "movement"
    const val HISTORY = "history"

    // Pantallas 9 a 16 del collage
    const val ALERTS = "alerts"
    const val LOCATION = "location"
    const val CAMERA = "camera"
    const val STATS = "stats"
    const val PROFILE = "profile"
    const val SETTINGS = "settings"
    const val STORAGE = "storage"

    const val DETAIL_ROUTE = "detail/{siloId}"
    const val MOVEMENT_ROUTE = "movement?siloId={siloId}"
    fun detail(id: String) = "detail/$id"
    fun movement(id: String = "") = "movement?siloId=$id"
}

private data class BottomItem(val route: String, val title: String, val icon: ImageVector)

private val bottomItems = listOf(
    BottomItem(Routes.HOME, "Inicio", Icons.Filled.Home),
    BottomItem(Routes.SILOS, "Silos", Icons.Filled.Inventory2),
    BottomItem(Routes.HISTORY, "Movimientos", Icons.Filled.SwapHoriz),
    BottomItem(Routes.ALERTS, "Alertas", Icons.Filled.Notifications),
    BottomItem(Routes.PROFILE, "Perfil", Icons.Filled.Person)
)

private fun NavController.goTo(route: String) = navigate(route)

/** Qué pestaña de la barra inferior queda marcada según la pantalla actual. null = no se muestra la barra. */
private fun tabFor(route: String?): String? = when {
    route == null -> null
    bottomItems.any { it.route == route } -> route
    route.startsWith(Routes.DETAIL) -> Routes.SILOS
    route.startsWith(Routes.MOVEMENT) -> Routes.HISTORY
    else -> null
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val activeTab = tabFor(currentRoute)

    Scaffold(
        bottomBar = {
            if (activeTab != null) {
                NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                    bottomItems.forEach { item ->
                        NavigationBarItem(
                            selected = activeTab == item.route,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(item.icon, contentDescription = item.title) },
                            label = { Text(item.title) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primary
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
            modifier = Modifier.padding(padding).consumeWindowInsets(padding)
        ) {
            // ---------- Pantallas 1 a 8 ----------
            composable(Routes.SPLASH) {
                SplashScreen(onFinished = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                })
            }
            composable(Routes.LOGIN) {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    },
                    onRegisterClick = { navController.goTo(Routes.REGISTER) }
                )
            }
            composable(Routes.REGISTER) {
                RegisterScreen(
                    onBack = { navController.popBackStack() },
                    onSuccess = { navController.popBackStack() }
                )
            }
            composable(Routes.HOME) {
                HomeScreen(
                    onSiloClick = { navController.goTo(Routes.detail(it)) },
                    onSeeAll = { navController.goTo(Routes.SILOS) },
                    onBellClick = { navController.goTo(Routes.ALERTS) }
                )
            }
            composable(Routes.SILOS) {
                SilosScreen(
                    onSiloClick = { navController.goTo(Routes.detail(it)) },
                    onAddClick = { navController.goTo(Routes.ADD_SILO) }
                )
            }
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
            composable(
                Routes.DETAIL_ROUTE,
                arguments = listOf(navArgument("siloId") { type = NavType.StringType })
            ) { entry ->
                SiloDetailScreen(
                    siloId = entry.arguments?.getString("siloId") ?: "",
                    onBack = { navController.popBackStack() },
                    onRegister = { navController.goTo(Routes.movement(it)) },
                    onLocation = { navController.goTo(Routes.LOCATION) }
                )
            }
            composable(
                Routes.MOVEMENT_ROUTE,
                arguments = listOf(navArgument("siloId") {
                    type = NavType.StringType
                    defaultValue = ""
                })
            ) { entry ->
                MovementScreen(
                    initialSiloId = entry.arguments?.getString("siloId") ?: "",
                    onBack = { navController.popBackStack() },
                    onSaved = {
                        navController.navigate(Routes.HISTORY) {
                            popUpTo(Routes.HOME)
                        }
                    }
                )
            }
            composable(Routes.HISTORY) { HistoryScreen() }

            // ---------- Pantallas 9 a 16 (provisorias) ----------

            composable(Routes.ALERTS) {
                AlertsScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Routes.LOCATION) {
                PlaceholderScreen(
                    "10. Ubicación del silo",
                    listOf("Volver" to { navController.popBackStack() })
                )
            }
            composable(Routes.CAMERA) {
                PlaceholderScreen(
                    "11. Cámara",
                    listOf("Volver" to { navController.popBackStack() })
                )
            }
            composable(Routes.STATS) {
                PlaceholderScreen(
                    "12. Estadísticas",
                    listOf("Volver" to { navController.popBackStack() })
                )
            }
            composable(Routes.PROFILE) {
                PlaceholderScreen(
                    "14. Perfil",
                    listOf(
                        "Estadísticas" to { navController.goTo(Routes.STATS) },
                        "Configuración" to { navController.goTo(Routes.SETTINGS) }
                    )
                )
            }
            composable(Routes.SETTINGS) {
                PlaceholderScreen(
                    "15. Configuración",
                    listOf(
                        "Almacenamiento local" to { navController.goTo(Routes.STORAGE) },
                        "Volver" to { navController.popBackStack() }
                    )
                )
            }
            composable(Routes.STORAGE) {
                PlaceholderScreen(
                    "16. Almacenamiento local",
                    listOf("Volver" to { navController.popBackStack() })
                )
            }
        }
    }
}