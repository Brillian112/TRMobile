package com.example.tr.data.remote.model

import com.google.gson.annotations.SerializedName

data class User(
    val id: Long,
    val name: String,
    @SerializedName("role") val role: String,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?
)

data class LoginRequest(
    @SerializedName("nama") val name: String,
    val password: String
)

data class LoginResponse(
    val message: String?,
    val success: Boolean?,
    val user: User?,
    val token: String?
)

data class UserResponse(
    val success: Boolean,
    val message: String?,
    val data: List<User>
)

data class SingleUserResponse(
    val success: Boolean,
    val message: String?,
    val data: User
)

data class RegisterRequest(
    val name: String,
    val password: String,
    val role: String
)
