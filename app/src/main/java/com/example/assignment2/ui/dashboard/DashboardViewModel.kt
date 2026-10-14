package com.example.assignment2.ui.dashboard

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignment2.R
import com.example.assignment2.data.model.ApiResult
import com.example.assignment2.data.repository.EntityRepository
import com.example.assignment2.ui.common.toMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// loads the entities for the keypass from the login screen.
// the keypass comes in the intent, and Hilt puts the intent extras in the SavedStateHandle
@HiltViewModel
class DashboardViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val entityRepository: EntityRepository
) : ViewModel() {

    val keypass: String = savedStateHandle.get<String>(KEY_KEYPASS).orEmpty()

    private val _uiState = MutableStateFlow<DashboardUiState>(DashboardUiState.Loading)
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadEntities()
    }

    // also used by the "Try again" button
    fun loadEntities() {
        if (keypass.isBlank()) {
            _uiState.value = DashboardUiState.Error(R.string.error_missing_keypass)
            return
        }

        _uiState.value = DashboardUiState.Loading
        viewModelScope.launch {
            _uiState.value = when (val result = entityRepository.getDashboard(keypass)) {
                is ApiResult.Success -> DashboardUiState.Success(result.data.entities, result.data.total)
                is ApiResult.Error -> DashboardUiState.Error(result.reason.toMessage())
            }
        }
    }

    companion object {
        // name of the intent extra that holds the keypass
        const val KEY_KEYPASS = "keypass"
    }
}
