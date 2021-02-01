package com.ajailani.projekan.ui.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.ajailani.projekan.data.repository.FirebaseRepository

class SplashViewModel @ViewModelInject constructor(
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {
    fun checkUserAuth() = firebaseRepository.checkUserAuth()
}