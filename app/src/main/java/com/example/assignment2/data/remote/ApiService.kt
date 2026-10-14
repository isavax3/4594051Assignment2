package com.example.assignment2.data.remote

import com.example.assignment2.data.remote.dto.DashboardResponse
import com.example.assignment2.data.remote.dto.LoginRequest
import com.example.assignment2.data.remote.dto.LoginResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

// the two endpoints of the nit3213 api. Retrofit makes the actual class from this interface.
interface ApiService {

    // POST /{location}/auth with the student id and first name, gives back a keypass
    @POST("{location}/auth")
    suspend fun login(
        @Path("location") location: String,
        @Body request: LoginRequest
    ): LoginResponse

    // GET /dashboard/{keypass}, gives back the list of entities for my topic
    @GET("dashboard/{keypass}")
    suspend fun getDashboard(@Path("keypass") keypass: String): DashboardResponse
}
