package com.ajailani.projekan.data.repository

import android.content.Intent
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.ajailani.projekan.data.api.ApiService
import com.ajailani.projekan.data.datasource.DeadlinedProjectsDataSource
import com.ajailani.projekan.data.datasource.MyProjectsDataSource
import com.ajailani.projekan.data.model.Page
import com.ajailani.projekan.data.model.Project
import com.ajailani.projekan.data.model.ProjectList
import com.ajailani.projekan.data.model.Task
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.collections.HashMap

class FirebaseRepository @Inject constructor(
    private val apiService: ApiService,
    private val firebaseAuth: FirebaseAuth,
    private val firebaseStorage: FirebaseStorage,
    private val dbReference: DatabaseReference
) {
    //Check user authentication
    fun checkUserAuth(): LiveData<Boolean> {
        val userAuth = MutableLiveData<Boolean>()

        userAuth.value = firebaseAuth.currentUser != null

        return userAuth
    }

    //Login with Google Account
    fun loginWithGoogle(): LiveData<Intent> {
        val loginIntent = MutableLiveData<Intent>()
        loginIntent.value = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(
                Collections.singletonList(
                    AuthUI.IdpConfig.GoogleBuilder().build()
                )
            )
            .setIsSmartLockEnabled(false)
            .build()

        return loginIntent
    }

    //Get user's name
    fun getUserName(): LiveData<String> {
        val userName = MutableLiveData<String>()
        val name = firebaseAuth.currentUser!!.displayName!!
        userName.value = name.substring(0, name.indexOf(" "))

        return userName
    }

    //Get user's avatar
    fun getUserAva(): LiveData<String> {
        val userAva = MutableLiveData<String>()
        userAva.value = firebaseAuth.currentUser!!.photoUrl.toString()

        return userAva
    }

    //Get deadlined projects list for header
    fun getDeadlinedProjectsHeader() =
        liveData(Dispatchers.IO) {
            val projectsList = mutableListOf<Project>()
            val deadlinedProjectsList = mutableListOf<Project>()
            val userId = firebaseAuth.currentUser!!.uid
            val totalPage = apiService.getTotalPage(userId).body() ?: 0

            //Search all projects
            for (i in 1 until totalPage + 1) {
                val data = apiService.getMyProjects(userId, "page$i").body() ?: emptyList()

                projectsList.addAll(data)
            }

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

                if (deadlinedProjectsList.size == 5) {
                    break
                }
            }

            emit(deadlinedProjectsList)
        }

    //Get deadlined projects list for See More
    fun getDeadlinedProjects(): LiveData<PagingData<Project>> {
        return Pager(
            config = PagingConfig(enablePlaceholders = false, pageSize = 10),
            pagingSourceFactory = {
                DeadlinedProjectsDataSource(apiService, firebaseAuth)
            }
        ).liveData
    }

    //Get my projects list with pagination
    fun getMyProjects(): LiveData<PagingData<Project>> {
        return Pager(
            config = PagingConfig(enablePlaceholders = false, pageSize = 10),
            pagingSourceFactory = {
                MyProjectsDataSource(apiService, firebaseAuth)
            }
        ).liveData
    }

    //Add project
    fun addProject(project: Project, iconUrl: String) =
        liveData(Dispatchers.IO) {
            val isSuccessful: Boolean

            val totalPage = apiService.getTotalPage(firebaseAuth.currentUser!!.uid).body() ?: 0

            val totalItem = apiService.getProjectTotalItem(
                firebaseAuth.currentUser!!.uid, "page$totalPage"
            ).body() ?: 0

            val projectId = apiService.getProjectId(
                firebaseAuth.currentUser!!.uid, "page$totalPage", totalItem - 1
            ).body() ?: 0

            //Update project id and icon
            project.id = projectId + 1
            project.icon = iconUrl

            if (totalItem == 0 || totalItem == 10) {
                /**Add project on the new page*/

                //Update totalPage
                val totalPageBody = HashMap<String, Int>()
                totalPageBody["totalPage"] = totalPage + 1
                apiService.updateTotalPage(
                    firebaseAuth.currentUser!!.uid, totalPageBody
                )

                //Put page and totalItem
                val pageBody = Page(totalPage + 1, 1)
                apiService.putPageAndTotalItem(
                    firebaseAuth.currentUser!!.uid, "page${totalPage + 1}", pageBody
                )

                //Put new project
                project.itemNum = 0
                project.onPage = totalPage + 1
                isSuccessful = apiService.putProject(
                    firebaseAuth.currentUser!!.uid, "page${totalPage + 1}", 0, project
                ).isSuccessful
            } else {
                /**Add project on the latest page*/

                //Patch totalItem
                val pageBody = Page(null, totalItem + 1)
                apiService.patchProjectTotalItem(
                    firebaseAuth.currentUser!!.uid, "page$totalPage", pageBody
                )

                //Patch new project
                project.itemNum = totalItem
                project.onPage = totalPage
                isSuccessful = apiService.patchProject(
                    firebaseAuth.currentUser!!.uid, "page$totalPage", totalItem, project
                ).isSuccessful
            }

            emit(isSuccessful)
        }

    //Upload project icon
    fun uploadProjectIcon(bytes: ByteArray): LiveData<String> {
        val uploadProjectIcon = MutableLiveData<String>()

        //Write path name where the image will be stored
        val fileName = "${UUID.randomUUID()}.jpg"
        val pathName = "Project Icons/$fileName"

        firebaseStorage.reference.child(pathName).putBytes(bytes)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    firebaseStorage.reference.child(pathName).downloadUrl.addOnSuccessListener { uri ->
                        val iconUrl = uri.toString()

                        uploadProjectIcon.value = iconUrl
                    }
                }
            }

        return uploadProjectIcon
    }

    //Get project details
    fun getProjectDetails(page: Int, itemNum: Int) =
        liveData(Dispatchers.IO) {
            val project = apiService.getProjectDetails(
                firebaseAuth.currentUser!!.uid, "page$page", itemNum
            ).body()

            emit(project)
        }

    //Get project progress in real-time
    fun getProjectProgress(page: Int, itemNum: Int): LiveData<Int?> {
        val project = MutableLiveData<Int?>()

        dbReference.child(firebaseAuth.currentUser!!.uid)
            .child("projects")
            .child("page$page")
            .child("data")
            .child("$itemNum")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val projectData = snapshot.getValue(Project::class.java)

                    project.value = projectData?.progress
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

        return project
    }

    //Update project
    fun updateProject(project: Project, iconUrl: String) =
        liveData(Dispatchers.IO) {
            //Update project icon if iconUrl changed
            if (iconUrl != "") {
                project.icon = iconUrl
            }

            //Update project
            val isSuccessful = apiService.patchProject(
                firebaseAuth.currentUser!!.uid, "page${project.onPage}", project.itemNum, project
            ).isSuccessful

            emit(isSuccessful)
        }

    //Delete project
    fun deleteProject(project: Project) =
        liveData(Dispatchers.IO) {
            var isSuccessful: Boolean

            //Update totalItem
            val totalItem = apiService.getProjectTotalItem(
                firebaseAuth.currentUser!!.uid, "page${project.onPage}"
            ).body()

            val pageBody = Page(null, totalItem?.minus(1))
            apiService.patchProjectTotalItem(
                firebaseAuth.currentUser!!.uid, "page${project.onPage}", pageBody
            )

            //Update page
            val totalPage = apiService.getTotalPage(firebaseAuth.currentUser!!.uid).body()

            if (totalItem == 1) {
                apiService.deletePage(
                    firebaseAuth.currentUser!!.uid, "page${project.onPage}"
                )

                val totalPageBody = HashMap<String, Int>()
                totalPageBody["totalPage"] = totalPage!!.minus(1)
                isSuccessful = apiService.updateTotalPage(
                    firebaseAuth.currentUser!!.uid, totalPageBody
                ).isSuccessful

                emit(isSuccessful)
            }

            //Restructure project list and patch it
            if (totalItem!! > 1) {
                //Get tasks list in each item
                val tasksListEachItem = mutableListOf<List<Task>>()

                for (i in 0 until totalItem) {
                    if (i != project.itemNum) {
                        dbReference.child(firebaseAuth.currentUser!!.uid)
                            .child("projects")
                            .child("page${project.onPage}")
                            .child("data")
                            .child("$i")
                            .child("tasks")
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val tempTasksList = mutableListOf<Task>()

                                    snapshot.children.forEach {
                                        val task = it.getValue(Task::class.java)

                                        Log.d("Projekan", "Copying task: $task")
                                        tempTasksList.add(task!!)
                                    }

                                    tasksListEachItem.add(tempTasksList)
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    TODO("Not yet implemented")
                                }

                            })
                    }
                }

                val projectList = apiService.getMyProjects(
                    firebaseAuth.currentUser!!.uid, "page${project.onPage}"
                ).body() ?: emptyList()

                val tempReProjectList = mutableListOf<Project>()

                for (i in projectList) {
                    if (i.id != project.id) {
                        if (i.itemNum > project.itemNum) {
                            i.itemNum -= 1
                        }

                        tempReProjectList.add(i)
                    } else {
                        continue
                    }
                }

                val reProjectList = ProjectList(tempReProjectList)

                isSuccessful = apiService.updateReProjectList(
                    firebaseAuth.currentUser!!.uid, "page${project.onPage}", reProjectList
                ).isSuccessful

                //Repost tasks list in each item
                Log.d("Projekan", "Task list: ${tasksListEachItem.size}")

                tasksListEachItem.forEach {
                    Log.d("Projekan", "Task list: $it")
                }

                if (tasksListEachItem.isNotEmpty()) {
                    val updatedProjectList = apiService.getMyProjects(
                        firebaseAuth.currentUser!!.uid, "page${project.onPage}"
                    ).body() ?: emptyList()

                    for ((itemNum, i) in updatedProjectList.withIndex()) {
                        if (i.hasTasks) {
                            for (j in tasksListEachItem[itemNum].indices) {
                                Log.d("Projekan", "Adding task: ${tasksListEachItem[itemNum][j]}")
                                apiService.addTask(
                                    firebaseAuth.currentUser!!.uid,
                                    "page${project.onPage}",
                                    itemNum,
                                    tasksListEachItem[itemNum][j]
                                )
                            }
                        }
                    }
                }

                emit(isSuccessful)
            }
        }

    //Add task
    fun addTask(page: Int, itemNum: Int, task: Task) =
        liveData(Dispatchers.IO) {
            //Set project has tasks
            val hasTasksBody = HashMap<String, Boolean>()
            hasTasksBody["hasTasks"] = true
            apiService.setProjectHasTasks(
                firebaseAuth.currentUser!!.uid, "page$page", itemNum, hasTasksBody
            )

            //Add task
            val isSuccessful = apiService.addTask(
                firebaseAuth.currentUser!!.uid, "page$page", itemNum, task
            ).isSuccessful

            emit(isSuccessful)
        }

    //Get tasks list in real-time
    fun getTasks(page: Int, itemNum: Int): LiveData<MutableList<Task>> {
        val tasks = MutableLiveData<MutableList<Task>>()

        dbReference.child(firebaseAuth.currentUser!!.uid)
            .child("projects")
            .child("page$page")
            .child("data")
            .child("$itemNum")
            .child("tasks")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val tempTasksList = mutableListOf<Task>()

                    snapshot.children.forEach {
                        val task = it.getValue(Task::class.java)
                        task!!.id = it.key

                        tempTasksList.add(task)
                    }

                    tasks.value = tempTasksList
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

        return tasks
    }

    //Update task status
    fun updateTaskProgress(page: Int, itemNum: Int, itemId: String, status: String) =
        liveData(Dispatchers.IO) {
            val statusBody = HashMap<String, String>()
            statusBody["status"] = status

            val isSuccessful = apiService.updateTaskStatus(
                firebaseAuth.currentUser!!.uid, "page$page", itemNum, itemId, statusBody
            ).isSuccessful

            emit(isSuccessful)
        }

    //Update project progress
    fun updateProjectProgress(page: Int, itemNum: Int): LiveData<Boolean> {
        val updatedProjectProg = MutableLiveData<Boolean>()
        var doneTasksList = 0

        /** Count project progress */
        dbReference.child(firebaseAuth.currentUser!!.uid)
            .child("projects")
            .child("page$page")
            .child("data")
            .child("$itemNum")
            .child("tasks")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach {
                        val task = it.getValue(Task::class.java)

                        if (task?.status == "done") doneTasksList++
                    }

                    val doneTasksTotal = doneTasksList.toFloat()
                    val tasksTotal = snapshot.childrenCount.toFloat()
                    val projectProgress = (doneTasksTotal / tasksTotal) * 100

                    Log.d("Projekan progress", "done task : $doneTasksTotal")
                    Log.d("Projekan progress", "task total : $tasksTotal")
                    Log.d("Projekan progress", "project progress : $projectProgress")

                    /** Update project progress */
                    val projectProgressBody = HashMap<String, Any>()
                    projectProgressBody["progress"] = projectProgress.toInt()

                    dbReference.child(firebaseAuth.currentUser!!.uid)
                        .child("projects")
                        .child("page$page")
                        .child("data")
                        .child("$itemNum")
                        .updateChildren(projectProgressBody)
                        .addOnSuccessListener {
                            updatedProjectProg.value = true
                        }
                        .addOnFailureListener {
                            updatedProjectProg.value = false
                        }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

        return updatedProjectProg
    }

    //Update project status
    fun updateProjectStatus(page: Int, itemNum: Int, status: String) =
        CoroutineScope(Dispatchers.IO).launch {
            val statusBody = HashMap<String, String>()
            statusBody["status"] = status

            apiService.updateProjectStatus(
                firebaseAuth.currentUser!!.uid, "page$page", itemNum, statusBody
            )
        }

    //Update task
    fun updateTask(page: Int, itemNum: Int, task: Task) =
        liveData(Dispatchers.IO) {
            val isSuccessful = apiService.updateTask(
                firebaseAuth.currentUser!!.uid, "page$page", itemNum, task.id, task
            ).isSuccessful

            emit(isSuccessful)
        }

    //Delete task
    fun deleteTask(page: Int, itemNum: Int, task: Task) =
        liveData(Dispatchers.IO) {
            val isSuccessful = apiService.deleteTask(
                firebaseAuth.currentUser!!.uid, "page$page", itemNum, task.id
            ).isSuccessful

            emit(isSuccessful)
        }
}