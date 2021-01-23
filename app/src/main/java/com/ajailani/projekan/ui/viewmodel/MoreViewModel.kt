package com.ajailani.projekan.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ajailani.projekan.data.model.Project

/** This ViewModel is just used to pass data from ProjectDetailsActivity to MoreFragment */
class MoreViewModel : ViewModel() {
    private val mutableTag = MutableLiveData<String>()
    val tag: LiveData<String> get() = mutableTag

    private val mutableProject = MutableLiveData<Project>()
    val project: LiveData<Project> get() = mutableProject

    fun setTag(tag: String) {
        mutableTag.value = tag
    }

    fun setProject(project: Project) {
        mutableProject.value = project
    }
}