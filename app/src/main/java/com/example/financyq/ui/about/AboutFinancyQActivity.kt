package com.example.financyq.ui.about

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.financyq.databinding.ActivityAboutFinancyQBinding

class AboutFinancyQActivity : AppCompatActivity() {

    private lateinit var binding:ActivityAboutFinancyQBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutFinancyQBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActions()
    }
    private fun setupActions(){
        binding.btnBack.setOnClickListener {
            finish()
        }
    }
}