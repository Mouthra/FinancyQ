package com.example.financyq.ui.edufinance

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.financyq.data.di.Result
import com.example.financyq.data.repo.EduFinanceRepository
import com.example.financyq.data.response.EduFinanceResponse

class EduFinanceViewModel(private val eduFinanceRepository: EduFinanceRepository) : ViewModel() {

    fun getEducationFinance(): LiveData<Result<List<EduFinanceResponse>>> = eduFinanceRepository.getEducationFinance()
}