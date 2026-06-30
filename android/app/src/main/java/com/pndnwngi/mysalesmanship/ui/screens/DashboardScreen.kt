package com.pndnwngi.mysalesmanship.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.AssignmentReturn
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pndnwngi.mysalesmanship.ui.components.AppBadge
import com.pndnwngi.mysalesmanship.ui.components.AppCard
import com.pndnwngi.mysalesmanship.ui.components.AppTopBar
import com.pndnwngi.mysalesmanship.ui.components.LoadingOverlay
import com.pndnwngi.mysalesmanship.ui.theme.MySalesmanshipTheme
import com.pndnwngi.mysalesmanship.usecase.DashboardData
import com.pndnwngi.mysalesmanship.usecase.DashboardOutlet
import com.pndnwngi.mysalesmanship.viewmodel.DashboardUiState
import com.pndnwngi.mysalesmanship.viewmodel.DashboardViewModel

@Composable
fun DashboardScreen(
    onNavigateToOutlets: () -> Unit,
    onNavigateToScanner: () -> Unit,
    onNavigateToSync: () -> Unit,
    onNavigateToRetur: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var isPulling by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        when (val state = uiState) {
            is DashboardUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is DashboardUiState.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = state.message,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Button(onClick = { viewModel.loadDashboard() }) {
                            Text("Coba Lagi")
                        }
                        Button(
                            onClick = {
                                isPulling = true
                                viewModel.pullData { res ->
                                    isPulling = false
                                    if (res.isSuccess) {
                                        Toast.makeText(context, "Tarik data berhasil!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, res.exceptionOrNull()?.message ?: "Tarik data gagal", Toast.LENGTH_LONG).show()
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                        ) {
                            Text("Tarik Data")
                        }
                    }
                }
            }
            is DashboardUiState.Success -> {
                DashboardContent(
                    data = state.data,
                    onNavigateToOutlets = onNavigateToOutlets,
                    onNavigateToScanner = onNavigateToScanner,
                    onNavigateToSync = onNavigateToSync,
                    onNavigateToRetur = onNavigateToRetur,
                    onPullData = {
                        isPulling = true
                        viewModel.pullData { res ->
                            isPulling = false
                            if (res.isSuccess) {
                                Toast.makeText(context, "Tarik data berhasil!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, res.exceptionOrNull()?.message ?: "Tarik data gagal", Toast.LENGTH_LONG).show()
                            }
                        }
                    },
                    onLogout = {
                        viewModel.logout(onLogout)
                    }
                )
            }
        }

        LoadingOverlay(isLoading = isPulling, message = "Menarik data...")
    }
}

@Composable
fun DashboardContent(
    data: DashboardData,
    onNavigateToOutlets: () -> Unit,
    onNavigateToScanner: () -> Unit,
    onNavigateToSync: () -> Unit,
    onNavigateToRetur: () -> Unit,
    onPullData: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showFabMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Sales Dashboard",
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Logout",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AnimatedVisibility(
                    visible = showFabMenu,
                    enter = expandVertically(),
                    exit = shrinkVertically()
                ) {
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ExtendedFloatingActionButton(
                            text = { Text("Tarik Data") },
                            icon = { Icon(Icons.Default.Download, contentDescription = null) },
                            onClick = {
                                showFabMenu = false
                                onPullData()
                            },
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        ExtendedFloatingActionButton(
                            text = { Text("Scan Barcode") },
                            icon = { Icon(Icons.Default.QrCodeScanner, contentDescription = null) },
                            onClick = {
                                showFabMenu = false
                                onNavigateToScanner()
                            },
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }

                FloatingActionButton(
                    onClick = { showFabMenu = !showFabMenu },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(
                        imageVector = if (showFabMenu) Icons.Default.Close else Icons.Default.Menu,
                        contentDescription = "Menu Aksi"
                    )
                }
            }
        },
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp)
        ) {
            item {
                AppCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                Text(
                                    text = data.salesName,
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "ID Sales: ${data.salesId}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            AppBadge(
                                text = "ONLINE",
                                containerColor = Color(0xFFC8E6C9),
                                contentColor = Color(0xFF2E7D32)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Periode Kerja:",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = data.weekPeriod,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    AppCard(
                        modifier = Modifier
                            .weight(1f)
                            .height(84.dp),
                        onClick = onNavigateToOutlets
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.List,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Daftar Outlet",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    AppCard(
                        modifier = Modifier
                            .weight(1f)
                            .height(84.dp),
                        onClick = onNavigateToSync
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Sync,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Sync Status",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    AppCard(
                        modifier = Modifier
                            .weight(1f)
                            .height(84.dp),
                        onClick = onNavigateToRetur
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.AssignmentReturn,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Ringkasan Retur",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Ringkasan Stok yang Dibawa",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    AppCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            if (data.stocks.isEmpty()) {
                                Text(
                                    text = "Tidak ada stok rokok aktif hari ini",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            } else {
                                data.stocks.forEachIndexed { index, stock ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 6.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = stock.name,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Medium,
                                            modifier = Modifier.weight(1.5f)
                                        )
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                            modifier = Modifier.weight(2f)
                                        ) {
                                            UomBadge(value = stock.qtyDusRemaining, label = "DUS")
                                            UomBadge(value = stock.qtyBalRemaining, label = "BAL")
                                            UomBadge(value = stock.qtySlfRemaining, label = "SLP")
                                            UomBadge(value = stock.qtyBksRemaining, label = "BKS")
                                        }
                                    }
                                    if (index < data.stocks.lastIndex) {
                                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Progress Achievement vs Target",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    AppCard(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            if (data.achievements.isEmpty()) {
                                Text(
                                    text = "Tidak ada sales target aktif",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            } else {
                                data.achievements.forEach { ach ->
                                    val progressFraction = if (ach.targetQty > 0) ach.soldQty.toFloat() / ach.targetQty.toFloat() else 0f
                                    val percentage = (progressFraction * 100).toInt()

                                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Row(
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                text = ach.productName,
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Medium
                                            )
                                            Text(
                                                text = "${ach.soldQty}/${ach.targetQty} Bks ($percentage%)",
                                                style = MaterialTheme.typography.bodySmall,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                        LinearProgressIndicator(
                                            progress = { progressFraction.coerceIn(0f, 1f) },
                                            color = if (percentage >= 100) Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary,
                                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                                            modifier = Modifier.fillMaxWidth().height(6.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Kunjungan Outlet Hari Ini",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            if (data.outlets.isEmpty()) {
                item {
                    Text(
                        text = "Tidak ada daftar kunjungan outlet untuk hari ini",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                val groupedOutlets = data.outlets.groupBy { it.routeName }
                groupedOutlets.forEach { (routeName, outlets) ->
                    item {
                        Text(
                            text = routeName,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }

                    items(outlets) { outlet ->
                        AppCard(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { onNavigateToOutlets() } // Navigate to outlet list detail
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = outlet.name,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = outlet.address,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                AppBadge(
                                    text = if (outlet.isVisited) "SELESAI" else "BELUM",
                                    containerColor = if (outlet.isVisited) Color(0xFFC8E6C9) else Color(0xFFFFCDD2),
                                    contentColor = if (outlet.isVisited) Color(0xFF2E7D32) else Color(0xFFC62828)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UomBadge(value: Int, label: String) {
    Surface(
        shape = RoundedCornerShape(4.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        modifier = Modifier.padding(horizontal = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$value ",
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            )
            Text(
                text = label,
                fontSize = 9.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    MySalesmanshipTheme {
        DashboardContent(
            data = DashboardData(
                salesName = "Budi Setiawan",
                salesId = "SLS-1",
                weekPeriod = "2026-W27",
                stocks = emptyList(),
                achievements = emptyList(),
                outlets = emptyList()
            ),
            onNavigateToOutlets = {},
            onNavigateToScanner = {},
            onNavigateToSync = {},
            onNavigateToRetur = {},
            onPullData = {},
            onLogout = {}
        )
    }
}
