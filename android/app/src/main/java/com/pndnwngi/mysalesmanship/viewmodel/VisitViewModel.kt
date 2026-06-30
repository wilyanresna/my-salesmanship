package com.pndnwngi.mysalesmanship.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pndnwngi.mysalesmanship.data.local.AppDatabase
import com.pndnwngi.mysalesmanship.data.local.entity.MstOutletEntity
import com.pndnwngi.mysalesmanship.data.repository.OutletRepository
import com.pndnwngi.mysalesmanship.data.repository.VisitRepository
import com.pndnwngi.mysalesmanship.usecase.CloseVisitUseCase
import com.pndnwngi.mysalesmanship.usecase.OpenVisitUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface VisitUiState {
    data object Idle : VisitUiState
    data object Loading : VisitUiState
    data class ActiveVisit(val visitId: Long, val outlet: MstOutletEntity, val startTime: Long) : VisitUiState
    data class Error(val message: String) : VisitUiState
    data object Finished : VisitUiState
}

@HiltViewModel
class VisitViewModel @Inject constructor(
    private val openVisitUseCase: OpenVisitUseCase,
    private val closeVisitUseCase: CloseVisitUseCase,
    private val outletRepository: OutletRepository,
    private val visitRepository: VisitRepository,
    private val database: AppDatabase
) : ViewModel() {

    private val _uiState = MutableStateFlow<VisitUiState>(VisitUiState.Idle)
    val uiState: StateFlow<VisitUiState> = _uiState.asStateFlow()

    fun loadActiveVisit(outletId: Long, lat: Double?, lng: Double?) {
        _uiState.value = VisitUiState.Loading
        viewModelScope.launch {
            try {
                val openVisits = visitRepository.getOpenVisits()
                val existing = openVisits.firstOrNull { it.outletId == outletId }
                
                val outlet = outletRepository.getOutletById(outletId)
                    ?: throw Exception("Outlet tidak ditemukan")

                if (existing != null) {
                    _uiState.value = VisitUiState.ActiveVisit(existing.id, outlet, existing.startTime)
                } else {
                    val salesman = database.mstSalesmanDao().getSalesman()
                        ?: throw Exception("Data salesman tidak ditemukan. Harap tarik data terlebih dahulu.")

                    val calendar = java.util.Calendar.getInstance()
                    val dayOfWeek = when (calendar.get(java.util.Calendar.DAY_OF_WEEK)) {
                        java.util.Calendar.MONDAY -> 1L
                        java.util.Calendar.TUESDAY -> 2L
                        java.util.Calendar.WEDNESDAY -> 3L
                        java.util.Calendar.THURSDAY -> 4L
                        java.util.Calendar.FRIDAY -> 5L
                        java.util.Calendar.SATURDAY -> 6L
                        java.util.Calendar.SUNDAY -> 7L
                        else -> 1L
                    }
                    val visitType = if (outlet.routeId == dayOfWeek) "NORMAL" else "OUT_OF_ROUTE"

                    val result = openVisitUseCase.openById(
                        outletId = outletId,
                        salesId = salesman.salesId,
                        visitType = visitType,
                        lat = lat,
                        lng = lng
                    )

                    if (result.isSuccess) {
                        val visitId = result.getOrThrow()
                        val visitEntity = visitRepository.getVisitById(visitId)
                            ?: throw Exception("Gagal memuat kunjungan yang baru dibuat")
                        _uiState.value = VisitUiState.ActiveVisit(visitId, outlet, visitEntity.startTime)
                    } else {
                        _uiState.value = VisitUiState.Error(result.exceptionOrNull()?.message ?: "Gagal memulai kunjungan")
                    }
                }
            } catch (e: Exception) {
                _uiState.value = VisitUiState.Error(e.message ?: "Terjadi kesalahan")
            }
        }
    }

    fun finishVisit(visitId: Long) {
        _uiState.value = VisitUiState.Loading
        viewModelScope.launch {
            val result = closeVisitUseCase(visitId)
            if (result.isSuccess) {
                _uiState.value = VisitUiState.Finished
            } else {
                _uiState.value = VisitUiState.Error(result.exceptionOrNull()?.message ?: "Gagal menyelesaikan kunjungan")
            }
        }
    }
}
