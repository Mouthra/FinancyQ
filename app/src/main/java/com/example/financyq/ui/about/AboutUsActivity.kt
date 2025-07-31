package com.example.financyq.ui.about

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.financyq.databinding.ActivityAboutUsBinding

class AboutUsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAboutUsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutUsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActions()
    }

    private fun setupActions(){
        binding.btnBack.setOnClickListener {
            finish()
        }
    }
}