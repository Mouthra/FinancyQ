package com.example.financyq.data.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.financyq.data.api.ApiService
import com.example.financyq.data.di.Result
import com.example.financyq.data.request.UpdateExpenditureRequest
import com.example.financyq.data.response.UpdateExpenditureResponse
import retrofit2.HttpException

class UpdateExpenditureRepository(private val apiService: ApiService) {

    fun updateExpenditure(idTransaksiPengeluaran: String, updateExpenditureRequest: UpdateExpenditureRequest): LiveData<Result<UpdateExpenditureResponse>> {
        return liveData {
            emit(Result.Loading)
            try {
                val response = apiService.updateExpenditure(idTransaksiPengeluaran, updateExpenditureRequest)
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
        private var instance: UpdateExpenditureRepository? = null

        fun getInstance(apiService: ApiService): UpdateExpenditureRepository =
            instance ?: synchronized(this) {
                instance ?: UpdateExpenditureRepository(apiService)
                    .also { instance = it }
            }
    }
}
