package com.pndnwngi.mysalesmanship.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pndnwngi.mysalesmanship.data.local.AppDatabase
import com.pndnwngi.mysalesmanship.usecase.GetReturSummaryUseCase
import com.pndnwngi.mysalesmanship.ui.screens.ProductReturData
import com.pndnwngi.mysalesmanship.ui.screens.StockQty
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ReturSummaryUiState {
    data object Loading : ReturSummaryUiState
    data class Success(val products: List<ProductReturData>) : ReturSummaryUiState
    data class Error(val message: String) : ReturSummaryUiState
}

@HiltViewModel
class ReturSummaryViewModel @Inject constructor(
    private val getReturSummaryUseCase: GetReturSummaryUseCase,
    private val database: AppDatabase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ReturSummaryUiState>(ReturSummaryUiState.Loading)
    val uiState: StateFlow<ReturSummaryUiState> = _uiState.asStateFlow()

    init {
        loadReturSummary()
    }

    fun loadReturSummary() {
        _uiState.value = ReturSummaryUiState.Loading
        viewModelScope.launch {
            val result = getReturSummaryUseCase()
            if (result.isSuccess) {
                val list = result.getOrThrow()
                val uiList = list.mapNotNull { remaining ->
                    val product = database.mstProductDao().getProductByServerId(remaining.productId) ?: return@mapNotNull null
                    
                    val bksPerSlf = product.uomBks
                    val bksPerBal = product.uomSlf * product.uomBks
                    val bksPerDus = product.uomBal * product.uomSlf * product.uomBks

                    val totalInitialBks = remaining.qtyDusInit * bksPerDus + remaining.qtyBalInit * bksPerBal + remaining.qtySlfInit * bksPerSlf + remaining.qtyBksInit
                    val totalRemainingBks = remaining.qtyDusRemaining * bksPerDus + remaining.qtyBalRemaining * bksPerBal + remaining.qtySlfRemaining * bksPerSlf + remaining.qtyBksRemaining
                    var soldBks = totalInitialBks - totalRemainingBks
                    if (soldBks < 0) soldBks = 0

                    var rem = soldBks
                    val soldDus = rem / bksPerDus
                    rem %= bksPerDus
                    val soldBal = rem / bksPerBal
                    rem %= bksPerBal
                    val soldSlf = rem / bksPerSlf
                    val soldBksVal = rem % bksPerSlf

                    ProductReturData(
                        sku = remaining.sku,
                        name = remaining.name,
                        initialStock = StockQty(remaining.qtyDusInit, remaining.qtyBalInit, remaining.qtySlfInit, remaining.qtyBksInit),
                        soldStock = StockQty(soldDus, soldBal, soldSlf, soldBksVal),
                        packsPerSlop = product.uomBks,
                        slopsPerBal = product.uomSlf,
                        balsPerDus = product.uomBal
                    )
                }
                _uiState.value = ReturSummaryUiState.Success(uiList)
            } else {
                val errorMsg = result.exceptionOrNull()?.message ?: "Gagal memuat ringkasan retur"
                _uiState.value = ReturSummaryUiState.Error(errorMsg)
            }
        }
    }
}
