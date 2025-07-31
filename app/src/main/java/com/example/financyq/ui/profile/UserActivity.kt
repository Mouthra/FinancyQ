package com.example.financyq.ui.profile

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.financyq.R
import com.example.financyq.data.di.Result
import com.example.financyq.data.di.ViewModelFactory
import com.example.financyq.data.local.UserPreferences
import com.example.financyq.databinding.ActivityUserBinding
import com.example.financyq.databinding.BottomSheetEditPasswordBinding
import com.example.financyq.databinding.BottomSheetEditUsernameBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class UserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserBinding
    private lateinit var userPrefs: UserPreferences
    private lateinit var currentUsername: String

    // ViewModels
    private val usernameViewModel: UsernameViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }
    private val updateProfileViewModel: UpdateProfileViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userPrefs = UserPreferences.getInstance(this)
        // Ambil username dari SharedPreferences
        currentUsername = runBlocking { userPrefs.userNameFlow.first() }.orEmpty()

        setupUi()
        loadUserData()
    }

    private fun setupUi() {
        // Tombol back
        binding.btnBack.setOnClickListener { finish() }

        // Field Username clickable untuk edit
        binding.nameEditText.apply {
            isFocusable = false
            isClickable = true
            setOnClickListener { showEditUsernameSheet() }
        }

        // Field Password clickable untuk edit
        binding.passwordEditText.apply {
            isFocusable = false
            isClickable = true
            // selalu tampil placeholder
            setText("••••••••")
            setOnClickListener { showEditPasswordSheet() }
        }
    }

    private fun loadUserData() {
        usernameViewModel.getUsername(currentUsername).observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    // optional: tampilkan progress
                }
                is Result.Success -> {
                    binding.nameEditText.setText(result.data.username)
                }
                is Result.Error -> {
                    Toast.makeText(
                        this,
                        "Gagal memuat profil: ${result.error}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun showEditUsernameSheet() {
        // Inflate bottom sheet layout
        val sheetBinding = BottomSheetEditUsernameBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(this).apply {
            setContentView(sheetBinding.root)
            window
                ?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
                ?.setBackgroundResource(R.drawable.rounded_corner_background_sheet)
        }

        // Prefill current username
        sheetBinding.etNewUsername.setText(currentUsername)
        sheetBinding.btnClose.setOnClickListener { dialog.dismiss() }

        sheetBinding.btnSubmitUsername.setOnClickListener {
            val newU = sheetBinding.etNewUsername.text.toString().trim()
            if (newU.isEmpty()) {
                sheetBinding.tilNewUsername.error = "Username tidak boleh kosong"
                return@setOnClickListener
            }
            // Panggil ViewModel untuk update
            updateProfileViewModel.updateUsername(currentUsername, newU)
                .observe(this) { r ->
                    when (r) {
                        is Result.Loading -> {
                            // optional: disable button / show loading
                        }
                        is Result.Success -> {
                            // Sukses: update UI & prefs
                            currentUsername = newU
                            binding.nameEditText.setText(newU)
                            runBlocking { userPrefs.saveUsername(newU) }
                            Toast.makeText(this, "Username diperbarui", Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                        }
                        is Result.Error -> {
                            Toast.makeText(this, "Error: ${r.error}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
        }

        dialog.show()
    }

    private fun showEditPasswordSheet() {
        val sheetBinding = BottomSheetEditPasswordBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(this).apply {
            setContentView(sheetBinding.root)
            window
                ?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
                ?.setBackgroundResource(R.drawable.rounded_corner_background_sheet)
        }

        sheetBinding.btnClose.setOnClickListener { dialog.dismiss() }
        sheetBinding.btnSubmitPassword.setOnClickListener {
            val oldP = sheetBinding.etOldPassword.text.toString().trim()
            val newP = sheetBinding.etNewPassword.text.toString().trim()
            when {
                oldP.isEmpty() -> {
                    sheetBinding.tilOldPassword.error = "Password lama diperlukan"
                    return@setOnClickListener
                }
                newP.length < 8 -> {
                    sheetBinding.tilNewPassword.error = "Minimal 8 karakter"
                    return@setOnClickListener
                }
                else -> {
                    // Panggil ViewModel untuk update password
                    updateProfileViewModel.updatePassword(currentUsername, newP)
                        .observe(this) { r ->
                            when (r) {
                                is Result.Loading -> {
                                    // optional: disable button / show loading
                                }
                                is Result.Success -> {
                                    Toast.makeText(
                                        this,
                                        "Password diperbarui",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    dialog.dismiss()
                                }
                                is Result.Error -> {
                                    Toast.makeText(this, "Error: ${r.error}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                }
            }
        }

        dialog.show()
    }
}
