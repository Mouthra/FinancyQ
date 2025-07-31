package com.example.financyq.data.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.financyq.data.api.ApiService
import com.example.financyq.data.di.Result
import com.example.financyq.data.response.TotalResponse
import kotlinx.coroutines.Dispatchers

class TotalExpenditureRepository(private val apiService: ApiService) {

    fun getTotalExpenditure(idUser: String): LiveData<Result<TotalResponse>> = liveData(Dispatchers.IO) {
        emit(Result.Loading)
        try {
            val response = apiService.getTotalExpenditure(idUser)
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
        private var instance: TotalExpenditureRepository? = null

        fun getInstance(apiService: ApiService): TotalExpenditureRepository =
            instance ?: synchronized(this) {
                instance ?: TotalExpenditureRepository(apiService)
            }.also { instance = it }
    }
}
