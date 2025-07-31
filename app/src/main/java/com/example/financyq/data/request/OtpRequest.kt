package com.example.financyq.data.request

import com.google.gson.annotations.SerializedName

data class OtpRequest (

    @field:SerializedName("email")
    val email: String,

    @field:SerializedName("otp")
    val otp: String
)
