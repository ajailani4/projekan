package com.ajailani.projekan.ui.view.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ajailani.projekan.R
import com.ajailani.projekan.databinding.ActivityProjectDetailsBinding
import com.ajailani.projekan.ui.adapter.TasksAdapter
import com.ajailani.projekan.ui.view.fragment.AddTaskFragment
import com.ajailani.projekan.ui.view.fragment.MoreFragment
import com.ajailani.projekan.ui.viewmodel.AddTaskViewModel
import com.ajailani.projekan.ui.viewmodel.MoreViewModel
import com.ajailani.projekan.ui.viewmodel.ProjectDetailsViewModel
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProjectDetailsActivity : AppCompatActivity() {
    lateinit var binding: ActivityProjectDetailsBinding
    private val projectDetailsViewModel: ProjectDetailsViewModel by viewModels()
    private val moreViewModel: MoreViewModel by viewModels()
    private val addTaskViewModel: AddTaskViewModel by viewModels()
    private lateinit var tasksAdapter: TasksAdapter

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
            projectProgress.progress = 0
            progressText.visibility = View.INVISIBLE
            deadlineTitleTv.visibility = View.INVISIBLE
            deadline.visibility = View.INVISIBLE
            tasksRv.visibility = View.INVISIBLE
        }
    }

    private fun setupLoadedUI() {
        binding.apply {
            shimmerLayout.stopShimmerAnimation()
            shimmerLayout.visibility = View.GONE

            icon.visibility = View.VISIBLE
            title.visibility = View.VISIBLE
            desc.visibility = View.VISIBLE
            platform.visibility = View.VISIBLE
            category.visibility = View.VISIBLE
            projectProgress.visibility = View.VISIBLE
            progressText.visibility = View.VISIBLE
            deadlineTitleTv.visibility = View.VISIBLE
            deadline.visibility = View.VISIBLE
            tasksRv.visibility = View.VISIBLE
        }
    }

    private fun setupView() {
        //Show project details
        projectDetailsViewModel.getProjectDetails(page, itemNum).observe(this, { project ->
            binding.apply {
                if(project?.icon != "") {
                    Glide.with(icon.context)
                        .load(project?.icon)
                        .into(icon)
                }

                title.text = project?.title
                desc.text = project?.desc
                platform.text = project?.platform
                category.text = project?.category
                deadline.text = project?.deadline

                moreViewModel.setProject(project!!)
                addTaskViewModel.setProject(project)

                setupLoadedUI()
            }
        })

        //Observe project progress real-time
        projectDetailsViewModel.getProjectProgress(page, itemNum).observe(this, { projectProgress ->
            if (projectProgress != null) {
                binding.projectProgress.progress = projectProgress
                binding.progressText.text = getString(R.string.progress_text, projectProgress)
            }
        })

        //Show MoreFragment
        binding.moreIv.setOnClickListener {
            moreViewModel.setTag("Project")

            MoreFragment().show(supportFragmentManager, MoreFragment.TAG)
        }

        //Show AddTaskFragment
        binding.addTask.setOnClickListener {
            AddTaskFragment().show(supportFragmentManager, AddTaskFragment.TAG)
        }

        //Show tasks list
        projectDetailsViewModel.getTasks(page, itemNum).observe(this, { tasks ->
            tasksAdapter = TasksAdapter(tasks) { task ->

            }

            binding.tasksRv.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = tasksAdapter
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.projectProgress.progress = 0
    }
}