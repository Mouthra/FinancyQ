package com.example.financyq.data.response

import com.google.gson.annotations.SerializedName

data class UpdateExpenditureResponse(

	@field:SerializedName("idUser")
	val idUser: String? = null,

	@field:SerializedName("jumlah")
	val jumlah: Int? = null,

	@field:SerializedName("sumber")
	val sumber: String? = null,

	@field:SerializedName("lampiran")
	val lampiran: String? = null,

	@field:SerializedName("kategori")
	val kategori: String? = null,

	@field:SerializedName("deskripsi")
	val deskripsi: String? = null,

	@field:SerializedName("tanggal")
	val tanggal: String? = null,

	@field:SerializedName("idTransaksiPengeluaran")
	val idTransaksiPengeluaran: String? = null
)
