package com.vc.standalone.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.vc.standalone.data.model.StandaloneUser

/**
 * Room entity representing a locally cached user.
 */
@Entity(tableName = "standalone_users")
data class StandaloneUserEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    val email: String,
    val username: String,
    val phone: String,
    val website: String,
    val companyName: String,
    val city: String,
    val avatarUrl: String
) {
    fun toDomain(): StandaloneUser {
        return StandaloneUser(
            id = id,
            name = name,
            email = email,
            username = username,
            phone = phone,
            website = website,
            companyName = companyName,
            city = city,
            avatarUrl = avatarUrl
        )
    }

    companion object {
        fun fromDomain(user: StandaloneUser): StandaloneUserEntity {
            return StandaloneUserEntity(
                id = user.id,
                name = user.name,
                email = user.email,
                username = user.username,
                phone = user.phone,
                website = user.website,
                companyName = user.companyName,
                city = user.city,
                avatarUrl = user.avatarUrl
            )
        }
    }
}
