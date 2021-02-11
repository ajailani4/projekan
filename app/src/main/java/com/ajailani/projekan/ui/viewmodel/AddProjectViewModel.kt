package com.ajailani.projekan.ui.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.ajailani.projekan.data.model.Project
import com.ajailani.projekan.data.repository.FirebaseRepository

class AddProjectViewModel @ViewModelInject constructor(
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {
    fun uploadProjectIcon(bytes: ByteArray) =
        firebaseRepository.uploadProjectIcon(bytes)

    fun addProject(project: Project, iconUrl: String) =
        firebaseRepository.addProject(project, iconUrl)

    fun updateProject(project: Project, iconUrl: String) =
        firebaseRepository.updateProject(project, iconUrl)
}