package com.example.financyq.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.financyq.data.di.Result
import com.example.financyq.data.repo.UsernameRepository
import com.example.financyq.data.response.UsernameResponse

class UsernameViewModel(private val usernameRepository: UsernameRepository) : ViewModel() {

    fun getUsername(username: String): LiveData<Result<UsernameResponse>> = usernameRepository.getUsername(username)
}
