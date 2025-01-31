package com.example.bookworm.data.api

import com.example.bookworm.domain.models.AuthApiResponse
import com.example.bookworm.domain.models.LoginRequest
import com.example.bookworm.domain.models.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    @POST("api/users/login")
    suspend fun loginUser(
        @Body loginRequest: LoginRequest
    ): Response<AuthApiResponse>

    @POST("api/users/register")
    suspend fun registerUser(
        @Body registerRequest: RegisterRequest
    ): Response<AuthApiResponse>
}

