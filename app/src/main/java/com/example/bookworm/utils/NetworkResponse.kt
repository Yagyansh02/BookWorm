package com.example.bookworm.utils

sealed class NetworkResponse<out T> {
    object Loading : NetworkResponse<Nothing>()

    data class Success<out T>(
        val data: T
    ) : NetworkResponse<T>()

    data class Error(
        val message: String
    ) : NetworkResponse<Nothing>()
}