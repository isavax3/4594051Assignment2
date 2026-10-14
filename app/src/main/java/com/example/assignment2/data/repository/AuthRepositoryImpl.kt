package com.example.assignment2.data.repository

import com.example.assignment2.data.model.ApiResult
import com.example.assignment2.data.model.ErrorReason
import com.example.assignment2.data.remote.ApiService
import com.example.assignment2.data.remote.dto.LoginRequest
import com.example.assignment2.di.ClassLocation
import javax.inject.Inject

// the real AuthRepository. Hilt injects the api service and my class location
class AuthRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    @ClassLocation private val classLocation: String
) : AuthRepository {

    override suspend fun login(studentId: String, firstName: String): ApiResult<String> =
        safeApiCall(httpErrorReason = ::loginErrorReason) {
            val request = LoginRequest(username = studentId, password = firstName)
            apiService.login(classLocation, request).keypass
        }

    // for login, a 4xx error means the student id or name was wrong
    private fun loginErrorReason(code: Int): ErrorReason = when (code) {
        in 400..499 -> ErrorReason.WRONG_DETAILS
        else -> defaultHttpErrorReason(code)
    }
}
