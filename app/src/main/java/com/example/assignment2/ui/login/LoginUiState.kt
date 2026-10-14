package com.example.assignment2.ui.login

import androidx.annotation.StringRes

// everything the login screen can be showing. the activity just draws whichever one it gets
sealed interface LoginUiState {
    data object Idle : LoginUiState
    data object Loading : LoginUiState
    data class Success(val keypass: String) : LoginUiState
    data class Error(@StringRes val messageId: Int) : LoginUiState
}
