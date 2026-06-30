package com.pndnwngi.mysalesmanship.ui.screens

import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pndnwngi.mysalesmanship.ui.components.*
import com.pndnwngi.mysalesmanship.ui.theme.MySalesmanshipTheme
import com.pndnwngi.mysalesmanship.viewmodel.SyncQueueDisplayItem
import com.pndnwngi.mysalesmanship.viewmodel.SyncUiState
import com.pndnwngi.mysalesmanship.viewmodel.SyncViewModel

@Composable
fun SyncStatusScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SyncViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var isConnected by remember { mutableStateOf(true) }
    val context = LocalContext.current

    // Sync button rotation animation
    val transition = rememberInfiniteTransition(label = "Sync Spin")
    val rotationAngle by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Rotation Angle"
    )
    val isSyncing = uiState is SyncUiState.Loading
    val buttonRotation = if (isSyncing) rotationAngle else 0f

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Sinkronisasi Data",
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
            // Internet Connection Status Card
            AppCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isConnected) Icons.Default.CloudDone else Icons.Default.CloudOff,
                            contentDescription = "Connection Status",
                            tint = if (isConnected) Color(0xFF2E7D32) else Color(0xFFC62828),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Koneksi Internet",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isConnected) "Terhubung ke server (Online)" else "Offline. Transaksi disimpan lokal.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Switch(
                        checked = isConnected,
                        onCheckedChange = { isConnected = it }
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                when (val state = uiState) {
                    is SyncUiState.Loading -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                    is SyncUiState.Error -> {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(24.dp),
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
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.End
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { viewModel.loadSyncQueue() }) {
                                Text("Coba Lagi")
                            }
                        }
                    }
                    is SyncUiState.Success -> {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Antrean Sinkronisasi (Sync Queue)",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${state.pendingCount} Tertunda",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.secondary,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            if (state.items.isEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1.5f),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Semua transaksi telah tersinkronisasi.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            } else {
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    items(state.items) { item ->
                                        AppCard(modifier = Modifier.fillMaxWidth()) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(16.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column(modifier = Modifier.weight(1.5f)) {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Text(
                                                            text = item.type,
                                                            style = MaterialTheme.typography.bodyLarge,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                        Spacer(modifier = Modifier.width(6.dp))
                                                        Text(
                                                            text = "#${item.id}",
                                                            style = MaterialTheme.typography.bodySmall,
                                                            color = MaterialTheme.colorScheme.outline
                                                        )
                                                    }
                                                    Text(
                                                        text = item.outletName,
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                    Spacer(modifier = Modifier.height(4.dp))
                                                    Text(
                                                        text = item.timestamp,
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = MaterialTheme.colorScheme.outline
                                                    )
                                                }
                                                
                                                AppBadge(
                                                    text = item.status,
                                                    containerColor = when (item.status) {
                                                        "SYNCED" -> Color(0xFFC8E6C9)
                                                        "PENDING" -> Color(0xFFBBDEFB)
                                                        "SYNCING" -> Color(0xFFE1BEE7)
                                                        else -> Color(0xFFFFCDD2) // FAILED
                                                    },
                                                    contentColor = when (item.status) {
                                                        "SYNCED" -> Color(0xFF2E7D32)
                                                        "PENDING" -> Color(0xFF1565C0)
                                                        "SYNCING" -> Color(0xFF4A148C)
                                                        else -> Color(0xFFC62828) // FAILED
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            AppButton(
                                text = if (isSyncing) "Mensinkronkan..." else "Sinkronisasi Manual",
                                onClick = {
                                    if (!isConnected) return@AppButton
                                    viewModel.triggerSync { res ->
                                        if (res.isSuccess) {
                                            Toast.makeText(context, "Sinkronisasi berhasil!", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(context, "Gagal sinkronisasi: ${res.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                },
                                enabled = isConnected && !isSyncing && state.items.isNotEmpty(),
                                icon = Icons.Default.Sync,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .rotate(buttonRotation)
                            )
                        }
                    }
                    else -> {}
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SyncStatusScreenPreview() {
    MySalesmanshipTheme {
        SyncStatusScreen(onBack = {})
    }
}
