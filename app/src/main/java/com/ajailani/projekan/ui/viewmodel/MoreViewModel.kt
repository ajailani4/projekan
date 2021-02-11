package com.ajailani.projekan.ui.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ajailani.projekan.data.model.Project
import com.ajailani.projekan.data.model.Task
import com.ajailani.projekan.data.repository.FirebaseRepository

/** This ViewModel is used to pass data from ProjectDetailsActivity */
class MoreViewModel @ViewModelInject constructor(
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {
    private val mutableTag = MutableLiveData<String>()
    val tag: LiveData<String> get() = mutableTag

    private val mutableProject = MutableLiveData<Project>()
    val project: LiveData<Project> get() = mutableProject

    private val mutableTask = MutableLiveData<Task>()
    val task: LiveData<Task> get() = mutableTask

    private val mutableIsTaskDeleted = MutableLiveData<Boolean>()
    val isTaskDeleted: LiveData<Boolean> get() = mutableIsTaskDeleted

    fun setTag(tag: String) {
        mutableTag.value = tag
    }

    fun setProject(project: Project) {
        mutableProject.value = project
    }

    fun setTask(task: Task) {
        mutableTask.value = task
    }

    fun deleteProject(project: Project) =
        firebaseRepository.deleteProject(project)

    fun deleteTask(page: Int, itemNum: Int, task: Task) =
        firebaseRepository.deleteTask(page, itemNum, task)

    fun setDeleteTask(isTaskDeleted: Boolean) {
        mutableIsTaskDeleted.value = isTaskDeleted
    }
}