package com.ajailani.projekan.ui.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ajailani.projekan.data.model.Project
import com.ajailani.projekan.data.repository.FirebaseRepository

/** This ViewModel is just used to pass data from ProjectDetailsActivity to MoreFragment */
class MoreViewModel @ViewModelInject constructor(
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {
    private val mutableTag = MutableLiveData<String>()
    val tag: LiveData<String> get() = mutableTag

    private val mutableProject = MutableLiveData<Project>()
    val project: LiveData<Project> get() = mutableProject

    private val mutIsProjectDeleted = MutableLiveData<Boolean>()
    val isProjectDeleted: LiveData<Boolean> get() = mutIsProjectDeleted

    fun setTag(tag: String) {
        mutableTag.value = tag
    }

    fun setProject(project: Project) {
        mutableProject.value = project
    }

    fun setDeleteProject(isProjectDeleted: Boolean) {
        mutIsProjectDeleted.value = isProjectDeleted
    }

    fun deleteProject(project: Project) = firebaseRepository.deleteProject(project)
}