package com.vc.sample.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.vc.sample.data.model.User
import com.vc.sample.data.model.UserDto

/**
 * Room Entity stored in local SQLite database.
 */
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    val username: String,
    val email: String,
    val phone: String,
    val website: String,
    val companyName: String
) {
    fun toDomain(): User = User(
        id = id,
        name = name,
        username = username,
        email = email,
        phone = phone,
        website = website,
        companyName = companyName
    )
}

fun UserDto.toEntity(): UserEntity = UserEntity(
    id = id,
    name = name,
    username = username,
    email = email,
    phone = phone ?: "",
    website = website ?: "",
    companyName = company?.name ?: ""
)

fun User.toEntity(): UserEntity = UserEntity(
    id = id,
    name = name,
    username = username,
    email = email,
    phone = phone,
    website = website,
    companyName = companyName
)
