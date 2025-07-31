package com.example.financyq.ui.profile

import androidx.lifecycle.ViewModel
import com.example.financyq.data.repo.UserRepository
import com.example.financyq.data.request.LoginRequest
import com.example.financyq.data.request.LogoutRequest

class LogoutViewModel(private val userRepository: UserRepository) : ViewModel() {
    fun logout(logoutRequest: LogoutRequest) = userRepository.logout(logoutRequest)
}