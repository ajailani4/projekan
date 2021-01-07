package com.ajailani.projekan.ui.view.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.ajailani.projekan.databinding.ActivityMainBinding
import com.ajailani.projekan.ui.adapter.MyProjectsAdapter
import com.ajailani.projekan.ui.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var myProjectsAdapter: MyProjectsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupList()
        setupView()
    }

    private fun setupList() {
        myProjectsAdapter = MyProjectsAdapter()

        binding.myProjectsRv.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = myProjectsAdapter
        }
    }

    private fun setupView() {
        lifecycleScope.launch {
            homeViewModel.getMyProjects()?.observe(this@MainActivity, { myProjects ->
                myProjectsAdapter.submitData(this@MainActivity.lifecycle, myProjects)
            })
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }
}