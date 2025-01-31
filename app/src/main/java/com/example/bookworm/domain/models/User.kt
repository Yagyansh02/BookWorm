package com.example.bookworm.domain.models

data class User(
    val email: String,
    val id: String,
    val preferences: Preferences,
    val profileImage: Any,
    val username: String
)