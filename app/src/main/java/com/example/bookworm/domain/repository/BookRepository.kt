package com.example.bookworm.domain.repository

import com.example.bookworm.domain.models.BooksResponseItem
import com.example.bookworm.domain.models.GoogleBooksApiResponse
import com.example.bookworm.presentation.Main.Category
import com.example.bookworm.utils.NetworkResponse
import kotlinx.coroutines.flow.Flow

interface BookRepository {
    suspend fun searchBooks(query: String): Flow<NetworkResponse<GoogleBooksApiResponse>>
    suspend fun saveBookToCategory(userId: String, book: BooksResponseItem, category: String): NetworkResponse<String>
    suspend fun fetchAllBooksByCategory(userId: String): Flow<Category>
}