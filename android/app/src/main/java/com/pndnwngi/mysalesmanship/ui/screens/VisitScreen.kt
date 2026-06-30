package com.pndnwngi.mysalesmanship.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pndnwngi.mysalesmanship.ui.components.AppButton
import com.pndnwngi.mysalesmanship.ui.components.AppCard
import com.pndnwngi.mysalesmanship.ui.components.AppTopBar
import com.pndnwngi.mysalesmanship.ui.components.LoadingOverlay
import com.pndnwngi.mysalesmanship.ui.theme.MySalesmanshipTheme
import com.pndnwngi.mysalesmanship.viewmodel.VisitUiState
import com.pndnwngi.mysalesmanship.viewmodel.VisitViewModel
import com.pndnwngi.mysalesmanship.viewmodel.CheckStockViewModel
import com.pndnwngi.mysalesmanship.viewmodel.SalesViewModel
import java.text.NumberFormat
import java.util.Locale

data class VisitProductItem(
    val id: String,
    val name: String,
    val price: Double,
    val lastWeekStock: Int,
    val salesStock: Int
)

@Composable
fun VisitScreen(
    outletId: String,
    onFinishVisit: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    visitViewModel: VisitViewModel = hiltViewModel(),
    checkStockViewModel: CheckStockViewModel = hiltViewModel(),
    salesViewModel: SalesViewModel = hiltViewModel()
) {
    val outletIdLong = outletId.toLongOrNull() ?: 0L
    val context = LocalContext.current

    LaunchedEffect(outletIdLong) {
        visitViewModel.loadActiveVisit(outletIdLong, lat = -6.2088, lng = 106.8456)
    }

    val visitState by visitViewModel.uiState.collectAsState()
    val productsList by checkStockViewModel.products.collectAsState()
    val remainingStocks by salesViewModel.remainingStocks.collectAsState()

    LaunchedEffect(productsList) {
        if (productsList.isNotEmpty()) {
            salesViewModel.loadRemainingStocks(productsList.map { it.serverId })
        }
    }

    LaunchedEffect(visitState) {
        if (visitState is VisitUiState.Finished) {
            onFinishVisit()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        when (val state = visitState) {
            is VisitUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is VisitUiState.Error -> {
                Column(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("Error: ${state.message}", color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { visitViewModel.loadActiveVisit(outletIdLong, -6.2088, 106.8456) }) {
                        Text("Coba Lagi")
                    }
                }
            }
            is VisitUiState.ActiveVisit -> {
                val visitProducts = productsList.map { product ->
                    val carriedStock = remainingStocks[product.serverId] ?: 0
                    VisitProductItem(
                        id = product.serverId.toString(),
                        name = product.name,
                        price = product.price,
                        lastWeekStock = 0,
                        salesStock = carriedStock
                    )
                }

                var isSaving by remember { mutableStateOf(false) }

                VisitScreenContent(
                    outletName = state.outlet.name,
                    products = visitProducts,
                    gpsLatitude = state.outlet.lat ?: -6.2088,
                    gpsLongitude = state.outlet.lng ?: 106.8456,
                    onFinishClick = { checkStockMap, salesMap ->
                        isSaving = true
                        val mappedCheckStock = checkStockMap.mapKeys { it.key.toLong() }
                        checkStockViewModel.saveCheckStocks(state.visitId, mappedCheckStock) { checkStockRes ->
                            if (checkStockRes.isFailure) {
                                isSaving = false
                                Toast.makeText(context, checkStockRes.exceptionOrNull()?.message ?: "Gagal simpan cek stok", Toast.LENGTH_LONG).show()
                                return@saveCheckStocks
                            }

                            val salesItems = salesMap.filter { it.value > 0 }.map { entry ->
                                val prodId = entry.key.toLong()
                                val prod = productsList.first { it.serverId == prodId }
                                com.pndnwngi.mysalesmanship.usecase.SalesItemInput(
                                    productId = prodId,
                                    qty = entry.value,
                                    price = prod.price
                                )
                            }

                            if (salesItems.isNotEmpty()) {
                                salesViewModel.saveSales(state.visitId, salesItems) { salesRes ->
                                    if (salesRes.isFailure) {
                                        isSaving = false
                                        Toast.makeText(context, salesRes.exceptionOrNull()?.message ?: "Gagal simpan penjualan", Toast.LENGTH_LONG).show()
                                        return@saveSales
                                    }
                                    visitViewModel.finishVisit(state.visitId)
                                }
                            } else {
                                visitViewModel.finishVisit(state.visitId)
                            }
                        }
                    },
                    onBack = onBack
                )

                LoadingOverlay(isLoading = isSaving, message = "Menyimpan kunjungan...")
            }
            else -> {}
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VisitScreenContent(
    outletName: String,
    products: List<VisitProductItem>,
    gpsLatitude: Double,
    gpsLongitude: Double,
    onFinishClick: (Map<String, Int>, Map<String, Int>) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val checkStockInputs = remember(products) { mutableStateListOf(*Array(products.size) { "" }) }
    val salesInputs = remember(products) { mutableStateListOf(*Array(products.size) { "" }) }
    
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Cek Stok", "Penjualan")

    val gpsAccuracy = "5m (Akurat)"

    val isCheckStockValid = checkStockInputs.map { text ->
        if (text.isEmpty()) return@map true
        val qty = text.toIntOrNull()
        qty != null && qty >= 0
    }

    val isSalesValid = salesInputs.mapIndexed { index, text ->
        if (text.isEmpty()) return@mapIndexed true
        val qty = text.toIntOrNull()
        val maxStock = products.getOrNull(index)?.salesStock ?: 0
        qty != null && qty >= 0 && qty <= maxStock
    }

    val canFinish = remember(checkStockInputs, salesInputs, isCheckStockValid, isSalesValid) {
        val anyCheckStock = checkStockInputs.any { it.isNotEmpty() }
        val allCheckStockValid = isCheckStockValid.all { it }
        val allSalesValid = isSalesValid.all { it }
        anyCheckStock && allCheckStockValid && allSalesValid
    }

    val rupiahFormatter = remember {
        NumberFormat.getCurrencyInstance(Locale.Builder().setLanguage("id").setRegion("ID").build()).apply {
            maximumFractionDigits = 0
        }
    }

    val totalAmount = remember(salesInputs, products) {
        salesInputs.mapIndexed { index, text ->
            val qty = text.toIntOrNull() ?: 0
            val price = products.getOrNull(index)?.price ?: 0.0
            qty * price
        }.sum()
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = outletName,
                onBackClick = onBack
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AppCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "GPS",
                            tint = Color(0xFF2E7D32),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "GPS: $gpsLatitude, $gpsLongitude (Akurasi: $gpsAccuracy)",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF2E7D32),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title, fontWeight = FontWeight.Bold) }
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    itemsIndexed(products) { index, product ->
                        AppCard(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = product.name,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = rupiahFormatter.format(product.price),
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                if (selectedTab == 0) {
                                    val err = !isCheckStockValid.getOrElse(index) { true }
                                    Row(
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = "Histori Minggu Lalu: ${product.lastWeekStock} Bks",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        OutlinedTextField(
                                            value = checkStockInputs.getOrElse(index) { "" },
                                            onValueChange = { checkStockInputs[index] = it },
                                            label = { Text("Stok Outlet") },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            isError = err,
                                            modifier = Modifier.width(120.dp),
                                            singleLine = true
                                        )
                                    }
                                } else {
                                    val err = !isSalesValid.getOrElse(index) { true }
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Row(
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                text = "Sisa Stok Sales: ${product.salesStock} Bks",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            OutlinedTextField(
                                                value = salesInputs.getOrElse(index) { "" },
                                                onValueChange = { salesInputs[index] = it },
                                                label = { Text("Qty Penjualan") },
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                                isError = err,
                                                modifier = Modifier.width(120.dp),
                                                singleLine = true
                                            )
                                        }
                                        val qty = salesInputs.getOrElse(index) { "" }.toIntOrNull() ?: 0
                                        if (qty > 0) {
                                            Row(
                                                horizontalArrangement = Arrangement.End,
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Text(
                                                    text = "Subtotal: ${rupiahFormatter.format(qty * product.price)}",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.secondary
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Surface(
                tonalElevation = 8.dp,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (selectedTab == 1 && totalAmount > 0) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Total Transaksi:",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = rupiahFormatter.format(totalAmount),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                              )
                        }
                    }

                    AppButton(
                        text = "Selesai Kunjungan",
                        onClick = {
                            val checkStockMap = mutableMapOf<String, Int>()
                            val salesMap = mutableMapOf<String, Int>()
                            products.forEachIndexed { index, product ->
                                val csText = checkStockInputs.getOrNull(index) ?: ""
                                if (csText.isNotEmpty()) {
                                    csText.toIntOrNull()?.let { checkStockMap[product.id] = it }
                                }
                                val sText = salesInputs.getOrNull(index) ?: ""
                                if (sText.isNotEmpty()) {
                                    sText.toIntOrNull()?.let { salesMap[product.id] = it }
                                }
                            }
                            onFinishClick(checkStockMap, salesMap)
                        },
                        enabled = canFinish,
                        icon = Icons.Default.CheckCircle,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun VisitScreenPreview() {
    MySalesmanshipTheme {
        VisitScreenContent(
            outletName = "Toko Sinar Jaya",
            products = listOf(
                VisitProductItem("1", "Gudang Garam Filter 16", 28000.0, 10, 50)
            ),
            gpsLatitude = -6.2088,
            gpsLongitude = 106.8456,
            onFinishClick = { _, _ -> },
            onBack = {}
        )
    }
}
