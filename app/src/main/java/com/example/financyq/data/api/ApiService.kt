package com.example.financyq.data.api

import com.example.financyq.data.request.AddExpenditureRequest
import com.example.financyq.data.request.AddIncomeRequest
import com.example.financyq.data.request.LoginRequest
import com.example.financyq.data.request.LogoutRequest
import com.example.financyq.data.request.OtpRequest
import com.example.financyq.data.request.SignupRequest
import com.example.financyq.data.request.UpdateExpenditureRequest
import com.example.financyq.data.request.UpdateIncomeRequest
import com.example.financyq.data.request.UpdatePasswordRequest
import com.example.financyq.data.request.UpdateUsernameRequest
import com.example.financyq.data.response.AddExpenditureResponse
import com.example.financyq.data.response.AddIncomeResponse
import com.example.financyq.data.response.DeleteResponse
import com.example.financyq.data.response.DetailResponse
import com.example.financyq.data.response.EduFinanceResponse
import com.example.financyq.data.response.GenericMessageResponse
import com.example.financyq.data.response.LoginResponse
import com.example.financyq.data.response.LogoutResponse
import com.example.financyq.data.response.OtpResponse
import com.example.financyq.data.response.SignUpResponse
import com.example.financyq.data.response.TotalResponse
import com.example.financyq.data.response.UpdateExpenditureResponse
import com.example.financyq.data.response.UpdateIncomeResponse
import com.example.financyq.data.response.UsernameResponse
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Streaming

interface ApiService {
    @GET("education-content")
    suspend fun getEducationFinance(
    ): Response<List<EduFinanceResponse>>

    @POST("signup")
    suspend fun register(
        @Body signupRequest : SignupRequest
    ): Response<SignUpResponse>

    @POST("verifyotp")
    suspend fun verifyOtp(
        @Body otpRequest: OtpRequest
    ): Response<OtpResponse>

    @POST("login")
    suspend fun login(
        @Body loginRequest: LoginRequest
    ): Response<LoginResponse>

    @POST("logout")
    suspend fun logout(
        @Body logoutRequest: LogoutRequest
    ): Response<LogoutResponse>

    @GET("api/transactions/pemasukan/{idUser}")
    suspend fun getDetailIncome(
        @Path("idUser") idUser: String
    ): Response<DetailResponse>

    @GET("api/transactions/pengeluaran/{idUser}")
    suspend fun getDetailExpenditure(
        @Path("idUser") idUser: String
    ): Response<DetailResponse>

    @POST("api/transactions/pemasukan")
    suspend fun addIncome(
        @Body incomeRequest: AddIncomeRequest
    ): Response<AddIncomeResponse>

    @POST("api/transactions/pengeluaran")
    suspend fun addExpenditure(
        @Body expenditureRequest: AddExpenditureRequest
    ): Response<AddExpenditureResponse>

    @PUT("api/transactions/pemasukan/{idTransaksiPemasukan}")
    suspend fun updateIncome(
        @Path("idTransaksiPemasukan") idTransaksiPemasukan: String,
        @Body updateIncomeRequest: UpdateIncomeRequest
    ): Response<UpdateIncomeResponse>

    @PUT("api/transactions/pengeluaran/{idTransaksiPengeluaran}")
    suspend fun updateExpenditure(
        @Path("idTransaksiPengeluaran") idTransaksiPengeluaran: String,
        @Body updateExpenditureRequest: UpdateExpenditureRequest
    ): Response<UpdateExpenditureResponse>

    @DELETE("api/transactions/pemasukan/{idTransaksi}")
    suspend fun deleteIncome(
        @Path("idTransaksi") idTransaksi : String,
    ): Response<DeleteResponse>

    @DELETE("api/transactions/pengeluaran/{idTransaksi}")
    suspend fun deleteExpenditure(
        @Path("idTransaksi") idTransaksi : String,
    ): Response<DeleteResponse>

    @GET("api/transactions/total/pemasukan/{idUser}")
    suspend fun getTotalIncome(
        @Path("idUser") idUser: String,
    ): Response<TotalResponse>

    @GET("api/transactions/total/pengeluaran/{idUser}")
    suspend fun getTotalExpenditure(
        @Path("idUser") idUser: String,
    ): Response<TotalResponse>

    @GET("api/transactions/{idUser}/export-pdf")
    @Streaming
    suspend fun exportPDF(
        @Path("idUser") idUser: String
    ): Response<ResponseBody>

    @GET("/users/{username}")
    suspend fun getUsername(
        @Path("username") username: String,
    ): Response<UsernameResponse>

    @Multipart
    @POST("/api/transactions/ocr")
    suspend fun postImage(
        @Part image: MultipartBody.Part
    ): Response<ResponseBody>

    @PUT("users/{username}")
    suspend fun updateUsername(
        @Path("username") oldUsername: String,
        @Body request: UpdateUsernameRequest
    ): Response<GenericMessageResponse>

    @PUT("users/{username}")
    suspend fun updatePassword(
        @Path("username") username: String,
        @Body request: UpdatePasswordRequest
    ): Response<GenericMessageResponse>

}
