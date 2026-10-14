package com.example.assignment2.data.repository

import com.example.assignment2.data.mapper.EntityMapper
import com.example.assignment2.data.model.ApiResult
import com.example.assignment2.data.model.Dashboard
import com.example.assignment2.data.model.ErrorReason
import com.example.assignment2.data.remote.ApiService
import com.example.assignment2.data.remote.dto.DashboardResponse
import com.squareup.moshi.JsonDataException
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

class EntityRepositoryImplTest {

    private val apiService: ApiService = mockk()

    // the real mapper is used here (it has no dependencies) so this also checks the mapping
    private val repository = EntityRepositoryImpl(apiService = apiService, entityMapper = EntityMapper())

    @Test
    fun `turns the api response into entities and uses entityTotal`() = runTest {
        // Given
        coEvery { apiService.getDashboard("animals") } returns DashboardResponse(
            entities = listOf(
                mapOf("name" to "Koala", "lifespan" to 18.0, "description" to "A tree-dwelling marsupial."),
                mapOf("name" to "Wombat", "lifespan" to 15.0, "description" to "Digs burrows.")
            ),
            entityTotal = 2
        )

        // When
        val result = repository.getDashboard("animals")

        // Then
        assertTrue(result is ApiResult.Success)
        val dashboard = (result as ApiResult.Success<Dashboard>).data
        assertEquals(2, dashboard.total)
        assertEquals(listOf("Koala", "Wombat"), dashboard.entities.map { it.title })
        assertEquals("18", dashboard.entities[0].fields[1].value)
        assertEquals("A tree-dwelling marsupial.", dashboard.entities[0].description)
    }

    @Test
    fun `counts the list when entityTotal is missing`() = runTest {
        coEvery { apiService.getDashboard("animals") } returns DashboardResponse(
            entities = listOf(mapOf("name" to "Koala"), mapOf("name" to "Wombat"), mapOf("name" to "Emu")),
            entityTotal = null
        )

        val result = repository.getDashboard("animals") as ApiResult.Success<Dashboard>

        assertEquals(3, result.data.total)
    }

    @Test
    fun `404 means the topic wasn't found`() = runTest {
        coEvery { apiService.getDashboard(any()) } throws
            HttpException(Response.error<Any>(404, "not found".toResponseBody(null)))

        assertEquals(ApiResult.Error(ErrorReason.NOT_FOUND), repository.getDashboard("nope"))
    }

    @Test
    fun `json that can't be read is an unknown error`() = runTest {
        coEvery { apiService.getDashboard(any()) } throws JsonDataException("Required value 'entities' missing")

        assertEquals(ApiResult.Error(ErrorReason.UNKNOWN), repository.getDashboard("animals"))
    }
}
