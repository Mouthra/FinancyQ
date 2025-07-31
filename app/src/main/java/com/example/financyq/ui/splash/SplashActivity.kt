package com.example.financyq.ui.splash

import android.content.Intent
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.financyq.MainActivity
import com.example.financyq.R
import com.example.financyq.data.local.UserPreferences
import com.example.financyq.ui.welcome.WelcomeActivity
import com.example.financyq.databinding.ActivitySplashBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        setAnimation()

        val userPreferences = UserPreferences.getInstance(this)

        lifecycleScope.launch {
            delay(2000)
            val token = userPreferences.tokenFlow.firstOrNull()
            val intent = if (token != null) {
                Intent(this@SplashActivity, MainActivity::class.java)
            } else {
                Intent(this@SplashActivity, WelcomeActivity::class.java)
            }
            startActivity(intent)
            finish()
        }
    }

    private fun setAnimation() {
        val fadeIn: Animation = AnimationUtils.loadAnimation(this, R.anim.fade)
        val slideUp: Animation = AnimationUtils.loadAnimation(this, R.anim.slide_up)

        binding.apply {
            logo.startAnimation(fadeIn)
            title.startAnimation(slideUp)
            team.startAnimation(slideUp)
            gp1.startAnimation(fadeIn)
            gp2.startAnimation(fadeIn)
        }
    }
}
