package com.example.financyq.data.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.financyq.data.api.ApiService
import com.example.financyq.data.di.Result
import com.example.financyq.data.local.UserPreferences
import com.example.financyq.data.request.AddExpenditureRequest
import com.example.financyq.data.response.AddExpenditureResponse
import retrofit2.HttpException

class AddExpenditureRepository(private val apiService: ApiService, private val userPreferences: UserPreferences) {
    fun addExpenditure(addExpenditureRequest: AddExpenditureRequest): LiveData<Result<AddExpenditureResponse>> =
        liveData {
            emit(Result.Loading)
            try {
                val response = apiService.addExpenditure(addExpenditureRequest)
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        responseBody.idTransaksiPengeluaran?.let { idTransaction ->
                            userPreferences.saveIdtransactionexpenditure(idTransaction)
                        }
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

    companion object {
        @Volatile
        private var instance: AddExpenditureRepository? = null

        fun getInstance(
            apiService: ApiService,
            userPreferences: UserPreferences
        ): AddExpenditureRepository =
            instance ?: synchronized(this) {
                instance ?: AddExpenditureRepository(apiService, userPreferences)
                    .also { instance = it }
            }
    }
}