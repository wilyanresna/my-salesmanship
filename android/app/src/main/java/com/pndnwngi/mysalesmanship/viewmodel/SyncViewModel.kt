package com.pndnwngi.mysalesmanship.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pndnwngi.mysalesmanship.data.local.AppDatabase
import com.pndnwngi.mysalesmanship.data.repository.SyncRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class SyncQueueDisplayItem(
    val id: String,
    val type: String,
    val outletName: String,
    val timestamp: String,
    val status: String
)

sealed interface SyncUiState {
    data object Idle : SyncUiState
    data object Loading : SyncUiState
    data class Success(
        val pendingCount: Int,
        val items: List<SyncQueueDisplayItem>
    ) : SyncUiState
    data class Error(val message: String) : SyncUiState
}

@HiltViewModel
class SyncViewModel @Inject constructor(
    private val syncRepository: SyncRepository,
    private val database: AppDatabase
) : ViewModel() {

    private val _uiState = MutableStateFlow<SyncUiState>(SyncUiState.Idle)
    val uiState: StateFlow<SyncUiState> = _uiState.asStateFlow()

    private val timeFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm 'WIB'")
        .withZone(ZoneId.systemDefault())

    init {
        loadSyncQueue()
    }

    fun loadSyncQueue() {
        _uiState.value = SyncUiState.Loading
        viewModelScope.launch {
            try {
                val pending = syncRepository.getPendingSyncItems()
                val failed = syncRepository.getFailedSyncItems()
                val allEntities = pending + failed
                
                val displayItems = allEntities.mapNotNull { entity ->
                    val visit = database.trOutletDao().getVisitById(entity.entityId) ?: return@mapNotNull null
                    val outlet = database.mstOutletDao().getOutletById(visit.outletId)
                    val outletName = outlet?.name ?: "Outlet #${visit.outletId}"
                    val timeStr = timeFormatter.format(Instant.ofEpochMilli(visit.startTime))
                    
                    val typeStr = when (entity.entityType) {
                        "VISIT" -> "Kunjungan & Penjualan"
                        else -> entity.entityType
                    }

                    SyncQueueDisplayItem(
                        id = entity.id.toString(),
                        type = typeStr,
                        outletName = outletName,
                        timestamp = timeStr,
                        status = entity.status
                    )
                }

                _uiState.value = SyncUiState.Success(
                    pendingCount = pending.size + failed.size,
                    items = displayItems
                )
            } catch (e: Exception) {
                _uiState.value = SyncUiState.Error(e.message ?: "Gagal memuat antrean sinkronisasi")
            }
        }
    }

    fun triggerSync(onFinished: (Result<Unit>) -> Unit) {
        _uiState.value = SyncUiState.Loading
        viewModelScope.launch {
            val result = syncRepository.syncAllPending()
            loadSyncQueue()
            onFinished(result)
        }
    }
}
