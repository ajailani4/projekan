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
import com.ajailani.projekan.data.datasource.MyProjectsDatasource
import com.ajailani.projekan.data.model.Page
import com.ajailani.projekan.data.model.Project
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
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
    private val firebaseAuth: FirebaseAuth,
    private val firebaseStorage: FirebaseStorage,
    private val apiService: ApiService
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
            .setAvailableProviders(Collections.singletonList(AuthUI.IdpConfig.GoogleBuilder().build()))
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
            for (i in 0 until projectsList.size) {
                val localeID = Locale("in", "ID")
                val sdf = SimpleDateFormat("dd MMM yyyy", localeID)
                val deadline = sdf.parse(projectsList[i].deadline)
                val curDate = Date()

                val dayDiff = TimeUnit.DAYS.convert((deadline!!.time - curDate.time), TimeUnit.MILLISECONDS)

                if (dayDiff in 1..7) {
                    deadlinedProjectsList.add(projectsList[i])
                }

                if (deadlinedProjectsList.size == 5) {
                    break
                }
            }

            emit(deadlinedProjectsList)
        }

    //Get my projects list with pagination
    fun getMyProjects(): LiveData<PagingData<Project>> {
        return Pager(
            config = PagingConfig(enablePlaceholders = false, pageSize = 5),
            pagingSourceFactory = {
                MyProjectsDatasource(apiService, firebaseAuth)
            }
        ).liveData
    }

    //Add project
    fun addProject(project: Project, iconUrl: String): LiveData<Boolean> =
        liveData(Dispatchers.IO) {
            val isSuccessful: Boolean

            val totalPage = apiService.getTotalPage(firebaseAuth.currentUser!!.uid).body() ?: 0

            val totalItem = apiService.getTotalItem(
                firebaseAuth.currentUser!!.uid, "page$totalPage"
            ).body() ?: 0

            val projectId = apiService.getProjectId(
                firebaseAuth.currentUser!!.uid, "page$totalPage", totalItem-1
            ).body() ?: 0

            //Update project id and icon
            project.id = projectId+1
            project.icon = iconUrl

            if(totalItem == 0 || totalItem == 5) {
                /**Add project on the new page*/

                //Update totalPage
                val totalPageBody = HashMap<String, Int>()
                totalPageBody["totalPage"] = totalPage+1
                apiService.updateTotalPage(
                    firebaseAuth.currentUser!!.uid, totalPageBody
                )

                //Put page and totalItem
                val pageBody = Page(totalPage+1, 1)
                apiService.putPageAndTotalItem(
                    firebaseAuth.currentUser!!.uid, "page${totalPage+1}", pageBody
                )

                //Put new project
                project.onPage = totalPage+1
                isSuccessful = apiService.putProject(
                    firebaseAuth.currentUser!!.uid, "page${totalPage+1}", 0, project
                ).isSuccessful
            } else {
                /**Add project on the current page*/

                //Patch totalItem
                val pageBody = Page(null, totalItem+1)
                apiService.patchTotalItem(
                    firebaseAuth.currentUser!!.uid, "page$totalPage", pageBody
                )

                //Patch new project
                project.onPage = totalPage
                isSuccessful = apiService.patchProject(
                    firebaseAuth.currentUser!!.uid, "page${totalPage}", totalItem, project
                ).isSuccessful
            }

            emit(isSuccessful)
        }

    //Upload project icon
    fun uploadProjectIcon(bytes: ByteArray): LiveData<String> {
        val uploadProjectIcon =  MutableLiveData<String>()

        //Write path name where the image will be stored
        val fileName = "${UUID.randomUUID()}.jpg"
        val pathName = "Project Icons/$fileName"

        firebaseStorage.reference.child(pathName).putBytes(bytes)
            .addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    firebaseStorage.reference.child(pathName).downloadUrl.addOnSuccessListener { uri ->
                        val iconUrl = uri.toString()

                        uploadProjectIcon.value = iconUrl
                    }
                }
            }

        return uploadProjectIcon
    }
}