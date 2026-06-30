package com.pndnwngi.mysalesmanship.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pndnwngi.mysalesmanship.data.local.AppDatabase
import com.pndnwngi.mysalesmanship.data.local.entity.MstProductEntity
import com.pndnwngi.mysalesmanship.usecase.SaveCheckStockUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CheckStockViewModel @Inject constructor(
    private val saveCheckStockUseCase: SaveCheckStockUseCase,
    private val database: AppDatabase
) : ViewModel() {

    private val _products = MutableStateFlow<List<MstProductEntity>>(emptyList())
    val products: StateFlow<List<MstProductEntity>> = _products.asStateFlow()

    init {
        loadProducts()
    }

    private fun loadProducts() {
        viewModelScope.launch {
            try {
                val list = database.mstProductDao().getAllProducts()
                _products.value = list
            } catch (e: Exception) {
                // ignore
            }
        }
    }

    fun saveCheckStocks(visitId: Long, checkStockInputs: Map<Long, Int>, onFinished: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            try {
                for ((productId, qty) in checkStockInputs) {
                    val res = saveCheckStockUseCase(visitId, productId, qty)
                    if (res.isFailure) {
                        onFinished(Result.failure(res.exceptionOrNull() ?: Exception("Gagal menyimpan cek stok")))
                        return@launch
                    }
                }
                onFinished(Result.success(Unit))
            } catch (e: Exception) {
                onFinished(Result.failure(e))
            }
        }
    }
}
