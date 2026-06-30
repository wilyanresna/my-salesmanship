package com.pndnwngi.mysalesmanship.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pndnwngi.mysalesmanship.ui.components.AppButton
import com.pndnwngi.mysalesmanship.ui.components.AppTextField
import com.pndnwngi.mysalesmanship.ui.components.LoadingOverlay
import com.pndnwngi.mysalesmanship.ui.theme.MySalesmanshipTheme
import com.pndnwngi.mysalesmanship.viewmodel.LoginViewModel

sealed interface LoginUiState {
    data object Idle : LoginUiState
    data object Loading : LoginUiState
    data class Error(val message: String) : LoginUiState
    data object Success : LoginUiState
}

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LoginScreenContent(
        uiState = uiState,
        onLoginClick = { username, password ->
            viewModel.login(username, password, onLoginSuccess)
        },
        modifier = modifier
    )
}

@Composable
fun LoginScreenContent(
    uiState: LoginUiState,
    onLoginClick: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    var validationError by remember { mutableStateOf<String?>(null) }

    Scaffold(modifier = modifier) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = MaterialTheme.shapes.extraLarge,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                Column(
                    modifier = Modifier.padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Salesmanship",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Aplikasi Distribusi Lapangan",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(32.dp))

                    AppTextField(
                        value = username,
                        onValueChange = { 
                            username = it
                            validationError = null
                        },
                        label = "Username / NIK",
                        leadingIcon = Icons.Default.Person,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    AppTextField(
                        value = password,
                        onValueChange = { 
                            password = it
                            validationError = null
                        },
                        label = "Password",
                        leadingIcon = Icons.Default.Lock,
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (passwordVisible) "Sembunyikan password" else "Tampilkan password"
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { focusManager.clearFocus() }
                        ),
                        isError = uiState is LoginUiState.Error || validationError != null,
                        errorText = validationError ?: (uiState as? LoginUiState.Error)?.message
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    AppButton(
                        text = "Masuk",
                        onClick = {
                            if (username.isBlank() || password.isBlank()) {
                                validationError = "Username dan password tidak boleh kosong"
                            } else {
                                focusManager.clearFocus()
                                onLoginClick(username, password)
                            }
                        }
                    )
                }
            }
        }
        
        LoadingOverlay(
            isLoading = uiState is LoginUiState.Loading,
            message = "Memproses masuk..."
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    MySalesmanshipTheme {
        LoginScreenContent(
            uiState = LoginUiState.Idle,
            onLoginClick = { _, _ -> }
        )
    }
}
