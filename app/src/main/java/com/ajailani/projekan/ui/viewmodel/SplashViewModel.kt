package com.ajailani.projekan.ui.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.ajailani.projekan.data.repository.FirebaseRepository

class SplashViewModel @ViewModelInject constructor(
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {
    private var userAuth: LiveData<Boolean>? = null

    fun checkUserAuth(): LiveData<Boolean>? {
        userAuth = firebaseRepository.checkUserAuth()

        return userAuth
    }
}