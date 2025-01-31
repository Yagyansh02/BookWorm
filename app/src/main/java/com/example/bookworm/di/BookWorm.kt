package com.example.bookworm.di

import android.content.Context
import com.example.bookworm.data.api.AuthApiService
import com.example.bookworm.data.api.BooksApiService
import com.example.bookworm.data.api.GoogleBooksApiService
import com.example.bookworm.data.repository.AuthenticationRepositoryImpl
import com.example.bookworm.data.repository.BookRepositoryImpl
import com.example.bookworm.utils.TokenManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object BookWorm {
    @Provides
    @Singleton
    fun provideBookRepository(booksApiService: BooksApiService,
                              googleBooksApiService: GoogleBooksApiService
    ): BookRepositoryImpl {
        return BookRepositoryImpl(booksApiService,googleBooksApiService)
    }
    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context {
        return context
    }

    @Provides
    @Singleton
    fun provideTokenManager(context: Context): TokenManager {
        return TokenManager(context)
    }

    @Provides
    @Singleton
    fun provideAuthenticationRepository(authApiService: AuthApiService,
                                        tokenManager: TokenManager
    ): AuthenticationRepositoryImpl {
        return AuthenticationRepositoryImpl(authApiService, tokenManager)
    }
}