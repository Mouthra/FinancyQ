package com.example.financyq.ui.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.financyq.data.di.Result
import com.example.financyq.data.repo.DeleteIncomeRepository
import com.example.financyq.data.response.DeleteResponse

class DeleteIncomeViewModel(private val deleteIncomeRepository: DeleteIncomeRepository) : ViewModel() {
    fun deleteIncome(idTransaksi: String): LiveData<Result<DeleteResponse>> {
        return deleteIncomeRepository.deleteIncome(idTransaksi)
    }
}
