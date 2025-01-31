package com.example.bookworm.presentation.Main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookworm.data.repository.BookRepositoryImpl
import com.example.bookworm.domain.models.BooksResponse
import com.example.bookworm.domain.models.BooksResponseItem
import com.example.bookworm.domain.models.GoogleBooksApiResponse
import com.example.bookworm.domain.models.User
import com.example.bookworm.utils.NetworkResponse
import com.example.bookworm.utils.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookViewModel @Inject constructor(
    private val repository: BookRepositoryImpl,
    private val tokenManager: TokenManager,
) : ViewModel() {

    private val _userdata = MutableStateFlow<User?>(null)
    val userdata= _userdata.asStateFlow()

    private val _searchBooksResults = MutableStateFlow<NetworkResponse<GoogleBooksApiResponse?>>(
        NetworkResponse.Success(null))
    val searchBooksResults = _searchBooksResults.asStateFlow()

    init {
        getUserData()
    }

    private fun getUserData() {
        viewModelScope.launch {
            _userdata.value = tokenManager.getUserData()
        }
    }

    private val _categoryState = MutableStateFlow(
        Category(
            currentlyReading = NetworkResponse.Loading,
            wantToRead = NetworkResponse.Loading,
            read = NetworkResponse.Loading,
            error = null
        )
    )

    val categoryState: StateFlow<Category> = _categoryState.asStateFlow()

    fun fetchAllBooksByCategories(userId: String) {
        viewModelScope.launch {
            repository.fetchAllBooksByCategory(userId)
                .catch { error ->
                    _categoryState.value = Category(
                        currentlyReading = NetworkResponse.Error(error.message ?: "An unexpected error occurred"),
                        wantToRead = NetworkResponse.Error(error.message ?: "An unexpected error occurred"),
                        read = NetworkResponse.Error(error.message ?: "An unexpected error occurred"),
                        error = error.message
                    )
                }
                .collect { category ->
                    _categoryState.value = category
                }
        }
    }
    fun searchBooks(query: String) {
        viewModelScope.launch {
            repository.searchBooks(query)
                .catch { error ->
                    _searchBooksResults.value = NetworkResponse.Error(error.message ?: "An unexpected error occurred")
                }
                .collect { response ->
                    _searchBooksResults.value = response
                }
        }
    }
    fun saveBookToCategory(userId: String, book: BooksResponseItem, category: String): String {
        var result = ""
        viewModelScope.launch {
            val response = repository.saveBookToCategory(userId, book, category)
            if (response is NetworkResponse.Success) {
                result = response.data
                Log.d("BookViewModel", "saveBookToCategory: $result")
            } else if (response is NetworkResponse.Error) {
                result = response.message
            }
        }
        return result
    }
}
data class Category(
    val currentlyReading: NetworkResponse<BooksResponse>,
    val wantToRead: NetworkResponse<BooksResponse>,
    val read: NetworkResponse<BooksResponse>,
    val error: String? = null
)