package com.example.assignment2.ui.dashboard

import androidx.annotation.StringRes
import com.example.assignment2.data.model.Entity

// the 3 things the dashboard can show: loading, the list, or an error
sealed interface DashboardUiState {
    data object Loading : DashboardUiState
    data class Success(val entities: List<Entity>, val total: Int) : DashboardUiState
    data class Error(@StringRes val messageId: Int) : DashboardUiState
}
