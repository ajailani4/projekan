package com.ajailani.projekan.ui.view.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.ajailani.projekan.R
import com.ajailani.projekan.databinding.ActivityMainBinding
import com.ajailani.projekan.ui.adapter.DeadlinedProjectsAdapter
import com.ajailani.projekan.ui.adapter.MyProjectsAdapter
import com.ajailani.projekan.ui.viewmodel.HomeViewModel
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var deadlinedProjectsAdapter: DeadlinedProjectsAdapter
    private lateinit var myProjectsAdapter: MyProjectsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupLoadingUI()
        setupView()
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
        }
    }

    private fun setupLoadedUI() {
        binding.apply {
            shimmerLayout.stopShimmerAnimation()
            shimmerLayout.visibility = View.GONE

            helloUserTv.visibility = View.VISIBLE
            greetingsTv.visibility = View.VISIBLE
            userAvaIv.visibility = View.VISIBLE
            deadlinedProjectsRv.visibility = View.VISIBLE
            myProjectsRv.visibility = View.VISIBLE
        }
    }

    private fun setupView() {
        //Show user's name
        homeViewModel.getUserName()?.observe(this, { userName ->
            binding.helloUserTv.text = getString(R.string.hello_user, userName)
        })

        //Show user's ava
        homeViewModel.getUserAva()?.observe(this, { userAva ->
            Glide.with(this)
                .load(userAva)
                .into(binding.userAvaIv)
        })

        //Show deadlined projects list for header
        lifecycleScope.launch {
            homeViewModel.getDeadlinedProjectsHeader()?.observe(this@MainActivity, { deadlinedProjects ->
                if(deadlinedProjects.isNotEmpty()) {
                    binding.noDataIv.visibility = View.GONE
                    binding.noDataTv.visibility = View.GONE
                    binding.seeMoreTv.visibility = View.VISIBLE

                    deadlinedProjectsAdapter = DeadlinedProjectsAdapter(deadlinedProjects)

                    binding.deadlinedProjectsRv.apply {
                        layoutManager = LinearLayoutManager(
                            context, LinearLayoutManager.HORIZONTAL, false
                        )
                        adapter = deadlinedProjectsAdapter
                    }
                } else {
                    binding.noDataIv.visibility = View.VISIBLE
                    binding.noDataTv.visibility = View.VISIBLE
                    binding.seeMoreTv.visibility = View.INVISIBLE
                }

                setupLoadedUI()
            })
        }

        //Show my projects list
        lifecycleScope.launch {
            homeViewModel.getMyProjects()?.observe(this@MainActivity, { myProjects ->
                myProjectsAdapter = MyProjectsAdapter { page, itemNum ->
                    //Go to ProjectDetailsActivity
                    val projectDetailsIntent = Intent(applicationContext, ProjectDetailsActivity::class.java)
                    projectDetailsIntent.putExtra("page", page)
                    projectDetailsIntent.putExtra("itemNum", itemNum)
                    startActivity(projectDetailsIntent)
                }

                binding.myProjectsRv.apply {
                    layoutManager = LinearLayoutManager(context)
                    adapter = myProjectsAdapter
                }

                myProjectsAdapter.submitData(this@MainActivity.lifecycle, myProjects)

                myProjectsAdapter.addLoadStateListener { loadState ->
                    if(loadState.source.refresh is LoadState.NotLoading && myProjectsAdapter.itemCount < 1) {
                        binding.addYourProjectsIv.visibility = View.VISIBLE
                        binding.addYourProjectsTv.visibility = View.VISIBLE
                    } else {
                        binding.addYourProjectsIv.visibility = View.GONE
                        binding.addYourProjectsTv.visibility = View.GONE
                    }
                }
            })
        }

        //Add project
        binding.addProject.setOnClickListener {
            val addProjectIntent = Intent(applicationContext, AddProjectActivity::class.java)
            addProjectIntent.putExtra("tag", "Add")
            startActivity(addProjectIntent)
        }
    }
}