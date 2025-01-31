package com.example.bookworm.domain.models

data class AuthApiResponse(
    val token: String,
    val user: User,
    val message:String
)
data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String
)