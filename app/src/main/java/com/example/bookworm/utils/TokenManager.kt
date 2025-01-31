package com.example.bookworm.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.bookworm.domain.models.User
import com.google.gson.Gson
import javax.inject.Inject

class TokenManager @Inject constructor(context: Context) {

    private val preferences: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun saveAuthToken(token: String) {
        preferences.edit().putString(KEY_AUTH_TOKEN, token).apply()
    }
    fun getAuthToken(): String? {
        return preferences.getString(KEY_AUTH_TOKEN, null)
    }

    fun saveUserData(user: User) {
        val gson = Gson()
        preferences.edit()
            .putString(KEY_USER_DATA, gson.toJson(user))
            .apply()
    }
    fun getUserData(): User? {
        val userData = preferences.getString(KEY_USER_DATA, null)
        return if (userData != null) {
            Gson().fromJson(userData, User::class.java)
        } else null
    }
    fun clearAuthToken() {
        preferences.edit().remove(KEY_AUTH_TOKEN).apply()
    }
    companion object {
        private const val PREF_NAME = "auth_prefs"
        private const val KEY_AUTH_TOKEN = "auth_token"
        private const val KEY_USER_DATA = "user_data"
    }
}