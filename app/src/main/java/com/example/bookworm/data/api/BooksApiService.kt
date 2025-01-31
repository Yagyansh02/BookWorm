package com.example.bookworm.data.api

import com.example.bookworm.domain.models.BooksResponse
import com.example.bookworm.domain.models.BooksResponseItem
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface BooksApiService {
    @GET("api/books/{userId}/category/{category}")
    suspend fun getBooksByCategory(
        @Path("userId") userId: String,
        @Path("category") category: String
    ): Response<BooksResponse>


    @POST("api/books/category")
    suspend fun addBookToCategory(
        @Body request: AddBookToCategoryRequest
    ): Response<AddBookToCategoryResponse>
}

data class AddBookToCategoryRequest(
    val userId: String,
    val book: BooksResponseItem,
    val category: String
)

data class AddBookToCategoryResponse(
    val message: String
)
