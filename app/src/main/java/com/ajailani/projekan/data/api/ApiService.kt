package com.ajailani.projekan.data.api

import com.ajailani.projekan.data.model.Project
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("{userId}/projects/{page}/data.json")
    suspend fun getMyProjects(
        @Path("userId") userId: String,
        @Path("page") page: String
    ): Response<List<Project>>
}