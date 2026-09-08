package com.vc.sample.data.api

import com.vc.androidcore.network.ApiService
import com.vc.sample.data.model.UserDto
import retrofit2.Response
import retrofit2.http.GET

/**
 * Retrofit API interface for fetching users from public JSONPlaceholder API.
 */
interface UserApiService : ApiService {

    @GET("users")
    suspend fun getUsers(): Response<List<UserDto>>
}
