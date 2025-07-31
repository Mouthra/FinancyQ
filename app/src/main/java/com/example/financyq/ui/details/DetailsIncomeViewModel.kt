package com.example.financyq.ui.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.financyq.data.di.Result
import com.example.financyq.data.repo.DetailIncomeRepository
import com.example.financyq.data.response.DetailResponse

class DetailsIncomeViewModel(private val detailIncomeRepository: DetailIncomeRepository) : ViewModel() {
    fun getDetailIncome(idUser: String): LiveData<Result<DetailResponse>> = detailIncomeRepository.getDetailIncome(idUser)
}
