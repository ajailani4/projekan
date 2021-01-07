package com.ajailani.projekan.ui.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ajailani.projekan.data.datasource.MyProjectsDatasource
import com.ajailani.projekan.data.model.Project
import com.ajailani.projekan.data.repository.FirebaseRepository

class HomeViewModel @ViewModelInject constructor(
    private val firebaseRepository: FirebaseRepository,
) : ViewModel() {
    private var myProjects: LiveData<PagingData<Project>>? = null

    fun getMyProjects(): LiveData<PagingData<Project>>? {
        myProjects = firebaseRepository.getMyProjects().cachedIn(viewModelScope)

        return myProjects
    }
}