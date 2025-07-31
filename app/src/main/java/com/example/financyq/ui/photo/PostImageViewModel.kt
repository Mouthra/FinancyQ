package com.example.financyq.ui.photo

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.financyq.data.repo.PostImageRepository
import com.example.financyq.data.di.Result
import com.example.financyq.data.response.PostImageResponse
import okhttp3.MultipartBody

class PostImageViewModel(private val repository: PostImageRepository) : ViewModel() {

    fun postImage(
        image: MultipartBody.Part
    ): LiveData<Result<PostImageResponse>> {
        return repository.postImage(image)
    }
}
