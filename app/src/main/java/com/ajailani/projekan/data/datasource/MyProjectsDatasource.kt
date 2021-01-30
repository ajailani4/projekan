package com.ajailani.projekan.data.datasource

import android.util.Log
import androidx.paging.PagingSource
import com.ajailani.projekan.data.api.ApiService
import com.ajailani.projekan.data.model.Project
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class MyProjectsDataSource @Inject constructor(
    private val apiService: ApiService,
    private val firebaseAuth: FirebaseAuth
) : PagingSource<Int, Project>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Project> {
        try {
            val currentLoadingPageKey = params.key ?: 1
            val response = apiService.getMyProjects(firebaseAuth.currentUser!!.uid, "page$currentLoadingPageKey")
            val data = response.body() ?: emptyList()

            val prevKey = if(currentLoadingPageKey == 1) null else currentLoadingPageKey-1

            return LoadResult.Page(
                data = data,
                prevKey = prevKey,
                nextKey = currentLoadingPageKey.plus(1)
            )
        } catch(e: Exception) {
            return LoadResult.Error(e)
        }
    }
}