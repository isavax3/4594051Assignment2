package com.example.assignment2.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignment2.R
import com.example.assignment2.data.model.ApiResult
import com.example.assignment2.data.repository.AuthRepository
import com.example.assignment2.ui.common.toMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// checks what the user typed, calls the login api through the repository and keeps
// the screen state in a StateFlow. Hilt injects the repository through the constructor.
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun login(studentId: String, firstName: String) {
        // ignore extra taps while a login is already happening
        if (_uiState.value is LoginUiState.Loading) return

        // only trim the spaces, the first name is case sensitive so it's not changed
        val id = studentId.trim()
        val name = firstName.trim()

        val inputError = checkInput(id, name)
        if (inputError != null) {
            _uiState.value = LoginUiState.Error(inputError)
            return
        }

        _uiState.value = LoginUiState.Loading
        viewModelScope.launch {
            _uiState.value = when (val result = authRepository.login(id, name)) {
                is ApiResult.Success -> LoginUiState.Success(result.data)
                is ApiResult.Error -> LoginUiState.Error(result.reason.toMessage())
            }
        }
    }

    // hides the error once the user starts typing again
    fun clearError() {
        if (_uiState.value is LoginUiState.Error) _uiState.value = LoginUiState.Idle
    }

    // called after opening the dashboard so the screen doesn't navigate again
    fun onNavigated() {
        _uiState.value = LoginUiState.Idle
    }

    // returns the error message to show, or null if the input looks ok
    private fun checkInput(studentId: String, firstName: String): Int? = when {
        studentId.isEmpty() || firstName.isEmpty() -> R.string.error_empty_fields
        !studentId.all { it.isDigit() } -> R.string.error_id_digits
        else -> null
    }
}
