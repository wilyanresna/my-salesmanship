package com.pndnwngi.mysalesmanship.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pndnwngi.mysalesmanship.data.repository.AuthRepository
import com.pndnwngi.mysalesmanship.usecase.DashboardData
import com.pndnwngi.mysalesmanship.usecase.GetDashboardUseCase
import com.pndnwngi.mysalesmanship.usecase.PullDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface DashboardUiState {
    data object Loading : DashboardUiState
    data class Success(val data: DashboardData) : DashboardUiState
    data class Error(val message: String) : DashboardUiState
}

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getDashboardUseCase: GetDashboardUseCase,
    private val pullDataUseCase: PullDataUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<DashboardUiState>(DashboardUiState.Loading)
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboard()
    }

    fun loadDashboard() {
        _uiState.value = DashboardUiState.Loading
        viewModelScope.launch {
            val result = getDashboardUseCase()
            if (result.isSuccess) {
                _uiState.value = DashboardUiState.Success(result.getOrThrow())
            } else {
                _uiState.value = DashboardUiState.Error(result.exceptionOrNull()?.message ?: "Gagal memuat dashboard")
            }
        }
    }

    fun pullData(onFinished: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            val result = pullDataUseCase()
            if (result.isSuccess) {
                loadDashboard()
            }
            onFinished(result)
        }
    }

    fun logout(onFinished: () -> Unit) {
        viewModelScope.launch {
            authRepository.clearTokens()
            onFinished()
        }
    }
}
