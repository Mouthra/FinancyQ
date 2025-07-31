// file: com/example/financyq/ui/profile/UpdateProfileViewModel.kt
package com.example.financyq.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.financyq.data.di.Result
import com.example.financyq.data.repo.UpdateProfileRepository
import com.example.financyq.data.response.GenericMessageResponse

class UpdateProfileViewModel(
    private val repo: UpdateProfileRepository
) : ViewModel() {

    fun updateUsername(oldUsername: String, newUsername: String)
            : LiveData<Result<GenericMessageResponse>> {
        return repo.updateUsername(oldUsername, newUsername)
    }

    fun updatePassword(username: String, newPassword: String)
            : LiveData<Result<GenericMessageResponse>> {
        return repo.updatePassword(username, newPassword)
    }
}
