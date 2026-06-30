package com.pndnwngi.mysalesmanship.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pndnwngi.mysalesmanship.data.repository.OutletRepository
import com.pndnwngi.mysalesmanship.data.repository.VisitRepository
import com.pndnwngi.mysalesmanship.ui.screens.OutletItem
import com.pndnwngi.mysalesmanship.ui.screens.OutletListUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class OutletListViewModel @Inject constructor(
    private val outletRepository: OutletRepository,
    private val visitRepository: VisitRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<OutletListUiState>(OutletListUiState.Loading)
    val uiState: StateFlow<OutletListUiState> = _uiState.asStateFlow()

    private var allOutlets: List<OutletItem> = emptyList()

    init {
        loadOutlets()
    }

    fun loadOutlets() {
        _uiState.value = OutletListUiState.Loading
        viewModelScope.launch {
            try {
                val outlets = outletRepository.getAllOutlets()
                val calendar = Calendar.getInstance()
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val startOfDay = calendar.timeInMillis
                calendar.set(Calendar.HOUR_OF_DAY, 23)
                calendar.set(Calendar.MINUTE, 59)
                calendar.set(Calendar.SECOND, 59)
                calendar.set(Calendar.MILLISECOND, 999)
                val endOfDay = calendar.timeInMillis

                allOutlets = outlets.map { outlet ->
                    val visits = visitRepository.getVisitsByOutletAndDate(outlet.serverId, startOfDay, endOfDay)
                    val isVisited = visits.any { it.status == "CLOSED" }

                    val routeStr = when (outlet.routeId) {
                        1L -> "Senin (Route 1)"
                        2L -> "Selasa (Route 2)"
                        3L -> "Rabu (Route 3)"
                        4L -> "Kamis (Route 4)"
                        5L -> "Jumat (Route 5)"
                        6L -> "Sabtu (Route 6)"
                        7L -> "Minggu (Route 7)"
                        else -> "Route #${outlet.routeId}"
                    }

                    OutletItem(
                        id = outlet.serverId.toString(),
                        name = outlet.name,
                        owner = outlet.ownerName ?: "",
                        address = outlet.ownerName ?: "",
                        route = routeStr,
                        isVisited = isVisited
                    )
                }

                if (allOutlets.isEmpty()) {
                    _uiState.value = OutletListUiState.Empty
                } else {
                    _uiState.value = OutletListUiState.Success(allOutlets)
                }
            } catch (e: Exception) {
                _uiState.value = OutletListUiState.Error(e.message ?: "Gagal memuat outlet")
            }
        }
    }

    fun searchOutlets(query: String) {
        if (query.isBlank()) {
            if (allOutlets.isEmpty()) {
                _uiState.value = OutletListUiState.Empty
            } else {
                _uiState.value = OutletListUiState.Success(allOutlets)
            }
            return
        }
        val filtered = allOutlets.filter {
            it.name.contains(query, ignoreCase = true) ||
                    it.address.contains(query, ignoreCase = true) ||
                    it.id.contains(query, ignoreCase = true)
        }
        if (filtered.isEmpty()) {
            _uiState.value = OutletListUiState.Empty
        } else {
            _uiState.value = OutletListUiState.Success(filtered)
        }
    }
}
