package com.example.financyq.ui.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.financyq.data.di.Result
import com.example.financyq.data.repo.DetailExpenditureRepository
import com.example.financyq.data.response.DetailResponse

class DetailsExpenditureViewModel(private val detailExpenditureRepository: DetailExpenditureRepository) : ViewModel() {
    fun getDetailExpenditure(idUser: String): LiveData<Result<DetailResponse>> = detailExpenditureRepository.getDetailExpenditure(idUser)
}