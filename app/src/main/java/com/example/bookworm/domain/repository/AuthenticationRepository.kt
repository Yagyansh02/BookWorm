package com.example.bookworm.domain.repository

import com.example.bookworm.domain.models.AuthApiResponse
import com.example.bookworm.utils.NetworkResponse

interface AuthenticationRepository {

    suspend fun isUserAuthenticated(): Boolean
    suspend fun signIn(email: String, password: String): NetworkResponse<AuthApiResponse>
    suspend fun signUp(userName: String ,email: String, password: String, ): NetworkResponse<AuthApiResponse>
    suspend fun signOut(): NetworkResponse<Boolean>

}