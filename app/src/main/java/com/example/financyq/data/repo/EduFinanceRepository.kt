package com.example.financyq.data.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.financyq.data.api.ApiService
import kotlinx.coroutines.Dispatchers
import com.example.financyq.data.di.Result
import com.example.financyq.data.response.EduFinanceResponse

class EduFinanceRepository(private val apiService: ApiService) {

    fun getEducationFinance(): LiveData<Result<List<EduFinanceResponse>>> = liveData(Dispatchers.IO) {
        emit(Result.Loading)
        try {
            val response = apiService.getEducationFinance()
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
        }
    }

    companion object {
        @Volatile
        private var instance: EduFinanceRepository? = null

        fun getInstance(apiService: ApiService): EduFinanceRepository =
            instance ?: synchronized(this) {
                instance ?: EduFinanceRepository(apiService)
            }.also { instance = it }
    }
}