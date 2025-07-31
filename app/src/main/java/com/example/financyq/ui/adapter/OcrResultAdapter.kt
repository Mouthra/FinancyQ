package com.example.financyq.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.financyq.data.response.ReceiptItem
import com.example.financyq.databinding.ItemListOcrBinding
import java.text.NumberFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

class OcrResultAdapter(
    private val onItemClick: (item: ReceiptItem, position: Int) -> Unit
) : ListAdapter<ReceiptItem, OcrResultAdapter.OcrViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OcrViewHolder {
        val binding = ItemListOcrBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return OcrViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OcrViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
        holder.itemView.setOnClickListener {
            onItemClick(item, position)
        }
    }

    class OcrViewHolder(
        private val binding: ItemListOcrBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ReceiptItem) {
            // Format tanggal ISO dengan T/Z menjadi lokal tanpa T/Z
            binding.tvOcrTanggal.text = formatDateTimeWithNow(item.tanggal)
            binding.tvOcrKategori.text = item.kategori ?: ""
            binding.tvOcrNama.text = item.namaPengeluaran ?: ""
            val total = item.totalPengeluaran ?: 0
            binding.tvOcrTotal.text = formatToRupiah(total)
            binding.tvOcrSumber.text = item.sumber ?: ""
        }

        private fun formatDateTimeWithNow(dateOnly: String?): String {
            if (dateOnly.isNullOrBlank()) return ""
            return runCatching {
                val localDate = LocalDate.parse(dateOnly, DateTimeFormatter.ISO_DATE)
                val now       = LocalTime.now(ZoneId.systemDefault())
                val ldt       = LocalDateTime.of(localDate, now)
                ldt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            }.getOrDefault(dateOnly) // fallback hanya tanggal
        }

        private fun formatToRupiah(value: Int): String {
            val localeID = Locale("in", "ID")
            val nf = NumberFormat.getCurrencyInstance(localeID)
            return nf.format(value)
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ReceiptItem>() {
            override fun areItemsTheSame(oldItem: ReceiptItem, newItem: ReceiptItem): Boolean {
                return oldItem.tanggal == newItem.tanggal &&
                        oldItem.namaPengeluaran == newItem.namaPengeluaran
            }

            override fun areContentsTheSame(oldItem: ReceiptItem, newItem: ReceiptItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}
