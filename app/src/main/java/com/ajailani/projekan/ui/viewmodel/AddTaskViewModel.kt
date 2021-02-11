package com.ajailani.projekan.ui.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ajailani.projekan.data.model.Project
import com.ajailani.projekan.data.model.Task
import com.ajailani.projekan.data.repository.FirebaseRepository

/** This ViewModel is used to pass data from ProjectDetailsActivity or MoreFragment */
class AddTaskViewModel @ViewModelInject constructor(
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {
    private val mutableProject = MutableLiveData<Project>()
    val project: LiveData<Project> get() = mutableProject

    private val mutableIsTaskAdded = MutableLiveData<Boolean>()
    val isTaskAdded: LiveData<Boolean> get() = mutableIsTaskAdded

    private val mutableTask = MutableLiveData<Task>()
    val task: LiveData<Task> get() = mutableTask

    private val mutableTag = MutableLiveData<String>()
    val tag: LiveData<String> get() = mutableTag

    fun setProject(project: Project) {
        mutableProject.value = project
    }

    fun setAddTask(isTaskAdded: Boolean) {
        mutableIsTaskAdded.value = isTaskAdded
    }

    fun setTask(task: Task) {
        mutableTask.value = task
    }

    fun setTag(tag: String) {
        mutableTag.value = tag
    }

    fun addTask(page: Int, itemNum: Int, task: Task) =
        firebaseRepository.addTask(page, itemNum, task)

    fun updateTask(page: Int, itemNum: Int, task: Task) =
        firebaseRepository.updateTask(page, itemNum, task)
}