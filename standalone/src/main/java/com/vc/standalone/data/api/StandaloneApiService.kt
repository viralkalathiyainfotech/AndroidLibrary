package com.vc.standalone.data.api

import com.vc.standalone.data.model.StandaloneUserDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Retrofit API contract for remote endpoints.
 */
interface StandaloneApiService {

    @GET("users")
    suspend fun getUsers(): Response<List<StandaloneUserDto>>

    @GET("users/{id}")
    suspend fun getUserById(@Path("id") id: Int): Response<StandaloneUserDto>
}
