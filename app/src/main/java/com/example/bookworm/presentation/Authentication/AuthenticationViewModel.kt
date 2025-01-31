package com.example.bookworm.presentation.Authentication

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookworm.data.repository.AuthenticationRepositoryImpl
import com.example.bookworm.domain.models.AuthApiResponse
import com.example.bookworm.utils.NetworkResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthenticationViewModel @Inject constructor(
    private val repository: AuthenticationRepositoryImpl
): ViewModel() {

    private val _authState = MutableStateFlow<NetworkResponse<AuthApiResponse>?>(null)
    val authState: StateFlow<NetworkResponse<AuthApiResponse>?> = _authState
    private val _isUserAuthenticated = MutableStateFlow(false)
    val isUserAuthenticated: StateFlow<Boolean> = _isUserAuthenticated

    fun isUserAuthenticated() {
        viewModelScope.launch {
            _isUserAuthenticated.value = repository.isUserAuthenticated()
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            try {
                _authState.value = NetworkResponse.Loading
                val response = repository.signIn(email, password)
                _authState.value = response
            } catch (e: Exception) {
                _authState.value = NetworkResponse.Error("Sign-in failed: ${e.localizedMessage}")
                Log.d("AuthenticationViewModel", "signIn: ${e.localizedMessage}")
            }
        }
    }
    fun signUp(userName: String, email: String, password: String) {
        viewModelScope.launch {
            try {
                _authState.value = NetworkResponse.Loading
                val response = repository.signUp(userName, email, password)
                _authState.value = response
            } catch (e: Exception) {
                _authState.value = NetworkResponse.Error("Sign-up failed: ${e.localizedMessage}")
                Log.d("AuthenticationViewModel", "signUp: ${e.localizedMessage}")
            }
        }
    }


}