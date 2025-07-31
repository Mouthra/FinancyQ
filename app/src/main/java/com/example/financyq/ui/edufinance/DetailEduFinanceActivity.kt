package com.example.financyq.ui.edufinance

import android.os.Bundle
import android.text.util.Linkify
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.financyq.databinding.ActivityDetailEduFinanceBinding

class DetailEduFinanceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailEduFinanceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailEduFinanceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val title = intent.getStringExtra(EXTRA_TITLE)
        val imageUrl = intent.getStringExtra(EXTRA_IMAGE_URL)
        val description = intent.getStringExtra(EXTRA_DESCRIPTION)
        val createdAt = intent.getStringExtra(EXTRA_CREATED_AT)
        val source = intent.getStringExtra(EXTRA_SOURCE)
        val content = intent.getStringExtra(EXTRA_CONTENT)

        Glide.with(this)
            .load(imageUrl)
            .into(binding.imgEducation)

        binding.apply {
            tvContent.text = content
            tvSource.text = source
            tvCreatedAt.text = createdAt
            tvTitle.text = title
            tvDescription.text = description
        }

        Linkify.addLinks(binding.tvSource, Linkify.WEB_URLS)
    }

    companion object {
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_IMAGE_URL = "extra_image_url"
        const val EXTRA_DESCRIPTION = "extra_description"
        const val EXTRA_CREATED_AT = "extra_created_at"
        const val EXTRA_SOURCE = "extra_source"
        const val EXTRA_CONTENT = "extra_content"
    }
}
