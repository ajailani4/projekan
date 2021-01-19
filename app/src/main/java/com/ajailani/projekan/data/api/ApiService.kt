package com.ajailani.projekan.data.api

import com.ajailani.projekan.data.model.Page
import com.ajailani.projekan.data.model.Project
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    //Get total page of projects list
    @GET("{userId}/projects/totalPage.json")
    suspend fun getTotalPage(@Path("userId") userId: String): Response<Int?>

    //Get total item of the page
    @GET("{userId}/projects/{page}/totalItem.json")
    suspend fun getTotalItem(
        @Path("userId") userId: String,
        @Path("page") page: String
    ): Response<Int?>

    //Get projects list
    @GET("{userId}/projects/{page}/data.json")
    suspend fun getMyProjects(
        @Path("userId") userId: String,
        @Path("page") page: String
    ): Response<List<Project>>

    //Get project id
    @GET("{userId}/projects/{page}/data/{item}/id.json")
    suspend fun getProjectId(
        @Path("userId") userId: String,
        @Path("page") page: String,
        @Path("item") item: Int,
    ): Response<Int?>


    /** Add project */

    //if totalItem is 5 then add project on the new page
    //Update totalPage
    @PATCH("{userId}/projects.json")
    suspend fun updateTotalPage(
        @Path("userId") userId: String,
        @Body totalPage: Map<String, Int>
    )

    //Put page and totalItem
    @PUT("{userId}/projects/{page}.json")
    suspend fun putPageAndTotalItem(
        @Path("userId") userId: String,
        @Path("page") page: String,
        @Body pageBody: Page
    ): Response<Void>

    //Put new project
    @PUT("{userId}/projects/{page}/data/{item}.json")
    suspend fun putProject(
        @Path("userId") userId: String,
        @Path("page") page: String,
        @Path("item") item: Int,
        @Body project: Project
    ): Response<Void>

    //else if totalItem less than 5 add then project on the latest page
    //Patch totalItem
    @PATCH("{userId}/projects/{page}.json")
    suspend fun patchTotalItem(
        @Path("userId") userId: String,
        @Path("page") page: String,
        @Body pageBody: Page
    ): Response<Void>

    //Patch new project
    @PATCH("{userId}/projects/{page}/data/{item}.json")
    suspend fun patchProject(
        @Path("userId") userId: String,
        @Path("page") page: String,
        @Path("item") item: Int,
        @Body project: Project
    ): Response<Void>
}