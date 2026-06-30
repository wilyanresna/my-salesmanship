package com.pndnwngi.mysalesmanship.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pndnwngi.mysalesmanship.data.repository.SalesRepository
import com.pndnwngi.mysalesmanship.usecase.AddSalesUseCase
import com.pndnwngi.mysalesmanship.usecase.SalesItemInput
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SalesViewModel @Inject constructor(
    private val addSalesUseCase: AddSalesUseCase,
    private val salesRepository: SalesRepository
) : ViewModel() {

    private val _remainingStocks = MutableStateFlow<Map<Long, Int>>(emptyMap())
    val remainingStocks: StateFlow<Map<Long, Int>> = _remainingStocks.asStateFlow()

    fun loadRemainingStocks(productIds: List<Long>) {
        viewModelScope.launch {
            val stockMap = mutableMapOf<Long, Int>()
            for (productId in productIds) {
                stockMap[productId] = salesRepository.getRemainingStock(productId)
            }
            _remainingStocks.value = stockMap
        }
    }

    fun saveSales(visitId: Long, items: List<SalesItemInput>, onFinished: (Result<Long>) -> Unit) {
        viewModelScope.launch {
            val result = addSalesUseCase(visitId, items)
            onFinished(result)
        }
    }
}
