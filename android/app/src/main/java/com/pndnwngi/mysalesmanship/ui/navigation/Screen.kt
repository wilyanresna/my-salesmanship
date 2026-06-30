package com.pndnwngi.mysalesmanship.ui.navigation

sealed class Screen(val route: String, val title: String) {
    data object Login : Screen("login", "Login")
    data object Dashboard : Screen("dashboard", "Dashboard")
    data object OutletList : Screen("outlet_list", "Daftar Outlet")
    data object Visit : Screen("visit/{outletId}", "Kunjungan Outlet") {
        fun createRoute(outletId: String) = "visit/$outletId"
    }
    data object BarcodeScanner : Screen("barcode_scanner", "Scan Barcode")
    data object SyncStatus : Screen("sync_status", "Status Sinkronisasi")
    data object ReturSummary : Screen("retur_summary", "Ringkasan Retur")
}
