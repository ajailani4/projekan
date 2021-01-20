package com.ajailani.projekan.di.module

import com.ajailani.projekan.data.api.ApiService
import com.ajailani.projekan.data.repository.FirebaseRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class ApplicationModule {

    @Provides
    fun provideBaseUrl() = "https://projekan-142f8-default-rtdb.firebaseio.com/"

    @Provides
    @Singleton
    fun provideRetrofit(BASE_URL: String): Retrofit =
        Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create().asLenient())
            .baseUrl(BASE_URL)
            .build()

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService =
        retrofit.create(ApiService::class.java)

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth =
        FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage =
        FirebaseStorage.getInstance()

    @Provides
    @Singleton
    fun provedDatabaseReference(): DatabaseReference =
        FirebaseDatabase.getInstance().reference
}