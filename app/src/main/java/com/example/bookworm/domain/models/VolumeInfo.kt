package com.example.bookworm.domain.models

data class VolumeInfo(
    val title: String,
    val authors: List<String>,
    val description: String,
    val imageLinks: ImageLinks,
    val language: String,
    val pageCount: Int,
    val publishedDate: String,
    val publisher: String,
)