package com.example.financyq.ui.analizeq

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.financyq.data.di.Result
import com.example.financyq.data.repo.TotalIncomeRepository
import com.example.financyq.data.response.TotalResponse

class TotalIncomeViewModel(private val totalIncomeRepository: TotalIncomeRepository) : ViewModel() {

    fun getTotalIncome(idUser: String): LiveData<Result<TotalResponse>> {
        return totalIncomeRepository.getTotalIncome(idUser)
    }
}