// app/src/main/java/com/project/bansigo_rider/core/models/AuthModels.kt
package com.project.bansigo_rider.core.models

data class TokenResponse(
    val accessToken: String,
    val tokenType: String = "bearer"
)

data class UserProfile(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String?,
    val username: String,
    val role: String,
    val picture: String? = null,
    val isVerified: Boolean = false,
    val isPhoneVerified: Boolean = true
)

data class LoginResult(
    val token: TokenResponse,
    val user: UserProfile? = null
)
