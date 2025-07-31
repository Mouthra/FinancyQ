package com.example.financyq.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.financyq.data.response.TransactionsItem
import com.example.financyq.databinding.ItemListDetailsexpenditureBinding
import java.text.NumberFormat
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class DetailsExpenditureAdapter(private val onItemClick: (TransactionsItem) -> Unit) : ListAdapter<TransactionsItem, DetailsExpenditureAdapter.DetailExpenditureViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailExpenditureViewHolder {
        val binding = ItemListDetailsexpenditureBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DetailExpenditureViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: DetailExpenditureViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    class DetailExpenditureViewHolder(private val binding: ItemListDetailsexpenditureBinding, private val onItemClick: (TransactionsItem) -> Unit) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: TransactionsItem) {
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
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<TransactionsItem>() {
            override fun areItemsTheSame(oldItem: TransactionsItem, newItem: TransactionsItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: TransactionsItem, newItem: TransactionsItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}
