package com.example.assignment2.ui.dashboard

import androidx.lifecycle.SavedStateHandle
import com.example.assignment2.R
import com.example.assignment2.data.model.ApiResult
import com.example.assignment2.data.model.Dashboard
import com.example.assignment2.data.model.Entity
import com.example.assignment2.data.model.EntityField
import com.example.assignment2.data.model.ErrorReason
import com.example.assignment2.data.repository.EntityRepository
import com.example.assignment2.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val entityRepository: EntityRepository = mockk()

    private val koala = Entity(
        fields = listOf(
            EntityField(key = "name", label = "Name", value = "Koala"),
            EntityField(key = "habitat", label = "Habitat", value = "Eucalyptus forest")
        ),
        description = "A tree-dwelling marsupial."
    )

    // the keypass gets to the viewmodel through the SavedStateHandle, like the intent extra does in the app
    private fun createViewModel(keypass: String? = "animals") = DashboardViewModel(
        savedStateHandle = SavedStateHandle(
            if (keypass == null) emptyMap() else mapOf(DashboardViewModel.KEY_KEYPASS to keypass)
        ),
        entityRepository = entityRepository
    )

    @Test
    fun `loads the entities for the keypass when it's created`() = runTest {
        // Given
        coEvery { entityRepository.getDashboard("animals") } returns
            ApiResult.Success(Dashboard(entities = listOf(koala), total = 1))

        // When
        val viewModel = createViewModel()

        // Then
        assertEquals(DashboardUiState.Loading, viewModel.uiState.value)
        advanceUntilIdle()
        assertEquals(DashboardUiState.Success(entities = listOf(koala), total = 1), viewModel.uiState.value)
        coVerify(exactly = 1) { entityRepository.getDashboard("animals") }
    }

    @Test
    fun `keeps the keypass so the screen can show the topic`() = runTest {
        coEvery { entityRepository.getDashboard(any()) } returns ApiResult.Success(Dashboard(emptyList(), 0))

        val viewModel = createViewModel(keypass = "animals")

        assertEquals("animals", viewModel.keypass)
    }

    @Test
    fun `shows an error when the api call fails`() = runTest {
        coEvery { entityRepository.getDashboard("animals") } returns ApiResult.Error(ErrorReason.NO_CONNECTION)

        val viewModel = createViewModel()
        advanceUntilIdle()

        assertEquals(DashboardUiState.Error(R.string.error_no_connection), viewModel.uiState.value)
    }

    @Test
    fun `try again loads the entities after an error`() = runTest {
        // first call fails, the second one works
        coEvery { entityRepository.getDashboard("animals") } returnsMany listOf(
            ApiResult.Error(ErrorReason.SERVER),
            ApiResult.Success(Dashboard(entities = listOf(koala), total = 1))
        )
        val viewModel = createViewModel()
        advanceUntilIdle()
        assertEquals(DashboardUiState.Error(R.string.error_server), viewModel.uiState.value)

        viewModel.loadEntities()
        advanceUntilIdle()

        assertEquals(DashboardUiState.Success(entities = listOf(koala), total = 1), viewModel.uiState.value)
        coVerify(exactly = 2) { entityRepository.getDashboard("animals") }
    }

    @Test
    fun `missing keypass shows an error without calling the api`() = runTest {
        val viewModel = createViewModel(keypass = null)
        advanceUntilIdle()

        assertEquals(DashboardUiState.Error(R.string.error_missing_keypass), viewModel.uiState.value)
        coVerify(exactly = 0) { entityRepository.getDashboard(any()) }
    }
}
