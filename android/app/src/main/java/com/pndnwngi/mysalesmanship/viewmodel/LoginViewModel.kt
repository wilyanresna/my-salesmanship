package com.pndnwngi.mysalesmanship.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pndnwngi.mysalesmanship.ui.screens.LoginUiState
import com.pndnwngi.mysalesmanship.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun login(username: String, password: String, onSuccess: () -> Unit) {
        _uiState.value = LoginUiState.Loading
        viewModelScope.launch {
            val result = loginUseCase(username, password)
            if (result.isSuccess) {
                _uiState.value = LoginUiState.Success
                onSuccess()
            } else {
                val errorMsg = result.exceptionOrNull()?.message ?: "Login gagal"
                _uiState.value = LoginUiState.Error(errorMsg)
            }
        }
    }
}
