package com.example.bookworm.data.repository

import android.util.Log
import com.example.bookworm.data.api.AddBookToCategoryRequest
import com.example.bookworm.data.api.BooksApiService
import com.example.bookworm.data.api.GoogleBooksApiService
import com.example.bookworm.domain.models.BooksResponse
import com.example.bookworm.domain.models.BooksResponseItem
import com.example.bookworm.domain.models.GoogleBooksApiResponse
import com.example.bookworm.domain.repository.BookRepository
import com.example.bookworm.presentation.Main.Category
import com.example.bookworm.utils.Constants
import com.example.bookworm.utils.NetworkResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class BookRepositoryImpl @Inject constructor(
    private val booksApiService: BooksApiService,
    private val googleBooksApiService: GoogleBooksApiService
): BookRepository
{
    private val apiKey = Constants.getApiKey()

    override suspend fun searchBooks(query: String): Flow<NetworkResponse<GoogleBooksApiResponse>> = flow {
        emit(NetworkResponse.Loading)
        val response = googleBooksApiService.searchBooks(query, apiKey = apiKey)
        if (response.isSuccessful) {
            response.body()?.let {
                emit(NetworkResponse.Success(it))
            } ?: emit(NetworkResponse.Error("Response body is null"))
        } else {
            emit(NetworkResponse.Error("Error: ${response.code()} - ${response.message()}"))
        }
    }

    override suspend fun saveBookToCategory(userId: String, book: BooksResponseItem, category: String): NetworkResponse<String> {
        return try {
            val response = booksApiService.addBookToCategory(AddBookToCategoryRequest(userId,book, category))
            if (response.isSuccessful) {
                Log.d("BookRepository", "saveBookToCategory: ${response.body()?.message}")
                NetworkResponse.Success("Book saved successfully")
            } else {
                NetworkResponse.Error("Error: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            when (e) {
                is IOException -> NetworkResponse.Error("Network error occurred. Please check your internet connection.")
                is HttpException -> NetworkResponse.Error("HTTP error occurred: ${e.code()}")
                else -> NetworkResponse.Error(e.message ?: "Unknown error occurred")
            }
        }
    }
    override suspend fun fetchAllBooksByCategory(userId: String): Flow<Category> = flow {
        // Emit loading state first
        emit(
            Category(
                currentlyReading = NetworkResponse.Loading,
                wantToRead = NetworkResponse.Loading,
                read = NetworkResponse.Loading,
                error = null
            )
        )

        try {
            coroutineScope {
                val currentlyReading = async { fetchBooksByCategory(userId, "currentlyReading") }
                val wantToRead = async { fetchBooksByCategory(userId, "wantToRead") }
                val read = async { fetchBooksByCategory(userId, "read") }

                val category = Category(
                    currentlyReading = currentlyReading.await(),
                    wantToRead = wantToRead.await(),
                    read = read.await(),
                    error = null
                )
                emit(category)
            }
        } catch (e: Exception) {
            val category = Category(
                currentlyReading = NetworkResponse.Error(e.message ?: "Unknown error occurred"),
                wantToRead = NetworkResponse.Error(e.message ?: "Unknown error occurred"),
                read = NetworkResponse.Error(e.message ?: "Unknown error occurred"),
                error = e.message
            )
            emit(category)
        }
    }.flowOn(Dispatchers.IO)

    private suspend fun fetchBooksByCategory(userId: String, category: String): NetworkResponse<BooksResponse> {
        return try {
            val response = booksApiService.getBooksByCategory(userId, category)
            if (response.isSuccessful) {
                response.body()?.let {
                    NetworkResponse.Success(it)
                } ?: NetworkResponse.Error("Response body is null")
            } else {
                NetworkResponse.Error("Error: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            when (e) {
                is IOException -> NetworkResponse.Error("Network error occurred. Please check your internet connection.")
                is HttpException -> NetworkResponse.Error("HTTP error occurred: ${e.code()}")
                else -> NetworkResponse.Error(e.message ?: "Unknown error occurred")
            }
        }
    }
}