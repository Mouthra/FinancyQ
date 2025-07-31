package com.example.financyq.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.example.financyq.MainActivity
import com.example.financyq.R
import com.example.financyq.data.di.Result
import com.example.financyq.data.di.ViewModelFactory
import com.example.financyq.data.request.LoginRequest
import com.example.financyq.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel: LoginViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Mulai dengan tombol disabled
        binding.loginButton.isEnabled = false
        binding.loginButton.alpha = 0.5f

        // Pasang listener untuk validasi setiap input berubah
        binding.emailEditText.doAfterTextChanged { validateInputs() }
        binding.passwordEditText.doAfterTextChanged { validateInputs() }

        setupAction()
    }

    private fun validateInputs() {
        val email = binding.emailEditText.text.toString().trim()
        val pass = binding.passwordEditText.text.toString()

        val isEmailValid = Patterns.EMAIL_ADDRESS.matcher(email).matches()
        val isPassValid = pass.length >= 8

        binding.loginButton.isEnabled = isEmailValid && isPassValid
        binding.loginButton.alpha = if (binding.loginButton.isEnabled) 1f else 0.5f
    }

    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            // Tampilkan overlay loading & disable tombol
            binding.loadingCard.visibility = View.VISIBLE
            binding.loginButton.isEnabled = false

            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString()

            val loginRequest = LoginRequest(email, password)
            loginViewModel.login(loginRequest).observe(this) { result ->
                when (result) {
                    is Result.Loading -> {
                        // overlay sudah tampil
                    }
                    is Result.Success -> {
                        // sembunyikan overlay, lanjut ke Main
                        binding.loadingCard.visibility = View.GONE
                        showCongratulationsDialog()
                    }
                    is Result.Error -> {
                        // sembunyikan overlay + re-enable tombol
                        binding.loadingCard.visibility = View.GONE
                        binding.loginButton.isEnabled = true

                        val errorMessage = result.error
                        when {
                            "Invalid password" in errorMessage ->
                                Toast.makeText(this, R.string.invalid_password, Toast.LENGTH_SHORT).show()
                            "User not found" in errorMessage ->
                                Toast.makeText(this, R.string.user_not_found, Toast.LENGTH_SHORT).show()
                            else ->
                                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun showCongratulationsDialog() {
        AlertDialog.Builder(this).apply {
            setTitle(R.string.congratulations)
            setMessage(R.string.you_have_successfully_login)
            setPositiveButton(R.string.continue_value) { _, _ ->
                startActivity(
                    Intent(this@LoginActivity, MainActivity::class.java)
                        .apply {
                            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                )
                finish()
            }
            setCancelable(false)
            create()
            show()
        }
    }
}
