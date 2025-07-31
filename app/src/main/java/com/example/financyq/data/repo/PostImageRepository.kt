package com.example.financyq.data.repo

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.financyq.data.api.ApiService
import com.example.financyq.data.di.Result
import com.example.financyq.data.response.PostImageResponse
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import okhttp3.MultipartBody
import retrofit2.HttpException
import java.io.IOException

class PostImageRepository(
    private val apiService: ApiService
) {

    fun postImage(image: MultipartBody.Part): LiveData<Result<PostImageResponse>> =
        liveData(Dispatchers.IO) {
            emit(Result.Loading)
            try {
                val response = apiService.postImage(image)

                if (response.isSuccessful) {
                    val body = response.body()
                    val bodyString = body?.string()

                    if (!bodyString.isNullOrBlank()) {
                        try {
                            val parsed = Gson().fromJson(bodyString, PostImageResponse::class.java)

                            // ✅ Jika items kosong, tetap emit Success agar ViewModel bisa validasi
                            emit(Result.Success(parsed))

                        } catch (jsonErr: Exception) {
                            emit(Result.Error("Gagal membaca respon dari server"))
                        }
                    } else {
                        emit(Result.Error("Response body kosong"))
                    }

                } else {
                    // ✅ Ambil error body agar lebih jelas
                    val err = response.errorBody()?.string() ?: response.message()
                    emit(Result.Error("Error ${response.code()}: $err"))
                }

            } catch (e: IOException) {
                emit(Result.Error("Network error: ${e.localizedMessage}"))
            } catch (e: HttpException) {
                emit(Result.Error("HTTP ${e.code()}: ${e.message}"))
            } catch (e: Exception) {
                // ✅ Lebih jelas daripada hanya "Unexpected error"
                emit(Result.Error("Terjadi kesalahan: ${e.localizedMessage}"))
            }
        }

    companion object {
        @Volatile
        private var instance: PostImageRepository? = null

        fun getInstance(apiService: ApiService): PostImageRepository =
            instance ?: synchronized(this) {
                instance ?: PostImageRepository(apiService).also { instance = it }
            }
    }
}
