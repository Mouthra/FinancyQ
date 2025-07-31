package com.example.financyq.ui.about

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.financyq.databinding.ActivityPrivacyPolicyBinding

class PrivacyPolicyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPrivacyPolicyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrivacyPolicyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActions()
    }

    private fun setupActions(){
        binding.btnBack.setOnClickListener {
            finish()
        }
    }
}