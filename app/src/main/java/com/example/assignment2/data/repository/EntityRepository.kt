package com.example.assignment2.data.repository

import com.example.assignment2.data.model.ApiResult
import com.example.assignment2.data.model.Dashboard

// gets the entities for a keypass from the dashboard endpoint
interface EntityRepository {
    suspend fun getDashboard(keypass: String): ApiResult<Dashboard>
}
