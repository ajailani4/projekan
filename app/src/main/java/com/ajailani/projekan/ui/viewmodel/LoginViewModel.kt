package com.ajailani.projekan.ui.viewmodel

import android.content.Intent
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.ajailani.projekan.data.repository.FirebaseRepository

class LoginViewModel @ViewModelInject constructor(
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {
    private var loginIntent: LiveData<Intent>? = null

    fun loginWithGoogle(): LiveData<Intent>? {
        loginIntent = firebaseRepository.loginWithGoogle()

        return loginIntent
    }
}