package com.vc.standalone.data.model

import com.google.gson.annotations.SerializedName

/**
 * Domain model representing a user.
 */
data class StandaloneUser(
    val id: Int,
    val name: String,
    val email: String,
    val username: String,
    val phone: String,
    val website: String,
    val companyName: String,
    val city: String,
    val avatarUrl: String = "https://i.pravatar.cc/150?u=$id"
)

/**
 * Network Data Transfer Object matching JSONPlaceholder /users response.
 */
data class StandaloneUserDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String,
    @SerializedName("phone") val phone: String?,
    @SerializedName("website") val website: String?,
    @SerializedName("company") val company: CompanyDto?,
    @SerializedName("address") val address: AddressDto?
) {
    fun toDomain(): StandaloneUser {
        return StandaloneUser(
            id = id,
            name = name,
            email = email,
            username = username,
            phone = phone ?: "N/A",
            website = website ?: "N/A",
            companyName = company?.name ?: "Independent",
            city = address?.city ?: "Unknown"
        )
    }
}

data class CompanyDto(
    @SerializedName("name") val name: String?
)

data class AddressDto(
    @SerializedName("city") val city: String?,
    @SerializedName("street") val street: String?
)
