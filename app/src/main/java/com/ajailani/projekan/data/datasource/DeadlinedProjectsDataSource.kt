package com.ajailani.projekan.data.datasource

import androidx.paging.PagingSource
import com.ajailani.projekan.data.api.ApiService
import com.ajailani.projekan.data.model.Project
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class DeadlinedProjectsDataSource @Inject constructor(
    private val apiService: ApiService,
    private val firebaseAuth: FirebaseAuth
) : PagingSource<Int, Project>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Project> {
        try {
            val currentLoadingPageKey = params.key ?: 1
            val response = apiService.getMyProjects(
                firebaseAuth.currentUser!!.uid,
                "page$currentLoadingPageKey"
            )
            val projectsList = response.body() ?: emptyList()
            val deadlinedProjectsList = mutableListOf<Project>()

            //Filter deadlined projects
            for (i in projectsList.indices) {
                val sdf = SimpleDateFormat("dd MMM yyyy", Locale.US)
                val deadline = sdf.parse(projectsList[i].deadline)
                val curDate = Date()

                val dayDiff =
                    TimeUnit.DAYS.convert((deadline!!.time - curDate.time), TimeUnit.MILLISECONDS)

                if (dayDiff in 0..7) {
                    deadlinedProjectsList.add(projectsList[i])
                }
            }

            val prevKey = if (currentLoadingPageKey == 1) null else currentLoadingPageKey - 1

            return LoadResult.Page(
                data = deadlinedProjectsList,
                prevKey = prevKey,
                nextKey = currentLoadingPageKey.plus(1)
            )
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }
}