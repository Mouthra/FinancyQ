package com.example.financyq.data.response

import com.google.gson.annotations.SerializedName

data class TotalResponse(

	@field:SerializedName("data")
	val data: Data? = null,

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class Data(

	@field:SerializedName("idUser")
	val idUser: String? = null,

	@field:SerializedName("total")
	val total: Int? = null,

	@field:SerializedName("type")
	val type: String? = null
)
