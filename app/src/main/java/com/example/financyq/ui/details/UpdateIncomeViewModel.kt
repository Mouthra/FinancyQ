package com.example.financyq.ui.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.financyq.data.di.Result
import com.example.financyq.data.repo.UpdateIncomeRepository
import com.example.financyq.data.request.UpdateIncomeRequest
import com.example.financyq.data.response.UpdateIncomeResponse

class UpdateIncomeViewModel(private val updateIncomeRepository: UpdateIncomeRepository) : ViewModel() {
    fun updateIncome(idTransaksiPemasukan: String, updateIncomeRequest: UpdateIncomeRequest): LiveData<Result<UpdateIncomeResponse>> {
        return updateIncomeRepository.updateIncome(idTransaksiPemasukan, updateIncomeRequest)
    }
}