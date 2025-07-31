package com.example.financyq.data.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.financyq.data.api.ApiService
import com.example.financyq.data.di.Result
import com.example.financyq.data.response.DeleteResponse
import kotlinx.coroutines.Dispatchers
import retrofit2.HttpException

class DeleteExpenditureRepository(private val apiService: ApiService) {

    fun deleteExpenditure(idTransaksi: String): LiveData<Result<DeleteResponse>> = liveData(Dispatchers.IO) {
        emit(Result.Loading)
        try {
            val response = apiService.deleteExpenditure(idTransaksi)
            if (response.isSuccessful) {
                emit(Result.Success(response.body()!!))
            } else {
                emit(Result.Error("Delete unsuccessful"))
            }
        } catch (e: HttpException) {
            emit(Result.Error(e.message ?: "Unknown error"))
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Unexpected error occurred"))
        }
    }

    companion object {
        @Volatile
        private var instance: DeleteExpenditureRepository? = null

        fun getInstance(apiService: ApiService): DeleteExpenditureRepository =
            instance ?: synchronized(this) {
                instance ?: DeleteExpenditureRepository(apiService)
            }.also { instance = it }
    }
}
