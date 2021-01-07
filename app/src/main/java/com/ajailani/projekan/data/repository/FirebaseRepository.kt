package com.ajailani.projekan.data.repository

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.ajailani.projekan.data.api.ApiService
import com.ajailani.projekan.data.datasource.MyProjectsDatasource
import com.ajailani.projekan.data.model.Project
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import java.util.*
import javax.inject.Inject

class FirebaseRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val apiService: ApiService
) {
    //Check user authentication
    fun checkUserAuth(): MutableLiveData<Boolean> {
        val userAuth = MutableLiveData<Boolean>()

        userAuth.value = firebaseAuth.currentUser != null

        return userAuth
    }

    //Login with Google Account
    fun loginWithGoogle(): MutableLiveData<Intent> {
        val loginIntent = MutableLiveData<Intent>()
        loginIntent.value = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(Collections.singletonList(AuthUI.IdpConfig.GoogleBuilder().build()))
            .build()

        return loginIntent
    }

    //Get my projects list
    fun getMyProjects(): LiveData<PagingData<Project>> {
        return Pager(
            config = PagingConfig(enablePlaceholders = false, pageSize = 5),
            pagingSourceFactory = {
                MyProjectsDatasource(apiService, firebaseAuth)
            }
        ).liveData
    }
}