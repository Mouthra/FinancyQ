package com.example.financyq.ui.otp

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.financyq.data.di.Result
import com.example.financyq.data.repo.UserRepository
import com.example.financyq.data.request.OtpRequest
import com.example.financyq.data.request.SignupRequest
import com.example.financyq.data.response.OtpResponse
import com.example.financyq.data.response.SignUpResponse

class OtpViewModel(private val userRepository: UserRepository): ViewModel() {
    fun verifyOtp(otpRequest: OtpRequest): LiveData<Result<OtpResponse>> {
        return userRepository.verifyOtp(otpRequest)
    }
    fun resendSignup(signupRequest: SignupRequest): LiveData<Result<SignUpResponse>> {
        return userRepository.register(signupRequest)
    }
}