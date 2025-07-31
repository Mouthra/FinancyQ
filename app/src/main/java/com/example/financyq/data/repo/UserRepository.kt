package com.example.financyq.data.repo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.financyq.data.api.ApiService
import com.example.financyq.data.response.SignUpResponse
import com.example.financyq.data.di.Result
import com.example.financyq.data.local.UserPreferences
import com.example.financyq.data.request.LoginRequest
import com.example.financyq.data.request.LogoutRequest
import com.example.financyq.data.request.OtpRequest
import com.example.financyq.data.request.SignupRequest
import com.example.financyq.data.response.LoginResponse
import com.example.financyq.data.response.LogoutResponse
import com.example.financyq.data.response.OtpResponse
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.HttpException

class UserRepository(private val apiService: ApiService, private val userPreferences: UserPreferences) {

    fun register(signupRequest: SignupRequest): LiveData<Result<SignUpResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.register(signupRequest)
            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    if (responseBody.success == true) {
                        emit(Result.Success(responseBody))
                    } else {
                        emit(Result.Error(responseBody.message ?: "Unknown error"))
                    }
                } else {
                    emit(Result.Error("Response body is null"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = if (response.code() == 400 && errorBody != null) {
                    val json = JSONObject(errorBody)
                    json.optString("message", "Username or email already exists")
                } else {
                    "Unsuccessful response"
                }
                emit(Result.Error(errorMessage))
            }
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, SignUpResponse::class.java)
            emit(Result.Error(errorResponse.message ?: "Unknown error"))
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "An unexpected error occurred"))
        }
    }


    fun verifyOtp(otpRequest: OtpRequest): LiveData<Result<OtpResponse>> =
        liveData {
            emit(Result.Loading)
            try {
                val response = apiService.verifyOtp(otpRequest)
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        if (responseBody.success == true) {
                            emit(Result.Success(responseBody))
                        } else {
                            emit(Result.Error(responseBody.message ?: "Unknown error"))
                        }
                    } else {
                        emit(Result.Error("Response body is null"))
                    }
                } else {
                    emit(Result.Error("Unsuccessful response"))
                }
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorResponse = Gson().fromJson(errorBody, OtpResponse::class.java)
                emit(Result.Error(errorResponse.message ?: "Unknown Error"))
            } catch (e: Exception) {
                emit(Result.Error(e.message ?: "Unexpected error occurred"))
            }
        }

    fun login(loginRequest: LoginRequest): LiveData<Result<LoginResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.login(loginRequest)
            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    responseBody.refreshToken?.let { token ->
                        withContext(Dispatchers.IO) {
                            userPreferences.saveToken(token)
                        }
                    }
                    responseBody.userId?.let { id ->
                        withContext(Dispatchers.IO) {
                            userPreferences.saveIdUser(id)
                        }
                    }
                    responseBody.username?.let { username ->
                        withContext(Dispatchers.IO) {
                            userPreferences.saveUsername(username)
                        }
                    }
                    emit(Result.Success(responseBody))
                } else {
                    emit(Result.Error("Response body is null"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = if (response.code() == 400 && errorBody != null) {
                    val json = JSONObject(errorBody)
                    json.optString("message", "Email atau password salah")
                } else {
                    "Unsuccessful response"
                }
                emit(Result.Error(errorMessage))
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun logout(logoutRequest: LogoutRequest): LiveData<Result<LogoutResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.logout(logoutRequest)
            if (response.isSuccessful) {
                withContext(Dispatchers.IO) {
                    Log.e("Logout", "Token cleared")
                    userPreferences.apply {
                        clearToken()
                        Log.e("Logout", "Token cleared successfully")
                        clearUserId()
                        Log.e("Logout", "User ID cleared successfully")
                        clearIdtansactionincome()
                        Log.e("Logout", "Transaction ID cleared successfully")
                        clearIdtransactionexpenditure()
                        clearUsername()
                    }
                }
                emit(Result.Success(response.body()!!))
            } else {
                emit(Result.Error("Logout unsuccessful"))
            }
        } catch (e: HttpException) {
            emit(Result.Error(e.message ?: "Unknown error"))
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Unexpected error occurred"))
        }
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null

        fun getInstance(
            apiService: ApiService,
            userPreferences: UserPreferences
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(apiService , userPreferences)
            }.also { instance = it }
    }
}
