package com.example.financyq.ui.analizeq

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.financyq.data.di.Result
import com.example.financyq.data.repo.TotalExpenditureRepository
import com.example.financyq.data.response.TotalResponse

class TotalExpenditureViewModel(private val totalExpenditureRepository: TotalExpenditureRepository) : ViewModel() {

    fun getTotalExpenditure(idUser: String): LiveData<Result<TotalResponse>> {
        return totalExpenditureRepository.getTotalExpenditure(idUser)
    }
}
