package com.ajailani.projekan.ui.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.ajailani.projekan.data.model.Project
import com.ajailani.projekan.data.repository.FirebaseRepository

class AddProjectViewModel @ViewModelInject constructor(
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {
    private var uploadProjectIcon: LiveData<String>? = null
    private var isProjectAdded: LiveData<Boolean>? = null
    private var isProjectUpdated: LiveData<Boolean>? = null

    fun uploadProjectIcon(bytes: ByteArray): LiveData<String>? {
        uploadProjectIcon = firebaseRepository.uploadProjectIcon(bytes)

        return uploadProjectIcon
    }

    fun addProject(project: Project, iconUrl: String): LiveData<Boolean>? {
        isProjectAdded = firebaseRepository.addProject(project, iconUrl)

        return isProjectAdded
    }

    fun updateProject(project: Project, iconUrl: String): LiveData<Boolean>? {
        isProjectUpdated = firebaseRepository.updateProject(project, iconUrl)

        return isProjectUpdated
    }
}