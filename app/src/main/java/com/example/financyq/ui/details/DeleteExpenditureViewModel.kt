package com.example.financyq.ui.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.financyq.data.di.Result
import com.example.financyq.data.repo.DeleteExpenditureRepository
import com.example.financyq.data.response.DeleteResponse

class DeleteExpenditureViewModel(private val deleteExpenditureRepository: DeleteExpenditureRepository) : ViewModel() {
    fun deleteExpenditure(idTransaksi: String): LiveData<Result<DeleteResponse>> {
        return deleteExpenditureRepository.deleteExpenditure(idTransaksi)
    }
}
