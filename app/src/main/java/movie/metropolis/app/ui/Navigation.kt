package movie.metropolis.app.ui

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import movie.metropolis.app.screen.Route
import movie.metropolis.app.screen.setup.component.rememberOneTapSaving
import movie.metropolis.app.screen.setup.component.requestOneTapAsState
import movie.metropolis.app.ui.setup.SetupScreen
import movie.metropolis.app.ui.setup.SetupViewModel

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun Navigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    SharedTransitionLayout(modifier = modifier) {
        val setupVm = hiltViewModel<SetupViewModel>()
        LaunchedEffect(setupVm) {
            setupVm.requiresSetup.collect {
                val route = if (it) Route.Setup() else Route.Home()
                navController.navigate(route) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        inclusive = true
                    }
                }
            }
        }
        NavHost(
            modifier = Modifier.fillMaxSize(),
            navController = navController,
            startDestination = Route.None.route
        ) {
            composable(Route.None.route) { Text("None") }
            composable(Route.Setup.route) {
                val scope = rememberCoroutineScope()
                val saving = rememberOneTapSaving()
                val oneTap by requestOneTapAsState()
                LaunchedEffect(oneTap) {
                    val ot = oneTap
                    if (ot != null) {
                        setupVm.state.email = ot.userName
                        setupVm.state.password = ot.password.concatToString()
                    }
                }
                fun navigateHome() {
                    navController.navigate(Route.Home()) {
                        popUpTo(Route.Setup.route) {
                            inclusive = true
                        }
                    }
                }
                SetupScreen(
                    state = setupVm.state,
                    onLoginClick = {
                        scope.launch {
                            if (!setupVm.login().await()) {
                                return@launch
                            }
                            if (
                                oneTap?.userName != setupVm.state.email ||
                                oneTap?.password?.concatToString() != setupVm.state.password
                            ) {
                                saving.save(setupVm.state.email, setupVm.state.password)
                            }
                            navigateHome()
                        }
                    },
                    onExitClick = ::navigateHome
                )
            }
            composable(Route.Upcoming.route) { Text("Upcoming") }
            composable(Route.Home.route) { Text("Home") }
            composable(Route.UserEditor.route) { Text("UserEditor") }
            composable(Route.Movie.route) { Text("Movie") }
            composable(Route.Booking.Movie.route) { Text("Booking.Movie") }
            composable(Route.Booking.Cinema.route) { Text("Booking.Cinema") }
            composable(Route.OrderComplete.route) { Text("OrderComplete") }
            composable(Route.Order.route) { Text("Order") }
            composable(Route.Settings.route) { Text("Settings") }
        }
    }
}