package com.pndnwngi.mysalesmanship.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pndnwngi.mysalesmanship.ui.components.AppCard
import com.pndnwngi.mysalesmanship.ui.components.AppTopBar
import com.pndnwngi.mysalesmanship.ui.theme.MySalesmanshipTheme
import com.pndnwngi.mysalesmanship.viewmodel.ReturSummaryUiState
import com.pndnwngi.mysalesmanship.viewmodel.ReturSummaryViewModel

data class StockQty(
    val dus: Int,
    val bal: Int,
    val slop: Int,
    val bks: Int
) {
    override fun toString(): String {
        return "$dus Dus, $bal Bal, $slop Slop, $bks Bks"
    }
}

data class ProductReturData(
    val sku: String,
    val name: String,
    val initialStock: StockQty,
    val soldStock: StockQty,
    val packsPerSlop: Int = 10,
    val slopsPerBal: Int = 10,
    val balsPerDus: Int = 4
)

@Composable
fun ReturSummaryScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ReturSummaryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Ringkasan Retur Stok",
                onBackClick = onBack
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AppCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Formula Retur",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Retur Barang = Stok Awal - Total Penjualan. Dihitung otomatis per SKU dalam satuan terkecil (Bungkus) kemudian dikonversi kembali ke multi-UOM.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                when (val state = uiState) {
                    is ReturSummaryUiState.Loading -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                    is ReturSummaryUiState.Error -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = state.message,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { viewModel.loadReturSummary() }) {
                                Text("Coba Lagi")
                            }
                        }
                    }
                    is ReturSummaryUiState.Success -> {
                        if (state.products.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(
                                    text = "Tidak ada ringkasan retur stok aktif",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(state.products) { data ->
                                    val packsPerSlop = data.packsPerSlop
                                    val packsPerBal = data.slopsPerBal * packsPerSlop
                                    val packsPerDus = data.balsPerDus * packsPerBal

                                    val totalInitialPacks = (data.initialStock.dus * packsPerDus) +
                                            (data.initialStock.bal * packsPerBal) +
                                            (data.initialStock.slop * packsPerSlop) +
                                            data.initialStock.bks

                                    val totalSoldPacks = (data.soldStock.dus * packsPerDus) +
                                            (data.soldStock.bal * packsPerBal) +
                                            (data.soldStock.slop * packsPerSlop) +
                                            data.soldStock.bks

                                    val totalReturnedPacks = (totalInitialPacks - totalSoldPacks).coerceAtLeast(0)

                                    var rem = totalReturnedPacks
                                    val returDus = rem / packsPerDus
                                    rem %= packsPerDus
                                    val returBal = rem / packsPerBal
                                    rem %= packsPerBal
                                    val returSlop = rem / packsPerSlop
                                    val returBks = rem % packsPerSlop

                                    val returnedStock = StockQty(returDus, returBal, returSlop, returBks)

                                    AppCard(modifier = Modifier.fillMaxWidth()) {
                                        Column(modifier = Modifier.padding(16.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = data.name,
                                                    style = MaterialTheme.typography.titleMedium,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Text(
                                                    text = data.sku,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.outline
                                                )
                                            }
                                            
                                            Spacer(modifier = Modifier.height(12.dp))
                                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                                            Spacer(modifier = Modifier.height(12.dp))

                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                UomColumn(title = "Stok Awal", stock = data.initialStock, textColor = MaterialTheme.colorScheme.onSurface)
                                                UomColumn(title = "Penjualan", stock = data.soldStock, textColor = MaterialTheme.colorScheme.primary)
                                                UomColumn(title = "Retur Fisik", stock = returnedStock, textColor = Color(0xFFC62828))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RowScope.UomColumn(
    title: String,
    stock: StockQty,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.weight(1f),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.outline
        )
        Spacer(modifier = Modifier.height(8.dp))
        UomLabel(value = stock.dus, label = "DUS", color = textColor)
        UomLabel(value = stock.bal, label = "BAL", color = textColor)
        UomLabel(value = stock.slop, label = "SLOP", color = textColor)
        UomLabel(value = stock.bks, label = "BKS", color = textColor)
    }
}

@Composable
fun UomLabel(value: Int, label: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding(vertical = 2.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline
        )
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ReturSummaryScreenPreview() {
    MySalesmanshipTheme {
        ReturSummaryScreen(onBack = {})
    }
}
