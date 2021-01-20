package com.ajailani.projekan.ui.view.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.ajailani.projekan.R
import com.ajailani.projekan.databinding.ActivityProjectDetailsBinding
import com.ajailani.projekan.ui.viewmodel.ProjectDetailsViewModel
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProjectDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProjectDetailsBinding
    private val projectDetailsViewModel: ProjectDetailsViewModel by viewModels()

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

        setupView()
    }

    private fun setupView() {
        projectDetailsViewModel.getProjectDetails(page, itemNum)?.observe(this, { project ->
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
            }
        })

        //Observe project progress real-time
        projectDetailsViewModel.getProjectProgress(page, itemNum)?.observe(this, { projectProgress ->
            binding.progress.progress = projectProgress
            binding.progressText.text = getString(R.string.progress_text, projectProgress)
        })
    }
}