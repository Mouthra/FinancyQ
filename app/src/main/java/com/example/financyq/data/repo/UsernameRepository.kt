package com.example.financyq.data.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.financyq.data.api.ApiService
import kotlinx.coroutines.Dispatchers
import com.example.financyq.data.di.Result
import com.example.financyq.data.response.UsernameResponse
import java.io.IOException

class UsernameRepository(private val apiService: ApiService) {

    fun getUsername(username: String): LiveData<Result<UsernameResponse>> = liveData(Dispatchers.IO) {
        emit(Result.Loading)
        try {
            val response = apiService.getUsername(username)
            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    emit(Result.Success(responseBody))
                } else {
                    emit(Result.Error("Response body is null"))
                }
            } else {
                emit(Result.Error(response.message() ?: "Unknown error"))
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Exception occurred"))
        }catch (e: IOException) {
            emit(Result.Error("Network error: ${e.localizedMessage}"))
        }
    }

    companion object {
        @Volatile
        private var instance: UsernameRepository? = null

        fun getInstance(apiService: ApiService): UsernameRepository =
            instance ?: synchronized(this) {
                instance ?: UsernameRepository(apiService)
            }.also { instance = it }
    }
}
