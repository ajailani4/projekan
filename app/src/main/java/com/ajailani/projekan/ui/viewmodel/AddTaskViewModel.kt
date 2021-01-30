package com.ajailani.projekan.ui.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ajailani.projekan.data.model.Project
import com.ajailani.projekan.data.model.Task
import com.ajailani.projekan.data.repository.FirebaseRepository

class AddTaskViewModel @ViewModelInject constructor(
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {
    private val mutableProject = MutableLiveData<Project>()
    val project: LiveData<Project> get() = mutableProject

    fun setProject(project: Project) {
        mutableProject.value = project
    }

    fun addTask(task: Task, page: Int, itemNum: Int) = firebaseRepository.addTask(task, page, itemNum)
}