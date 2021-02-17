package com.ajailani.projekan.ui.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.ajailani.projekan.databinding.ActivityDeadlinedProjectsBinding
import com.ajailani.projekan.ui.adapter.DeadlinedProjectsAdapter
import com.ajailani.projekan.ui.viewmodel.DeadlinedProjectsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeadlinedProjectsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDeadlinedProjectsBinding
    private val deadlinedProjectsViewModel: DeadlinedProjectsViewModel by viewModels()
    private lateinit var deadlinedProjectsAdapter: DeadlinedProjectsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeadlinedProjectsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "This Week Deadlines"
        supportActionBar?.elevation = 0F

        setupLoadingUI()
        setupView()
    }

    private fun setupLoadingUI() {
        binding.apply {
            shimmerLayout.visibility = View.VISIBLE
            shimmerLayout.startShimmerAnimation()

            deadlinedProjectsRv.visibility = View.INVISIBLE
        }
    }

    private fun setupLoadedUI() {
        binding.apply {
            shimmerLayout.visibility = View.GONE
            shimmerLayout.stopShimmerAnimation()

            deadlinedProjectsRv.visibility = View.VISIBLE
        }
    }

    private fun setupView() {
        if (deadlinedProjectsViewModel.isNetworkConnected()) {
            deadlinedProjectsViewModel.getDeadlinedProjects()
                .observe(this@DeadlinedProjectsActivity, { deadlinedProjects ->
                    deadlinedProjectsAdapter = DeadlinedProjectsAdapter { page, itemNum ->
                        //Go to ProjectDetailsActivity
                        val projectDetailsIntent =
                            Intent(applicationContext, ProjectDetailsActivity::class.java)
                        projectDetailsIntent.putExtra("page", page)
                        projectDetailsIntent.putExtra("itemNum", itemNum)
                        startActivity(projectDetailsIntent)
                    }

                    binding.deadlinedProjectsRv.apply {
                        layoutManager = LinearLayoutManager(context)
                        adapter = deadlinedProjectsAdapter
                    }

                    deadlinedProjectsAdapter.submitData(
                        this@DeadlinedProjectsActivity.lifecycle,
                        deadlinedProjects
                    )

                    //Handling load state
                    deadlinedProjectsAdapter.addLoadStateListener { loadState ->
                        if (loadState.source.refresh is LoadState.NotLoading && deadlinedProjectsAdapter.itemCount > 0) {
                            setupLoadedUI()
                        }
                    }
                })
        } else {
            binding.apply {
                shimmerLayout.visibility = View.GONE
                shimmerLayout.stopShimmerAnimation()

                youAreOfflineIv.visibility = View.VISIBLE
                youAreOfflineTv.visibility = View.VISIBLE
            }
        }
    }
}