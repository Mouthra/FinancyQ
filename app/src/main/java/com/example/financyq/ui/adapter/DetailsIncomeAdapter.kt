package com.example.financyq.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.financyq.data.response.TransactionsItem
import com.example.financyq.databinding.ItemListDetailsincomeBinding
import java.text.NumberFormat
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

class DetailsIncomeAdapter(
    private val onItemClick: (TransactionsItem) -> Unit
) : ListAdapter<TransactionsItem, DetailsIncomeAdapter.DetailIncomeViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailIncomeViewHolder {
        val binding = ItemListDetailsincomeBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return DetailIncomeViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: DetailIncomeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DetailIncomeViewHolder(
        private val binding: ItemListDetailsincomeBinding,
        private val onItemClick: (TransactionsItem) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: TransactionsItem) {
            // Hanya ambil 10 karakter pertama: "YYYY-MM-DD"
            binding.tvTanggal.text = formatDateTime(item.tanggal)
            binding.tvKategori.text = item.kategori
            binding.tvDeskripsi.text = item.deskripsi
            binding.tvJumlah.text = formatToRupiah(item.jumlah ?: 0)
            binding.tvSumber.text = item.sumber

            itemView.setOnClickListener {
                onItemClick(item)
            }
        }

        private fun formatDateTime(dateTime: String?): String {
            // kalau null atau blank langsung kembalikan empty
            if (dateTime.isNullOrBlank()) return ""

            return runCatching {
                // 1) Parse ISO-8601 lengkap
                val odt = OffsetDateTime.parse(dateTime)
                // 2) Konversi ke zona lokal perangkat
                val zoned = odt.atZoneSameInstant(ZoneId.systemDefault())
                // 3) Format dengan detik
                zoned.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            }.getOrElse { _ ->
                // fallback: ambil 19 char dari `str`, ganti 'T' -> ' '
                dateTime.take(19).replace('T', ' ')
            }
        }

        private fun formatToRupiah(value: Int): String {
            val localeID = Locale("in", "ID")
            val numberFormat = NumberFormat.getCurrencyInstance(localeID)
            return numberFormat.format(value)
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<TransactionsItem>() {
            override fun areItemsTheSame(oldItem: TransactionsItem, newItem: TransactionsItem) =
                oldItem == newItem

            override fun areContentsTheSame(oldItem: TransactionsItem, newItem: TransactionsItem) =
                oldItem == newItem
        }
    }
}
