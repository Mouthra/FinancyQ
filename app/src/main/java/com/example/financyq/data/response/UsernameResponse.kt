package com.example.financyq.data.response

import com.google.gson.annotations.SerializedName

data class UsernameResponse(

	@field:SerializedName("password")
	val password: String? = null,

	@field:SerializedName("userId")
	val userId: String? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("username")
	val username: String? = null,

	@field:SerializedName("refreshToken")
	val refreshToken: String? = null
)
