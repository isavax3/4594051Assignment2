package com.example.assignment2.ui.login

import com.example.assignment2.R
import com.example.assignment2.data.model.ApiResult
import com.example.assignment2.data.model.ErrorReason
import com.example.assignment2.data.repository.AuthRepository
import com.example.assignment2.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // the repository is mocked so these tests never touch the real api
    private val authRepository: AuthRepository = mockk()
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setUp() {
        viewModel = LoginViewModel(authRepository)
    }

    @Test
    fun `starts in the idle state`() {
        assertEquals(LoginUiState.Idle, viewModel.uiState.value)
    }

    @Test
    fun `successful login shows loading then success with the keypass`() = runTest {
        // Given
        coEvery { authRepository.login("12345678", "Alex") } returns ApiResult.Success("animals")

        // When
        viewModel.login("12345678", "Alex")

        // Then - loading straight away, then success once the coroutine has run
        assertEquals(LoginUiState.Loading, viewModel.uiState.value)
        advanceUntilIdle()
        assertEquals(LoginUiState.Success("animals"), viewModel.uiState.value)
        coVerify(exactly = 1) { authRepository.login("12345678", "Alex") }
    }

    @Test
    fun `spaces are trimmed but the first name keeps its capitals`() = runTest {
        coEvery { authRepository.login(any(), any()) } returns ApiResult.Success("animals")

        viewModel.login("  12345678 ", " Alex ")
        advanceUntilIdle()

        coVerify { authRepository.login("12345678", "Alex") }
    }

    @Test
    fun `empty fields show an error without calling the api`() = runTest {
        viewModel.login("", "")
        advanceUntilIdle()

        assertEquals(LoginUiState.Error(R.string.error_empty_fields), viewModel.uiState.value)
        coVerify(exactly = 0) { authRepository.login(any(), any()) }
    }

    @Test
    fun `student id with an s in front shows an error`() = runTest {
        viewModel.login("s12345678", "Alex")
        advanceUntilIdle()

        assertEquals(LoginUiState.Error(R.string.error_id_digits), viewModel.uiState.value)
        coVerify(exactly = 0) { authRepository.login(any(), any()) }
    }

    @Test
    fun `wrong details from the api shows the wrong details message`() = runTest {
        coEvery { authRepository.login(any(), any()) } returns ApiResult.Error(ErrorReason.WRONG_DETAILS)

        viewModel.login("12345678", "alex")
        advanceUntilIdle()

        assertEquals(LoginUiState.Error(R.string.error_wrong_details), viewModel.uiState.value)
    }

    @Test
    fun `no connection shows the connection message`() = runTest {
        coEvery { authRepository.login(any(), any()) } returns ApiResult.Error(ErrorReason.NO_CONNECTION)

        viewModel.login("12345678", "Alex")
        advanceUntilIdle()

        assertEquals(LoginUiState.Error(R.string.error_no_connection), viewModel.uiState.value)
    }

    @Test
    fun `tapping log in again while loading doesn't call the api twice`() = runTest {
        coEvery { authRepository.login(any(), any()) } returns ApiResult.Success("animals")

        viewModel.login("12345678", "Alex")
        viewModel.login("12345678", "Alex")
        advanceUntilIdle()

        coVerify(exactly = 1) { authRepository.login(any(), any()) }
    }

    @Test
    fun `clearError goes back to idle`() = runTest {
        viewModel.login("", "")
        viewModel.clearError()

        assertEquals(LoginUiState.Idle, viewModel.uiState.value)
    }

    @Test
    fun `onNavigated resets the state so the dashboard isn't opened twice`() = runTest {
        coEvery { authRepository.login(any(), any()) } returns ApiResult.Success("animals")
        viewModel.login("12345678", "Alex")
        advanceUntilIdle()

        viewModel.onNavigated()

        assertEquals(LoginUiState.Idle, viewModel.uiState.value)
    }
}
