package com.example.financyq.data.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.financyq.data.api.ApiService
import com.example.financyq.data.di.Result
import com.example.financyq.data.request.UpdateIncomeRequest
import com.example.financyq.data.response.UpdateIncomeResponse
import retrofit2.HttpException

class UpdateIncomeRepository(private val apiService: ApiService) {

    fun updateIncome(idTransaksiPemasukan: String, updateIncomeRequest: UpdateIncomeRequest): LiveData<Result<UpdateIncomeResponse>> {
        return liveData {
            emit(Result.Loading)
            try {
                val response = apiService.updateIncome(idTransaksiPemasukan, updateIncomeRequest)
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        emit(Result.Success(responseBody))
                    } else {
                        emit(Result.Error("Response body is null"))
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = errorBody ?: "Unsuccessful response"
                    emit(Result.Error(errorMessage))
                }
            } catch (e: HttpException) {
                emit(Result.Error(e.message ?: "HTTP Exception"))
            } catch (e: Exception) {
                emit(Result.Error(e.message ?: "Unexpected error occurred"))
            }
        }
    }

    companion object {
        @Volatile
        private var instance: UpdateIncomeRepository? = null

        fun getInstance(apiService: ApiService): UpdateIncomeRepository =
            instance ?: synchronized(this) {
                instance ?: UpdateIncomeRepository(apiService).also { instance = it }
            }
    }
}
