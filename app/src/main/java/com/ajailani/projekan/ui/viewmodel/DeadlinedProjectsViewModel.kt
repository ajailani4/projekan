package com.ajailani.projekan.ui.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.ajailani.projekan.data.repository.FirebaseRepository
import com.ajailani.projekan.utils.NetworkHelper

class DeadlinedProjectsViewModel @ViewModelInject constructor(
    private val firebaseRepository: FirebaseRepository,
    private val networkHelper: NetworkHelper
) : ViewModel() {
    fun isNetworkConnected() = networkHelper.isNetworkConnected()

    fun getDeadlinedProjects() = firebaseRepository.getDeadlinedProjects().cachedIn(viewModelScope)
}