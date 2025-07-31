package com.example.financyq.data.response

import com.google.gson.annotations.SerializedName

data class SignUpResponse(

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)
