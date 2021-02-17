package com.ajailani.projekan.ui.view.activity

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.ajailani.projekan.R
import com.ajailani.projekan.data.model.Project
import com.ajailani.projekan.databinding.ActivityProjectDetailsBinding
import com.ajailani.projekan.ui.adapter.TasksAdapter
import com.ajailani.projekan.ui.view.fragment.AddTaskFragment
import com.ajailani.projekan.ui.view.fragment.MoreFragment
import com.ajailani.projekan.ui.viewmodel.AddTaskViewModel
import com.ajailani.projekan.ui.viewmodel.MoreViewModel
import com.ajailani.projekan.ui.viewmodel.ProjectDetailsViewModel
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class ProjectDetailsActivity : AppCompatActivity() {
    lateinit var binding: ActivityProjectDetailsBinding
    private val projectDetailsViewModel: ProjectDetailsViewModel by viewModels()
    private val moreViewModel: MoreViewModel by viewModels()
    private val addTaskViewModel: AddTaskViewModel by viewModels()
    private lateinit var tasksAdapter: TasksAdapter

    private var project = Project()
    private var page = 0
    private var itemNum = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProjectDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Project Details"
        supportActionBar?.elevation = 0F

        page = intent.extras?.getInt("page")!!
        itemNum = intent.extras?.getInt("itemNum")!!

        setupLoadingUI()
        setupView()
    }

    private fun setupLoadingUI() {
        binding.apply {
            shimmerLayout.visibility = View.VISIBLE
            shimmerLayout.startShimmerAnimation()

            icon.visibility = View.INVISIBLE
            title.visibility = View.INVISIBLE
            desc.visibility = View.INVISIBLE
            platform.visibility = View.INVISIBLE
            category.visibility = View.INVISIBLE
            projectProgress.visibility = View.INVISIBLE
            progressText.visibility = View.INVISIBLE
            deadlineTitleTv.visibility = View.INVISIBLE
            deadline.visibility = View.INVISIBLE
        }
    }

    private fun setupLoadedUI() {
        binding.apply {
            shimmerLayout.stopShimmerAnimation()
            shimmerLayout.visibility = View.GONE

            icon.visibility = View.VISIBLE
            title.visibility = View.VISIBLE
            moreIv.visibility = View.VISIBLE
            desc.visibility = View.VISIBLE
            platform.visibility = View.VISIBLE
            category.visibility = View.VISIBLE
            projectProgress.visibility = View.VISIBLE
            progressText.visibility = View.VISIBLE
            deadlineTitleTv.visibility = View.VISIBLE
            deadline.visibility = View.VISIBLE
            appCompatTextView2.visibility = View.VISIBLE
            addTask.visibility = View.VISIBLE
        }
    }

    private fun setupView() {
        if (projectDetailsViewModel.isNetworkConnected()) {
            //Show project details
            projectDetailsViewModel.getProjectDetails(page, itemNum).observe(this, {
                project = it!!

                binding.apply {
                    if (project.icon != "") {
                        Glide.with(icon.context)
                            .load(project.icon)
                            .into(icon)
                    }

                    title.text = project.title
                    desc.text = project.desc
                    platform.text = project.platform
                    category.text = project.category
                    deadline.text = project.deadline
                }

                //Pass project in order to MoreFragment or AddTaskFragment knows what project will be executed
                moreViewModel.setProject(project)
                addTaskViewModel.setProject(project)

                //Show tasks list
                projectDetailsViewModel.getTasks(page, itemNum)
                    .observe(this@ProjectDetailsActivity, { tasks ->
                        if (tasks.isNotEmpty()) {
                            binding.apply {
                                tasksRv.visibility = View.VISIBLE
                                addSomeTasksIv.visibility = View.GONE
                                addSomeTasksTv.visibility = View.GONE
                            }

                            tasksAdapter = TasksAdapter(tasks, { id, status ->
                                binding.apply {
                                    moreIv.isEnabled = false
                                    addTask.isEnabled = false
                                    progressBar.root.visibility = View.VISIBLE
                                }

                                projectDetailsViewModel.updateTaskProgress(
                                    page,
                                    itemNum,
                                    id,
                                    status
                                )
                                    .observe(this@ProjectDetailsActivity, { isTaskStatusUpdated ->
                                        if (isTaskStatusUpdated) {
                                            projectDetailsViewModel.updateProjectProgress(
                                                page,
                                                itemNum
                                            )
                                                .observe(
                                                    this@ProjectDetailsActivity,
                                                    { isProjectProgUpdated ->
                                                        Log.d("Projekan progress", "updating")
                                                        if (isProjectProgUpdated) {
                                                            Log.d("Projekan progress", "updated")
                                                            Toast
                                                                .makeText(
                                                                    applicationContext,
                                                                    "Progress has been updated",
                                                                    Toast.LENGTH_SHORT
                                                                )
                                                                .show()
                                                        } else {
                                                            Toast
                                                                .makeText(
                                                                    applicationContext,
                                                                    "Progress can't be updated",
                                                                    Toast.LENGTH_SHORT
                                                                )
                                                                .show()
                                                        }

                                                        binding.apply {
                                                            moreIv.isEnabled = true
                                                            addTask.isEnabled = true
                                                            progressBar.root.visibility = View.GONE
                                                        }
                                                    })
                                        }
                                    })
                            }, { task ->
                                //Pass the task in order to MoreFragment knows what task will be executed
                                moreViewModel.setTag("Task")
                                moreViewModel.setTask(task)

                                MoreFragment().show(supportFragmentManager, MoreFragment.TAG)
                            })

                            binding.tasksRv.apply {
                                layoutManager = LinearLayoutManager(context)
                                adapter = tasksAdapter
                            }
                        } else {
                            binding.apply {
                                tasksRv.visibility = View.INVISIBLE
                                addSomeTasksIv.visibility = View.VISIBLE
                                addSomeTasksTv.visibility = View.VISIBLE
                            }
                        }

                        setupLoadedUI()
                    })
            })

            //Observe project progress real-time
            projectDetailsViewModel.getProjectProgress(page, itemNum)
                .observe(this, { projectProgress ->
                    binding.projectProgress.progress = 0

                    if (projectProgress != null) {
                        binding.projectProgress.progress = projectProgress
                        binding.progressText.text =
                            getString(R.string.progress_text, projectProgress)

                        //Update project progress in order to MoreFragment also has updated project progress
                        project.progress = projectProgress
                        moreViewModel.setProject(project)

                        if (projectProgress == 100) {
                            projectDetailsViewModel.updateProjectStatus(page, itemNum, "done")
                        } else {
                            projectDetailsViewModel.updateProjectStatus(page, itemNum, "undone")
                        }
                    }
                })

            //Show MoreFragment
            binding.moreIv.setOnClickListener {
                //Pass the project in order to MoreFragment knows that project will be executed
                moreViewModel.setTag("Project")

                MoreFragment().show(supportFragmentManager, MoreFragment.TAG)
            }

            //Show AddTaskFragment
            binding.addTask.setOnClickListener {
                addTaskViewModel.setTag("Add")

                AddTaskFragment().show(supportFragmentManager, AddTaskFragment.TAG)
            }

            //Observe isTaskAdded in order to project progress is updated in real-time
            addTaskViewModel.isTaskAdded.observe(this, { isTaskAdded ->
                if (isTaskAdded) {
                    projectDetailsViewModel.updateProjectProgress(page, itemNum)
                }
            })

            //Observe isTaskDeleted in order to project progress is updated in real-time
            moreViewModel.isTaskDeleted.observe(this, { isTaskDeleted ->
                if (isTaskDeleted) {
                    projectDetailsViewModel.updateProjectProgress(page, itemNum)
                }
            })
        } else {
            binding.apply {
                shimmerLayout.stopShimmerAnimation()
                shimmerLayout.visibility = View.GONE

                //Set background color to white
                binding.root.setBackgroundColor(Color.rgb(255, 255, 255))

                youAreOfflineIv.visibility = View.VISIBLE
                youAreOfflineTv.visibility = View.VISIBLE

                icon.visibility = View.INVISIBLE
                title.visibility = View.INVISIBLE
                moreIv.visibility = View.INVISIBLE
                desc.visibility = View.INVISIBLE
                platform.visibility = View.INVISIBLE
                category.visibility = View.INVISIBLE
                projectProgress.visibility = View.INVISIBLE
                progressText.visibility = View.INVISIBLE
                deadlineTitleTv.visibility = View.INVISIBLE
                deadline.visibility = View.INVISIBLE
                appCompatTextView2.visibility = View.INVISIBLE
                addTask.visibility = View.INVISIBLE
                deleteProjectMsg.visibility = View.GONE
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.projectProgress.progress = 0
    }
}