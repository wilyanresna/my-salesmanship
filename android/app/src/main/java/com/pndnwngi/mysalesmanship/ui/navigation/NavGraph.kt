package com.pndnwngi.mysalesmanship.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.pndnwngi.mysalesmanship.ui.components.*
import com.pndnwngi.mysalesmanship.ui.screens.LoginScreen
import com.pndnwngi.mysalesmanship.ui.screens.DashboardScreen
import com.pndnwngi.mysalesmanship.ui.screens.OutletListScreen
import com.pndnwngi.mysalesmanship.ui.screens.VisitScreen
import com.pndnwngi.mysalesmanship.ui.screens.BarcodeScannerScreen
import com.pndnwngi.mysalesmanship.ui.screens.SyncStatusScreen
import com.pndnwngi.mysalesmanship.ui.screens.ReturSummaryScreen

@Composable
fun NavGraph(
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
                onLoginSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateToOutlets = { navController.navigate(Screen.OutletList.route) },
                onNavigateToScanner = { navController.navigate(Screen.BarcodeScanner.route) },
                onNavigateToSync = { navController.navigate(Screen.SyncStatus.route) },
                onNavigateToRetur = { navController.navigate(Screen.ReturSummary.route) },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Dashboard.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.OutletList.route) {
            OutletListScreen(
                onOutletClick = { outletId ->
                    navController.navigate(Screen.Visit.createRoute(outletId))
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.Visit.route,
            arguments = listOf(navArgument("outletId") { type = NavType.StringType })
        ) { backStackEntry ->
            val outletId = backStackEntry.arguments?.getString("outletId") ?: "unknown"
            VisitScreen(
                outletId = outletId,
                onFinishVisit = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.BarcodeScanner.route) {
            BarcodeScannerScreen(
                onBarcodeDetected = { outletId ->
                    navController.navigate(Screen.Visit.createRoute(outletId)) {
                        popUpTo(Screen.BarcodeScanner.route) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.SyncStatus.route) {
            SyncStatusScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.ReturSummary.route) {
            ReturSummaryScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}


