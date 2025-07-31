package com.example.financyq.data.response

import com.google.gson.annotations.SerializedName

data class DetailResponse(

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("transactions")
	val transactions: List<TransactionsItem?>? = null
)

data class TransactionsItem(

	@field:SerializedName("jumlah")
	val jumlah: Int? = null,

	@field:SerializedName("sumber")
	val sumber: String? = null,

	@field:SerializedName("idTransaksi")
	val idTransaksi: String? = null,

	@field:SerializedName("kategori")
	val kategori: String? = null,

	@field:SerializedName("deskripsi")
	val deskripsi: String? = null,

	@field:SerializedName("tanggal")
	val tanggal: String? = null
)
