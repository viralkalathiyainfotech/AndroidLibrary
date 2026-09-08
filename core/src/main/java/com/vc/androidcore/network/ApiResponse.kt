package com.vc.androidcore.network

import com.google.gson.annotations.SerializedName

/**
 * Standard API response model commonly returned by REST backends.
 * Applications may use this or define their own models.
 */
data class ApiResponse<T>(
    @SerializedName("status")
    val status: Boolean = true,

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("data")
    val data: T? = null,

    @SerializedName("code")
    val code: Int? = null
)
