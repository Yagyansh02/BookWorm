package com.example.bookworm.domain.models

data class GoogleBooksApiResponse(
    val items: List<BooksResponseItem>
)