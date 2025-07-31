package com.example.financyq.ui.manual

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import com.example.financyq.R
import com.example.financyq.databinding.ActivityManualBinding

class ManualActivity : AppCompatActivity() {

    private lateinit var binding: ActivityManualBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManualBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupViews()

        binding.btnBack.setOnClickListener{
            finish()
        }
    }

    private fun setupViews() {
        binding.apply {
            btnIncome.isSelected = true
            replaceFragment(IncomeFragment())
            btnIncome.background = AppCompatResources.getDrawable(this@ManualActivity, R.drawable.btn_left_pressed)
            btnExpenditure.background = AppCompatResources.getDrawable(this@ManualActivity, R.drawable.btn_right_default)

            btnIncome.setOnClickListener {
                btnIncome.isSelected = true
                btnExpenditure.isSelected = false
                btnIncome.background = AppCompatResources.getDrawable(this@ManualActivity, R.drawable.btn_left_pressed)
                btnExpenditure.background = AppCompatResources.getDrawable(this@ManualActivity, R.drawable.btn_right_default)
                replaceFragment(IncomeFragment())
            }

            btnExpenditure.setOnClickListener {
                btnExpenditure.isSelected = true
                btnIncome.isSelected = false
                btnIncome.background = AppCompatResources.getDrawable(this@ManualActivity, R.drawable.btn_left_default)
                btnExpenditure.background = AppCompatResources.getDrawable(this@ManualActivity, R.drawable.btn_right_pressed)
                replaceFragment(ExpenditureFragment())
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}