package com.example.financyq.data.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.financyq.data.api.ApiService
import com.example.financyq.data.di.Result
import com.example.financyq.data.local.UserPreferences
import com.example.financyq.data.request.AddIncomeRequest
import com.example.financyq.data.response.AddIncomeResponse
import retrofit2.HttpException

class AddIncomeRepository(private val apiService: ApiService, private val userPreferences: UserPreferences) {

    fun addIncome(addIncomeRequest: AddIncomeRequest): LiveData<Result<AddIncomeResponse>> =
        liveData {
            emit(Result.Loading)
            try {
                val response = apiService.addIncome(addIncomeRequest)
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        responseBody.idTransaksiPemasukan?.let { idTransaction ->
                            userPreferences.saveIdtransactionincome(idTransaction)
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
        private var instance: AddIncomeRepository? = null

        fun getInstance(
            apiService: ApiService,
            userPreferences: UserPreferences
        ): AddIncomeRepository =
            instance ?: synchronized(this) {
                instance ?: AddIncomeRepository(apiService, userPreferences)
                    .also { instance = it }
            }
    }
}
