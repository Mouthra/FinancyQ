package com.example.financyq.ui.otp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.financyq.R
import com.example.financyq.data.di.Result
import com.example.financyq.data.di.ViewModelFactory
import com.example.financyq.data.request.OtpRequest
import com.example.financyq.data.request.SignupRequest
import com.example.financyq.databinding.ActivityOtpBinding
import com.example.financyq.ui.login.LoginActivity

class OtpActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOtpBinding
    private val otpViewModel by viewModels<OtpViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private var email = ""
    private var password = ""
    private var username = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ambil data Intent
        email = intent.getStringExtra(EXTRA_EMAIL).orEmpty()
        password = intent.getStringExtra(EXTRA_PASSWORD).orEmpty()
        username = intent.getStringExtra(EXTRA_USERNAME).orEmpty()

        binding.tvDescEmail.text = email

        setupActionResendOtp()
        setupActionVerifyOtp()
    }

    private fun setupActionResendOtp() {
        binding.tvResend.setOnClickListener {
            // Tampilkan overlay loading, disable tombol Resend
            binding.loadingCard.visibility = View.VISIBLE
            binding.tvResend.isEnabled = false

            val signupRequest = SignupRequest(username, email, password)
            otpViewModel.resendSignup(signupRequest).observe(this) { result ->
                when (result) {
                    is Result.Loading -> {
                        // loadingCard sudah tampil
                    }
                    is Result.Success -> {
                        // sembunyikan loading, enable tombol
                        binding.loadingCard.visibility = View.GONE
                        binding.tvResend.isEnabled = true

                        AlertDialog.Builder(this).apply {
                            setTitle(R.string.resend_success)
                            setMessage(R.string.otp_has_been_resent_successfully)
                            setPositiveButton(R.string.ok) { dialog, _ ->
                                dialog.dismiss()
                            }
                            setCancelable(false)
                            create()
                            show()
                        }
                    }
                    is Result.Error -> {
                        binding.loadingCard.visibility = View.GONE
                        binding.tvResend.isEnabled = true
                        Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun setupActionVerifyOtp() {
        binding.btnVerify.setOnClickListener {
            val otp = binding.OtpEditText.text.toString().trim()
            if (otp.isEmpty()) {
                Toast.makeText(this, R.string.otp_is_empty, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Tampilkan overlay loading, disable tombol Verify
            binding.loadingCard.visibility = View.VISIBLE
            binding.btnVerify.isEnabled = false

            val otpRequest = OtpRequest(email = email, otp = otp)
            otpViewModel.verifyOtp(otpRequest).observe(this) { result ->
                when (result) {
                    is Result.Loading -> {
                        // loadingCard sudah tampil
                    }
                    is Result.Success -> {
                        binding.loadingCard.visibility = View.GONE
                        AlertDialog.Builder(this).apply {
                            setTitle(R.string.verification_successfull)
                            setMessage(R.string.otp_verification_was_successfull)
                            setPositiveButton(R.string.ok) { dialog, _ ->
                                dialog.dismiss()
                                startActivity(Intent(this@OtpActivity, LoginActivity::class.java))
                                finish()
                            }
                            setCancelable(false)
                            create()
                            show()
                        }
                    }
                    is Result.Error -> {
                        // Sembunyikan loading, enable tombol kembali
                        binding.loadingCard.visibility = View.GONE
                        binding.btnVerify.isEnabled = true
                        Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    companion object {
        const val EXTRA_EMAIL = "EXTRA_EMAIL"
        const val EXTRA_USERNAME = "EXTRA_USERNAME"
        const val EXTRA_PASSWORD = "EXTRA_PASSWORD"
    }
}
