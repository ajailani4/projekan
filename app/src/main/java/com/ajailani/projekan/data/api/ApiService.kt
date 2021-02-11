package com.ajailani.projekan.data.api

import com.ajailani.projekan.data.model.Page
import com.ajailani.projekan.data.model.Project
import com.ajailani.projekan.data.model.ProjectList
import com.ajailani.projekan.data.model.Task
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    //Get total page of projects list
    @GET("{userId}/projects/totalPage.json")
    suspend fun getTotalPage(@Path("userId") userId: String): Response<Int?>

    //Get total item of the page
    @GET("{userId}/projects/{page}/totalItem.json")
    suspend fun getProjectTotalItem(
        @Path("userId") userId: String,
        @Path("page") page: String
    ): Response<Int?>

    //Get project id
    @GET("{userId}/projects/{page}/data/{itemNum}/id.json")
    suspend fun getProjectId(
        @Path("userId") userId: String,
        @Path("page") page: String,
        @Path("itemNum") itemNum: Int,
    ): Response<Int?>

    //Get projects list
    @GET("{userId}/projects/{page}/data.json")
    suspend fun getMyProjects(
        @Path("userId") userId: String,
        @Path("page") page: String
    ): Response<List<Project>>

    //Get project details
    @GET("{userId}/projects/{page}/data/{itemNum}.json")
    suspend fun getProjectDetails(
        @Path("userId") userId: String,
        @Path("page") page: String,
        @Path("itemNum") itemNum: Int
    ): Response<Project>

    /** Add project */

    //if totalItem is 0 or 10 then add project on the new page
    //Update totalPage
    @PATCH("{userId}/projects.json")
    suspend fun updateTotalPage(
        @Path("userId") userId: String,
        @Body totalPage: Map<String, Int>
    ): Response<Void>

    //Put page and totalItem
    @PUT("{userId}/projects/{page}.json")
    suspend fun putPageAndTotalItem(
        @Path("userId") userId: String,
        @Path("page") page: String,
        @Body pageBody: Page
    ): Response<Void>

    //Put new project
    @PUT("{userId}/projects/{page}/data/{itemNum}.json")
    suspend fun putProject(
        @Path("userId") userId: String,
        @Path("page") page: String,
        @Path("itemNum") itemNum: Int,
        @Body project: Project
    ): Response<Void>

    //else if totalItem less than 10 then add project on the latest page
    //Patch totalItem
    @PATCH("{userId}/projects/{page}.json")
    suspend fun patchProjectTotalItem(
        @Path("userId") userId: String,
        @Path("page") page: String,
        @Body pageBody: Page
    ): Response<Void>

    //Patch new project
    @PATCH("{userId}/projects/{page}/data/{itemNum}.json")
    suspend fun patchProject(
        @Path("userId") userId: String,
        @Path("page") page: String,
        @Path("itemNum") itemNum: Int,
        @Body project: Project
    ): Response<Void>

    /** Delete Project */

    //Delete page if totalItem is 1
    @DELETE("{userId}/projects/{page}.json")
    suspend fun deletePage(
        @Path("userId") userId: String,
        @Path("page") page: String
    ): Response<Void>

    //Update new restructured project list
    @PATCH("{userId}/projects/{page}.json")
    suspend fun updateReProjectList(
        @Path("userId") userId: String,
        @Path("page") page: String,
        @Body data: ProjectList
    ): Response<Void>

    /** Add Task */

    //Set project has tasks
    @PATCH("{userId}/projects/{page}/data/{itemNum}.json")
    suspend fun setProjectHasTasks(
        @Path("userId") userId: String,
        @Path("page") page: String,
        @Path("itemNum") itemNum: Int,
        @Body hasTasks: Map<String, Boolean>
    ): Response<Void>

    //Add task
    @POST("{userId}/projects/{page}/data/{itemNum}/tasks.json")
    suspend fun addTask(
        @Path("userId") userId: String,
        @Path("page") page: String,
        @Path("itemNum") itemNum: Int,
        @Body task: Task
    ): Response<Void>

    //Update task status
    @PATCH("{userId}/projects/{page}/data/{itemNum}/tasks/{taskId}.json")
    suspend fun updateTaskStatus(
        @Path("userId") userId: String,
        @Path("page") page: String,
        @Path("itemNum") itemNum: Int,
        @Path("taskId") taskId: String,
        @Body status: Map<String, String>
    ): Response<Void>

    //Update project status
    @PATCH("{userId}/projects/{page}/data/{itemNum}.json")
    suspend fun updateProjectStatus(
        @Path("userId") userId: String,
        @Path("page") page: String,
        @Path("itemNum") itemNum: Int,
        @Body status: Map<String, String>
    ): Response<Void>

    //Update task
    @PATCH("{userId}/projects/{page}/data/{itemNum}/tasks/{taskId}.json")
    suspend fun updateTask(
        @Path("userId") userId: String,
        @Path("page") page: String,
        @Path("itemNum") itemNum: Int,
        @Path("taskId") taskId: String?,
        @Body task: Task
    ): Response<Void>

    //Delete task
    @DELETE("{userId}/projects/{page}/data/{itemNum}/tasks/{taskId}.json")
    suspend fun deleteTask(
        @Path("userId") userId: String,
        @Path("page") page: String,
        @Path("itemNum") itemNum: Int,
        @Path("taskId") taskId: String?
    ): Response<Void>
}