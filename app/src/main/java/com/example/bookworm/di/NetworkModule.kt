package com.example.bookworm.di

import com.example.bookworm.utils.Constants.GOOGLE_BASE_URL
import com.example.bookworm.utils.Constants.MONGO_BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton
import com.example.bookworm.data.api.BooksApiService
import com.example.bookworm.data.api.AuthApiService
import com.example.bookworm.data.api.GoogleBooksApiService

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    @Named("Mongo")
    fun provideMongoRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(MONGO_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    @Provides
    @Singleton
    @Named("Google")
    fun provideGoogleRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(GOOGLE_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideBooksApiService(@Named("Mongo")retrofit: Retrofit): BooksApiService {
        return retrofit.create(BooksApiService::class.java)
    }
    @Provides
    @Singleton
    fun provideAuthApiService(@Named("Mongo")retrofit: Retrofit): AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideGoogleBooksApiService(@Named("Google") retrofit: Retrofit): GoogleBooksApiService {
        return retrofit.create(GoogleBooksApiService::class.java)
    }
}