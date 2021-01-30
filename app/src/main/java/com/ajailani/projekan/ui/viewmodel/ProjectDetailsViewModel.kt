package com.ajailani.projekan.ui.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.ajailani.projekan.data.model.Project
import com.ajailani.projekan.data.repository.FirebaseRepository

class ProjectDetailsViewModel @ViewModelInject constructor(
    private val firebaseRepository: FirebaseRepository
): ViewModel() {
    fun getProjectDetails(page: Int, itemNum: Int) = firebaseRepository.getProjectDetails(page, itemNum)

    fun getProjectProgress(page: Int, itemNum: Int) = firebaseRepository.getProjectProgress(page, itemNum)

    fun getTasks(page: Int, itemNum: Int) = firebaseRepository.getTasks(page, itemNum)
}