package com.ajailani.projekan.ui.view.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.ajailani.projekan.databinding.ActivitySplashBinding
import com.ajailani.projekan.ui.viewmodel.SplashViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    private val splashViewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Check user authentication
        splashViewModel.checkUserAuth().observe(this, { isUserAuth ->
            if (isUserAuth) {
                val homeIntent = Intent(applicationContext, MainActivity::class.java)
                startActivity(homeIntent)
                finish()
            } else {
                val loginIntent = Intent(this, LoginActivity::class.java)
                startActivity(loginIntent)
                finish()
            }
        })
    }
}