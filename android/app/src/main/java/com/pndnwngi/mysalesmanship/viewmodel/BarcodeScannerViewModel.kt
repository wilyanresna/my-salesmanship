package com.pndnwngi.mysalesmanship.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pndnwngi.mysalesmanship.data.local.entity.MstOutletEntity
import com.pndnwngi.mysalesmanship.data.repository.OutletRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface BarcodeScanUiState {
    data object Idle : BarcodeScanUiState
    data object Loading : BarcodeScanUiState
    data class Success(val outlet: MstOutletEntity) : BarcodeScanUiState
    data class Error(val message: String) : BarcodeScanUiState
}

@HiltViewModel
class BarcodeScannerViewModel @Inject constructor(
    private val outletRepository: OutletRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<BarcodeScanUiState>(BarcodeScanUiState.Idle)
    val uiState: StateFlow<BarcodeScanUiState> = _uiState.asStateFlow()

    fun scanBarcode(barcode: String) {
        _uiState.value = BarcodeScanUiState.Loading
        viewModelScope.launch {
            try {
                val outlet = outletRepository.getOutletByBarcode(barcode)
                if (outlet != null) {
                    _uiState.value = BarcodeScanUiState.Success(outlet)
                } else {
                    _uiState.value = BarcodeScanUiState.Error("Barcode tidak terdaftar")
                }
            } catch (e: Exception) {
                _uiState.value = BarcodeScanUiState.Error(e.message ?: "Terjadi kesalahan lookup barcode")
            }
        }
    }

    fun resetState() {
        _uiState.value = BarcodeScanUiState.Idle
    }
}
