package com.vc.sample.data.model

/**
 * Domain model used in Presentation / UI layer.
 */
data class User(
    val id: Int,
    val name: String,
    val username: String,
    val email: String,
    val phone: String,
    val website: String,
    val companyName: String,
    val avatarUrl: String = "https://i.pravatar.cc/150?u=$id"
)
