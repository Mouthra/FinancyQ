// file: com/example/financyq/data/repo/UpdateProfileRepository.kt
package com.example.financyq.data.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.financyq.data.api.ApiService
import com.example.financyq.data.di.Result
import com.example.financyq.data.request.UpdatePasswordRequest
import com.example.financyq.data.request.UpdateUsernameRequest
import com.example.financyq.data.response.GenericMessageResponse
import retrofit2.HttpException

class UpdateProfileRepository(private val apiService: ApiService) {

    fun updateUsername(
        oldUsername: String,
        newUsername: String
    ): LiveData<Result<GenericMessageResponse>> = liveData {
        emit(Result.Loading)
        try {
            val resp = apiService.updateUsername(
                oldUsername,
                UpdateUsernameRequest(username = newUsername)
            )
            if (resp.isSuccessful && resp.body() != null) {
                emit(Result.Success(resp.body()!!))
            } else {
                emit(Result.Error(resp.errorBody()?.string() ?: "Gagal mengubah username"))
            }
        } catch (e: HttpException) {
            emit(Result.Error(e.message() ?: "HTTP error"))
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Unknown error"))
        }
    }

    fun updatePassword(
        username: String,
        newPassword: String
    ): LiveData<Result<GenericMessageResponse>> = liveData {
        emit(Result.Loading)
        try {
            val resp = apiService.updatePassword(
                username,
                UpdatePasswordRequest(password = newPassword)
            )
            if (resp.isSuccessful && resp.body() != null) {
                emit(Result.Success(resp.body()!!))
            } else {
                emit(Result.Error(resp.errorBody()?.string() ?: "Gagal mengubah password"))
            }
        } catch (e: HttpException) {
            emit(Result.Error(e.message() ?: "HTTP error"))
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Unknown error"))
        }
    }

    companion object {
        @Volatile private var instance: UpdateProfileRepository? = null

        fun getInstance(api: ApiService): UpdateProfileRepository =
            instance ?: synchronized(this) {
                instance ?: UpdateProfileRepository(api).also { instance = it }
            }
    }
}
