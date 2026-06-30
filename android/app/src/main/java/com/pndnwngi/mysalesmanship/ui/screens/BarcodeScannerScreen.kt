package com.pndnwngi.mysalesmanship.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashlightOff
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pndnwngi.mysalesmanship.ui.components.AppButton
import com.pndnwngi.mysalesmanship.ui.components.AppTopBar
import com.pndnwngi.mysalesmanship.ui.theme.MySalesmanshipTheme
import com.pndnwngi.mysalesmanship.viewmodel.BarcodeScanUiState
import com.pndnwngi.mysalesmanship.viewmodel.BarcodeScannerViewModel

@Composable
fun BarcodeScannerScreen(
    onBarcodeDetected: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BarcodeScannerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var flashlightOn by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "Laser Scanner Line")
    val laserOffsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 240f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Laser Y Offset"
    )

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Scan Barcode Outlet",
                onBackClick = onBack,
                actions = {
                    IconButton(onClick = { flashlightOn = !flashlightOn }) {
                        Icon(
                            imageVector = if (flashlightOn) Icons.Default.FlashlightOn else Icons.Default.FlashlightOff,
                            contentDescription = "Flashlight Toggle"
                        )
                    }
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFF121212)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Posisikan barcode outlet di dalam area kotak berikut",
                color = Color.White.copy(alpha = 0.8f),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(24.dp)
            )

            Box(
                modifier = Modifier
                    .size(260.dp)
                    .border(2.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Black.copy(alpha = 0.6f)),
                contentAlignment = Alignment.TopCenter
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .border(4.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(16.dp))
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .offset(y = laserOffsetY.dp)
                        .background(Color(0xFF00FF00))
                )
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    when (val state = uiState) {
                        is BarcodeScanUiState.Loading -> {
                            CircularProgressIndicator()
                            Text("Mencari outlet...")
                        }
                        is BarcodeScanUiState.Success -> {
                            Text(
                                text = "Outlet Ditemukan!",
                                color = Color(0xFF2E7D32),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = state.outlet.name,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Barcode: ${state.outlet.barcode}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            AppButton(
                                text = "Mulai Kunjungan",
                                onClick = { onBarcodeDetected(state.outlet.serverId.toString()) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        is BarcodeScanUiState.Error -> {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Not Found",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(48.dp)
                            )
                            Text(
                                text = "Barcode Tidak Dikenali!",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = state.message,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )

                            AppButton(
                                text = "Scan Ulang",
                                onClick = { viewModel.resetState() },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        is BarcodeScanUiState.Idle -> {
                            Text(
                                text = "Simulasi Deteksi Barcode",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                AppButton(
                                    text = "Scan Sukses",
                                    onClick = {
                                        // Look up first outlet in the database
                                        viewModel.scanBarcode("OTL-0001")
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                                AppButton(
                                    text = "Scan Gagal",
                                    onClick = {
                                        viewModel.scanBarcode("INVALID-999")
                                    },
                                    outlined = true,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BarcodeScannerScreenPreview() {
    MySalesmanshipTheme {
        BarcodeScannerScreen(
            onBarcodeDetected = {},
            onBack = {}
        )
    }
}
