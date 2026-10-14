package com.example.assignment2.data.repository

import com.example.assignment2.data.mapper.EntityMapper
import com.example.assignment2.data.model.ApiResult
import com.example.assignment2.data.model.Dashboard
import com.example.assignment2.data.remote.ApiService
import javax.inject.Inject

// the real EntityRepository. calls the api then uses the mapper to turn the raw json
// maps into Entity objects
class EntityRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val entityMapper: EntityMapper
) : EntityRepository {

    override suspend fun getDashboard(keypass: String): ApiResult<Dashboard> = safeApiCall {
        val response = apiService.getDashboard(keypass)
        val entities = response.entities.map { entityMapper.map(it) }
        // use entityTotal from the api, or count the list if it's missing
        Dashboard(entities = entities, total = response.entityTotal ?: entities.size)
    }
}
