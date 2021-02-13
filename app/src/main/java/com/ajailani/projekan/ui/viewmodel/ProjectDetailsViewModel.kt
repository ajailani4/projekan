package com.ajailani.projekan.ui.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.ajailani.projekan.data.repository.FirebaseRepository
import com.ajailani.projekan.utils.NetworkHelper

class ProjectDetailsViewModel @ViewModelInject constructor(
    private val firebaseRepository: FirebaseRepository,
    private val networkHelper: NetworkHelper
) : ViewModel() {
    fun isNetworkConnected() =
        networkHelper.isNetworkConnected()

    fun getProjectDetails(page: Int, itemNum: Int) =
        firebaseRepository.getProjectDetails(page, itemNum)

    fun getProjectProgress(page: Int, itemNum: Int) =
        firebaseRepository.getProjectProgress(page, itemNum)

    fun getTasks(page: Int, itemNum: Int) = firebaseRepository.getTasks(page, itemNum)

    fun updateTaskProgress(page: Int, itemNum: Int, itemId: String, status: String) =
        firebaseRepository.updateTaskProgress(page, itemNum, itemId, status)

    fun updateProjectProgress(page: Int, itemNum: Int) =
        firebaseRepository.updateProjectProgress(page, itemNum)

    fun updateProjectStatus(page: Int, itemNum: Int, status: String) =
        firebaseRepository.updateProjectStatus(page, itemNum, status)
}