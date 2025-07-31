package com.example.financyq.ui.manual

import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import com.example.financyq.data.repo.AddIncomeRepository
import com.example.financyq.data.di.Result
import com.example.financyq.data.request.AddIncomeRequest
import com.example.financyq.data.response.AddIncomeResponse

class AddIncomeViewModel(private val addIncomeRepository: AddIncomeRepository) : ViewModel() {

    fun addIncome(addIncomeRequest: AddIncomeRequest): LiveData<Result<AddIncomeResponse>> {
        return addIncomeRepository.addIncome(addIncomeRequest)
    }
}
