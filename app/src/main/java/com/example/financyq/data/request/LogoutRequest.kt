package com.example.financyq.data.request

import com.google.gson.annotations.SerializedName

data class LogoutRequest(
    @field:SerializedName("token")
    val token: String
)
