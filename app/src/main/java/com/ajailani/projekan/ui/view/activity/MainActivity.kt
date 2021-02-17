package com.ajailani.projekan.ui.view.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.ajailani.projekan.R
import com.ajailani.projekan.databinding.ActivityMainBinding
import com.ajailani.projekan.ui.adapter.DeadlinedProjectsHeaderAdapter
import com.ajailani.projekan.ui.adapter.MyProjectsAdapter
import com.ajailani.projekan.ui.viewmodel.HomeViewModel
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var deadlinedProjectsAdapter: DeadlinedProjectsHeaderAdapter
    private lateinit var myProjectsAdapter: MyProjectsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupLoadingUI()
        setupView()

        //Refreshing layout
        binding.swipeRefresh.setColorSchemeColors(Color.rgb(251, 146, 65))
        binding.swipeRefresh.setOnRefreshListener {
            setupLoadingUI()
            setupView()
        }
    }

    private fun setupLoadingUI() {
        binding.apply {
            shimmerLayout.visibility = View.VISIBLE
            shimmerLayout.startShimmerAnimation()

            helloUserTv.visibility = View.INVISIBLE
            greetingsTv.visibility = View.INVISIBLE
            userAvaIv.visibility = View.INVISIBLE
            deadlinedProjectsRv.visibility = View.INVISIBLE
            myProjectsRv.visibility = View.INVISIBLE
            noDataIv.visibility = View.GONE
            noDataTv.visibility = View.GONE
            addYourProjectsIv.visibility = View.GONE
            addYourProjectsTv.visibility = View.GONE
            youAreOfflineIv.visibility = View.GONE
            youAreOfflineTv.visibility = View.GONE
        }
    }

    private fun setupLoadedUI() {
        binding.apply {
            shimmerLayout.stopShimmerAnimation()
            shimmerLayout.visibility = View.GONE

            swipeRefresh.isRefreshing = false
            helloUserTv.visibility = View.VISIBLE
            greetingsTv.visibility = View.VISIBLE
            userAvaIv.visibility = View.VISIBLE
            deadlinedProjectsRv.visibility = View.VISIBLE
            myProjectsRv.visibility = View.VISIBLE
            addProject.visibility = View.VISIBLE
        }
    }

    private fun setupView() {
        if (homeViewModel.isNetworkConnected()) {
            //Show user's name
            homeViewModel.getUserName().observe(this, { userName ->
                binding.helloUserTv.text = getString(R.string.hello_user, userName)
            })

            //Show user's ava
            homeViewModel.getUserAva().observe(this, { userAva ->
                Glide.with(this)
                    .load(userAva)
                    .into(binding.userAvaIv)
            })

            //Show deadlined projects list for header
            showDeadlinedProjects()

            //Show my projects list
            showMyProjects()

            //Add project
            binding.addProject.setOnClickListener {
                val addProjectIntent = Intent(applicationContext, AddProjectActivity::class.java)
                addProjectIntent.putExtra("tag", "Add")
                startActivity(addProjectIntent)
            }

            //See More
            binding.seeMoreTv.setOnClickListener {
                val deadlinedProjectsIntent =
                    Intent(applicationContext, DeadlinedProjectsActivity::class.java)
                startActivity(deadlinedProjectsIntent)
            }
        } else {
            binding.apply {
                shimmerLayout.stopShimmerAnimation()
                shimmerLayout.visibility = View.GONE

                swipeRefresh.isRefreshing = false
                noDataIv.visibility = View.VISIBLE
                noDataTv.visibility = View.VISIBLE
                youAreOfflineIv.visibility = View.VISIBLE
                youAreOfflineTv.visibility = View.VISIBLE
                seeMoreTv.visibility = View.INVISIBLE
                addProject.visibility = View.INVISIBLE
            }
        }
    }

    private fun showDeadlinedProjects() {
        lifecycleScope.launch {
            homeViewModel.getDeadlinedProjectsHeader()
                .observe(this@MainActivity, { deadlinedProjects ->
                    if (deadlinedProjects.isNotEmpty()) {
                        binding.apply {
                            noDataIv.visibility = View.GONE
                            noDataTv.visibility = View.GONE
                            seeMoreTv.visibility = View.VISIBLE
                        }

                        deadlinedProjectsAdapter =
                            DeadlinedProjectsHeaderAdapter(deadlinedProjects) { page, itemNum ->
                                //Go to ProjectDetailsActivity
                                val projectDetailsIntent =
                                    Intent(applicationContext, ProjectDetailsActivity::class.java)
                                projectDetailsIntent.putExtra("page", page)
                                projectDetailsIntent.putExtra("itemNum", itemNum)
                                startActivity(projectDetailsIntent)
                            }

                        binding.deadlinedProjectsRv.apply {
                            layoutManager = LinearLayoutManager(
                                context, LinearLayoutManager.HORIZONTAL, false
                            )
                            adapter = deadlinedProjectsAdapter
                        }
                    } else {
                        binding.apply {
                            noDataIv.visibility = View.VISIBLE
                            noDataTv.visibility = View.VISIBLE
                            seeMoreTv.visibility = View.INVISIBLE
                        }
                    }

                    setupLoadedUI()
                })
        }
    }

    private fun showMyProjects() {
        homeViewModel.getMyProjects().observe(this@MainActivity, { myProjects ->
            myProjectsAdapter = MyProjectsAdapter { page, itemNum ->
                //Go to ProjectDetailsActivity
                val projectDetailsIntent =
                    Intent(applicationContext, ProjectDetailsActivity::class.java)
                projectDetailsIntent.putExtra("page", page)
                projectDetailsIntent.putExtra("itemNum", itemNum)
                startActivity(projectDetailsIntent)
            }

            binding.myProjectsRv.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = myProjectsAdapter
            }

            myProjectsAdapter.submitData(this@MainActivity.lifecycle, myProjects)

            //Handling load state
            myProjectsAdapter.addLoadStateListener { loadState ->
                if (loadState.source.refresh is LoadState.NotLoading && myProjectsAdapter.itemCount < 1) {
                    binding.apply {
                        addYourProjectsIv.visibility = View.VISIBLE
                        addYourProjectsTv.visibility = View.VISIBLE
                    }
                } else {
                    binding.apply {
                        addYourProjectsIv.visibility = View.GONE
                        addYourProjectsTv.visibility = View.GONE
                    }

                    if (loadState.append.endOfPaginationReached) {
                        Log.d("Projekan", "End of pagination of my projects")
                    }
                }
            }
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }
}