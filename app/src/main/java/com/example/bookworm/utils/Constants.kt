package com.example.bookworm.utils

import com.example.bookworm.BuildConfig

object Constants {
    @Suppress("AuthLeak")
    const val MONGO_BASE_URL = "http://192.168.36.196:3000/"
    const val GOOGLE_BASE_URL = "https://www.googleapis.com/books/v1/"
    fun getApiKey(): String {
        return BuildConfig.API_KEY
    }
}