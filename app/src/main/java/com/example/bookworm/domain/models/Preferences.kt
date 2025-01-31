package com.example.bookworm.domain.models


data class Preferences(
    val darkMode: Boolean,
    val emailNotifications: Boolean,
    val favoriteGenres: List<String>
)