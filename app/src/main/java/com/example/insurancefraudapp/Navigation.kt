package com.example.insurancefraudapp

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

/**
 * Sealed class defining all navigation routes in the app.
 */
sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Dashboard : Screen("dashboard")
    data object ClaimsList : Screen("claims_list")
    data object CustomersList : Screen("customers_list")
    data object Analytics : Screen("analytics")
    data object ClaimDetails : Screen("claim_details/{claimId}") {
        fun createRoute(claimId: String) = "claim_details/$claimId"
    }
    data object Settings : Screen("settings")
}

/**
 * Central navigation graph for the Insurance Fraud App.
 * Wires all screens into a single NavHost with type-safe argument passing.
 */
@Composable
fun InsuranceFraudNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route,
        modifier = modifier
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginClick = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onClaimClick = { claimId ->
                    navController.navigate(Screen.ClaimDetails.createRoute(claimId))
                },
                onSettingsClick = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }

        composable(Screen.ClaimsList.route) {
            ClaimsListScreen(
                onClaimClick = { claimId ->
                    navController.navigate(Screen.ClaimDetails.createRoute(claimId))
                }
            )
        }

        composable(Screen.CustomersList.route) {
            CustomersListScreen()
        }

        composable(Screen.Analytics.route) {
            AnalyticsScreen()
        }

        composable(
            route = Screen.ClaimDetails.route,
            arguments = listOf(navArgument("claimId") { type = NavType.StringType })
        ) { backStackEntry ->
            val claimId = backStackEntry.arguments?.getString("claimId") ?: ""
            ClaimDetailsScreen(
                claimId = claimId,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
