package com.example.financyq.data.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.financyq.data.api.ApiService
import com.example.financyq.data.di.Result
import kotlinx.coroutines.Dispatchers
import okhttp3.ResponseBody

class ExportPdfRepository(private val apiService: ApiService) {

    fun exportPDF(idUser: String): LiveData<Result<ResponseBody>> = liveData(Dispatchers.IO) {
        emit(Result.Loading)
        try {
            val response = apiService.exportPDF(idUser)
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
        private var instance: ExportPdfRepository? = null

        fun getInstance(apiService: ApiService): ExportPdfRepository =
            instance ?: synchronized(this) {
                instance ?: ExportPdfRepository(apiService)
            }.also { instance = it }
    }
}
