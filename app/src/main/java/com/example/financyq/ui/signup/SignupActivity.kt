package com.example.financyq.ui.signup

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.example.financyq.R
import com.example.financyq.data.di.Result
import com.example.financyq.data.di.ViewModelFactory
import com.example.financyq.data.request.SignupRequest
import com.example.financyq.databinding.ActivitySignupBinding
import com.example.financyq.ui.otp.OtpActivity

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private val signUpViewModel by viewModels<SignUpViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Mulai dengan tombol disabled
        binding.signupButton.isEnabled = false
        binding.signupButton.alpha = 0.5f

        // Pasang listener validasi real-time
        binding.nameEditText.doAfterTextChanged     { validateInputs() }
        binding.emailEditText.doAfterTextChanged    { validateInputs() }
        binding.passwordEditText.doAfterTextChanged { validateInputs() }

        setupAction()
    }

    private fun validateInputs() {
        val name     = binding.nameEditText.text.toString().trim()
        val email    = binding.emailEditText.text.toString().trim()
        val password = binding.passwordEditText.text.toString()

        // Cek masing-masing
        val isNameValid  = name.isNotEmpty()
        val isEmailValid = Patterns.EMAIL_ADDRESS.matcher(email).matches()
        val isPassValid  = password.length >= 8

        // Enable hanya jika semua valid
        val allValid = isNameValid && isEmailValid && isPassValid
        binding.signupButton.isEnabled = allValid
        binding.signupButton.alpha     = if (allValid) 1f else 0.5f
    }

    private fun setupAction() {
        binding.signupButton.setOnClickListener {
            // safety-net: pastikan sekali lagi
            val name     = binding.nameEditText.text.toString().trim()
            val email    = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, R.string.please_fill_in_all_columns, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Tampilkan overlay loading & disable tombol
            binding.loadingCard.visibility     = View.VISIBLE
            binding.signupButton.isEnabled     = false

            val request = SignupRequest(name, email, password)
            signUpViewModel.register(request).observe(this) { result ->
                when (result) {
                    is Result.Loading -> {
                        // overlay sudah tampil
                    }
                    is Result.Success -> {
                        binding.loadingCard.visibility = View.GONE
                        showConfirmationDialog(name, email, password)
                    }
                    is Result.Error -> {
                        binding.loadingCard.visibility = View.GONE
                        binding.signupButton.isEnabled = true

                        val msg = result.error
                        when {
                            "Username or email already exists" in msg ->
                                Toast.makeText(this, R.string.username_or_email_already_exists, Toast.LENGTH_SHORT).show()
                            "Invalid email format" in msg ->
                                Toast.makeText(this, R.string.invalid_email_format, Toast.LENGTH_SHORT).show()
                            else ->
                                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun showConfirmationDialog(username: String, email: String, password: String) {
        AlertDialog.Builder(this).apply {
            setTitle(R.string.title_set)
            setMessage(R.string.message_set)
            setPositiveButton(R.string.continue_set) { _, _ ->
                Intent(this@SignupActivity, OtpActivity::class.java).also {
                    it.putExtra(OtpActivity.EXTRA_USERNAME, username)
                    it.putExtra(OtpActivity.EXTRA_EMAIL, email)
                    it.putExtra(OtpActivity.EXTRA_PASSWORD, password)
                    startActivity(it)
                }
                finish()
            }
            setCancelable(false)
            create()
            show()
        }
    }
}
