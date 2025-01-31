package com.example.bookworm.data.repository

import com.example.bookworm.data.api.AuthApiService
import com.example.bookworm.domain.models.AuthApiResponse
import com.example.bookworm.domain.models.LoginRequest
import com.example.bookworm.domain.models.RegisterRequest
import com.example.bookworm.domain.repository.AuthenticationRepository
import com.example.bookworm.utils.NetworkResponse
import com.example.bookworm.utils.TokenManager
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject


class AuthenticationRepositoryImpl @Inject constructor(
    private val authApiService: AuthApiService,
    private val tokenManager: TokenManager
) : AuthenticationRepository {


    override suspend fun isUserAuthenticated(): Boolean {
        return tokenManager.getAuthToken() != null
    }

    override suspend fun signIn(email: String, password: String): NetworkResponse<AuthApiResponse> {
        return runCatching {

            val loginRequest = LoginRequest(email, password)
            val response = authApiService.loginUser(loginRequest)

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {

                    tokenManager.saveAuthToken(body.token)
                    tokenManager.saveUserData(body.user)

                    NetworkResponse.Success(body)
                } else {
                    NetworkResponse.Error("Response body is null")
                }
            } else {
                val httpException = HttpException(response)
                val errorMessage = parseErrorMessage(httpException)
                NetworkResponse.Error(errorMessage)
            }
        }.getOrElse { exception ->
            // Handle exceptions gracefully
            when (exception) {
                is IOException -> NetworkResponse.Error("Check your internet connection")
                is HttpException -> parseErrorMessage(exception).let {
                    NetworkResponse.Error(it)
                }
                else -> NetworkResponse.Error("Unknown error: ${exception.localizedMessage}")
            }
        }
    }
    override suspend fun signUp(userName: String, email: String, password: String): NetworkResponse<AuthApiResponse> {
        return runCatching {
            val registerRequest = RegisterRequest(userName, email, password)
            val response = authApiService.registerUser(registerRequest)

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    tokenManager.saveAuthToken(body.token)
                    tokenManager.saveUserData(body.user)

                    NetworkResponse.Success(body)
                } else {
                    NetworkResponse.Error("Response body is null")
                }
            } else {
                // Convert the unsuccessful response to HttpException and parse it
                val httpException = HttpException(response)
                val errorMessage = parseErrorMessage(httpException)
                NetworkResponse.Error(errorMessage)
            }
        }.getOrElse { exception ->
            when (exception) {
                is IOException -> NetworkResponse.Error("Check your internet connection")
                is HttpException -> NetworkResponse.Error(parseErrorMessage(exception))
                else -> NetworkResponse.Error("Unknown error: ${exception.localizedMessage}")
            }
        }
    }
    override suspend fun signOut(): NetworkResponse<Boolean> {
        return runCatching {
            tokenManager.clearAuthToken()
            NetworkResponse.Success(true)
        }.getOrElse { exception ->
            // Handle exceptions gracefully
            when (exception) {
                is IOException -> NetworkResponse.Error("Check your internet connection")
                else -> NetworkResponse.Error("Unknown error: ${exception.localizedMessage}")
            }
        }
    }

    private suspend fun parseErrorMessage(exception: HttpException): String {
        return withContext(Dispatchers.IO) {
            try {
                val errorJson = exception.response()?.errorBody()?.string()
                val jsonObject = Gson().fromJson(errorJson, JsonObject::class.java)
                jsonObject.get("message")?.asString ?: "An unknown error occurred"
            } catch (e: Exception) {
                "An unknown error occurred"
            }
        }
    }

}
