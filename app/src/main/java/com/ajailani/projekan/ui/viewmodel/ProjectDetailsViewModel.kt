package com.ajailani.projekan.ui.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.ajailani.projekan.data.model.Project
import com.ajailani.projekan.data.repository.FirebaseRepository

class ProjectDetailsViewModel @ViewModelInject constructor(
    private val firebaseRepository: FirebaseRepository
): ViewModel() {
    private var project: LiveData<Project?>? = null
    private var projectProgress: LiveData<Int>? = null

    fun getProjectDetails(page: Int, itemNum: Int): LiveData<Project?>?  {
        project = firebaseRepository.getProjectDetails(page, itemNum)

        return project
    }

    fun getProjectProgress(page: Int, itemNum: Int): LiveData<Int>? {
        projectProgress = firebaseRepository.getProjectProgress(page, itemNum)

        return projectProgress
    }
}