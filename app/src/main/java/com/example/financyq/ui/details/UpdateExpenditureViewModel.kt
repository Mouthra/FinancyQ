package com.example.financyq.ui.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.financyq.data.di.Result
import com.example.financyq.data.repo.UpdateExpenditureRepository
import com.example.financyq.data.request.UpdateExpenditureRequest
import com.example.financyq.data.response.UpdateExpenditureResponse

class UpdateExpenditureViewModel(private val updateExpenditureRepository: UpdateExpenditureRepository) : ViewModel() {
    fun updateExpenditure(idTransaksiPengeluaran: String, updateExpenditureRequest: UpdateExpenditureRequest): LiveData<Result<UpdateExpenditureResponse>> {
        return updateExpenditureRepository.updateExpenditure(idTransaksiPengeluaran, updateExpenditureRequest)
    }
}