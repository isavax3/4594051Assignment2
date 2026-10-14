package com.example.assignment2.data.repository

import com.example.assignment2.data.model.ApiResult

// logs in and gives back the keypass. it's an interface so the viewmodel doesn't care
// where the data comes from, and the tests can use a mock instead
interface AuthRepository {
    suspend fun login(studentId: String, firstName: String): ApiResult<String>
}
