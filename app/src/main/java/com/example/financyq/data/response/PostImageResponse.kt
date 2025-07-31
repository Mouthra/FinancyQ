package com.example.financyq.data.response

import com.google.gson.annotations.SerializedName

data class PostImageResponse(
	@field:SerializedName("items")
	val items: MutableList<ReceiptItem> = mutableListOf()
)

data class ReceiptItem(
	@field:SerializedName("tanggal")
    var tanggal: String? = null,

	@field:SerializedName("kategori")
	var kategori: String? = null,

	@field:SerializedName("nama_pengeluaran")
    var namaPengeluaran: String? = null,

	@field:SerializedName("total_pengeluaran")
	var totalPengeluaran: Int? = null,

	@field:SerializedName("sumber")
	var sumber: String? = null
)
