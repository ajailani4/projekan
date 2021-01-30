package com.ajailani.projekan.ui.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.ajailani.projekan.data.repository.FirebaseRepository

class HomeViewModel @ViewModelInject constructor(
    private val firebaseRepository: FirebaseRepository,
) : ViewModel() {
    fun getUserName() = firebaseRepository.getUserName()

    fun getUserAva() = firebaseRepository.getUserAva()

    fun getDeadlinedProjectsHeader() = firebaseRepository.getDeadlinedProjectsHeader()

    fun getMyProjects() = firebaseRepository.getMyProjects().cachedIn(viewModelScope)
}