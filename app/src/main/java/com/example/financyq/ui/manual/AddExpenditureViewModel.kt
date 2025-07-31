package com.example.financyq.ui.manual

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.financyq.data.di.Result
import com.example.financyq.data.repo.AddExpenditureRepository
import com.example.financyq.data.request.AddExpenditureRequest
import com.example.financyq.data.response.AddExpenditureResponse

class AddExpenditureViewModel(private val addExpenditureRepository: AddExpenditureRepository): ViewModel(){

    fun addExpenditure(addExpenditureRequest: AddExpenditureRequest): LiveData<Result<AddExpenditureResponse>> {
        return addExpenditureRepository.addExpenditure(addExpenditureRequest)
    }
}