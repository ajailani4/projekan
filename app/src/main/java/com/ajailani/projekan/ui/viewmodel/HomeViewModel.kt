package com.ajailani.projekan.ui.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
    private var userName: LiveData<String>? = null
    private var userAva: LiveData<String>? = null
    private var deadlineProjects: LiveData<MutableList<Project>>? = null
    private var myProjects: LiveData<PagingData<Project>>? = null

    fun getUserName(): LiveData<String>? {
        userName = firebaseRepository.getUserName()

        return userName
    }

    fun getUserAva(): LiveData<String>? {
        userAva = firebaseRepository.getUserAva()

        return userAva
    }

    fun getDeadlinedProjectsHeader(): LiveData<MutableList<Project>>? {
        deadlineProjects = firebaseRepository.getDeadlinedProjectsHeader()

        return deadlineProjects
    }

    fun getMyProjects(): LiveData<PagingData<Project>>? {
        myProjects = firebaseRepository.getMyProjects().cachedIn(viewModelScope)

        return myProjects
    }
}