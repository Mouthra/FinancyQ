package com.example.financyq.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.financyq.data.response.EduFinanceResponse
import com.example.financyq.databinding.ItemListHomeBinding
import com.example.financyq.ui.edufinance.DetailEduFinanceActivity

class ShortcutAdapter : ListAdapter<EduFinanceResponse, ShortcutAdapter.EduFinanceViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EduFinanceViewHolder {
        val binding = ItemListHomeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EduFinanceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EduFinanceViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    class EduFinanceViewHolder(private val binding: ItemListHomeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: EduFinanceResponse) {
            binding.tvTitle.text = item.content
            Glide.with(binding.root)
                .load(item.imageUrl)
                .into(binding.imgEducation)
                .clearOnDetach()

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, DetailEduFinanceActivity::class.java).apply {
                    putExtra(DetailEduFinanceActivity.EXTRA_TITLE, item.title)
                    putExtra(DetailEduFinanceActivity.EXTRA_IMAGE_URL, item.imageUrl)
                    putExtra(DetailEduFinanceActivity.EXTRA_DESCRIPTION, item.description)
                    putExtra(DetailEduFinanceActivity.EXTRA_CREATED_AT, item.createdAt)
                    putExtra(DetailEduFinanceActivity.EXTRA_SOURCE, item.source)
                    putExtra(DetailEduFinanceActivity.EXTRA_CONTENT, item.content)
                }
                itemView.context.startActivity(intent)
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<EduFinanceResponse>() {
            override fun areItemsTheSame(oldItem: EduFinanceResponse, newItem: EduFinanceResponse): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: EduFinanceResponse, newItem: EduFinanceResponse): Boolean {
                return oldItem == newItem
            }
        }
    }
}
