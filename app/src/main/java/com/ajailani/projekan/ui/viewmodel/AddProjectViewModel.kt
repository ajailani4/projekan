package com.ajailani.projekan.ui.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.ajailani.projekan.data.model.Project
import com.ajailani.projekan.data.repository.FirebaseRepository

class AddProjectViewModel @ViewModelInject constructor(
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {
    private var isProjectAdded: LiveData<Boolean>? = null
    private var uploadProjectIcon: LiveData<String>? = null

    fun addProject(project: Project, iconUrl: String): LiveData<Boolean>? {
        isProjectAdded = firebaseRepository.addProject(project, iconUrl)

        return isProjectAdded
    }

    fun uploadProjectIcon(bytes: ByteArray): LiveData<String>? {
        uploadProjectIcon = firebaseRepository.uploadProjectIcon(bytes)

        return uploadProjectIcon
    }
}