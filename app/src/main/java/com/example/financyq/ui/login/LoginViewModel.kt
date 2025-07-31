
package com.example.financyq.ui.login

import androidx.lifecycle.ViewModel
import com.example.financyq.data.repo.UserRepository
import com.example.financyq.data.request.LoginRequest

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {
    fun login(loginRequest: LoginRequest) = userRepository.login(loginRequest)
}
