package com.example.financyq.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.financyq.data.response.EduFinanceResponse
import com.example.financyq.databinding.ItemListEdufinanceBinding
import com.example.financyq.ui.edufinance.DetailEduFinanceActivity

class EduFinanceAdapter : RecyclerView.Adapter<EduFinanceAdapter.EduFinanceViewHolder>() {

    private val items = ArrayList<EduFinanceResponse>()

    fun submitList(newItems: List<EduFinanceResponse>) {
        items.clear()
        items.addAll(newItems)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EduFinanceViewHolder {
        val binding = ItemListEdufinanceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EduFinanceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EduFinanceViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class EduFinanceViewHolder(private val binding: ItemListEdufinanceBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: EduFinanceResponse) {
            binding.tvTitle.text = item.title
            binding.tvContent.text = item.content
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
}
