package com.example.bookworm.data.api

import com.example.bookworm.domain.models.GoogleBooksApiResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleBooksApiService {
    @GET("volumes")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("key") apiKey: String
    ): Response<GoogleBooksApiResponse>
}