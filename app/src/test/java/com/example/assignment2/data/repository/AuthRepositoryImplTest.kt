package com.example.assignment2.data.repository

import com.example.assignment2.data.model.ApiResult
import com.example.assignment2.data.model.ErrorReason
import com.example.assignment2.data.remote.ApiService
import com.example.assignment2.data.remote.dto.LoginRequest
import com.example.assignment2.data.remote.dto.LoginResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class AuthRepositoryImplTest {

    private val apiService: ApiService = mockk()
    private val repository = AuthRepositoryImpl(apiService = apiService, classLocation = "footscray")

    @Test
    fun `returns the keypass when the login works`() = runTest {
        coEvery { apiService.login(any(), any()) } returns LoginResponse(keypass = "animals")

        val result = repository.login("12345678", "Alex")

        assertEquals(ApiResult.Success("animals"), result)
    }

    @Test
    fun `sends the student id and first name to my class location`() = runTest {
        coEvery { apiService.login(any(), any()) } returns LoginResponse(keypass = "animals")

        repository.login("12345678", "Alex")

        coVerify { apiService.login("footscray", LoginRequest(username = "12345678", password = "Alex")) }
    }

    @Test
    fun `401 from the api means the details were wrong`() = runTest {
        coEvery { apiService.login(any(), any()) } throws httpError(401)

        assertEquals(ApiResult.Error(ErrorReason.WRONG_DETAILS), repository.login("12345678", "alex"))
    }

    @Test
    fun `400 from the api also means the details were wrong`() = runTest {
        coEvery { apiService.login(any(), any()) } throws httpError(400)

        assertEquals(ApiResult.Error(ErrorReason.WRONG_DETAILS), repository.login("12345678", "alex"))
    }

    @Test
    fun `500 from the api is a server error`() = runTest {
        coEvery { apiService.login(any(), any()) } throws httpError(500)

        assertEquals(ApiResult.Error(ErrorReason.SERVER), repository.login("12345678", "Alex"))
    }

    @Test
    fun `no internet is a connection error`() = runTest {
        coEvery { apiService.login(any(), any()) } throws IOException("no internet")

        assertEquals(ApiResult.Error(ErrorReason.NO_CONNECTION), repository.login("12345678", "Alex"))
    }

    private fun httpError(code: Int) = HttpException(Response.error<Any>(code, "error".toResponseBody(null)))
}
