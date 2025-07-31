package com.example.financyq.ui.analizeq

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.financyq.data.di.Result
import com.example.financyq.data.repo.ExportPdfRepository
import okhttp3.ResponseBody

class ExportPdfViewModel(private val exportPdfRepository: ExportPdfRepository) : ViewModel() {
    fun exportPdf(idUser: String): LiveData<Result<ResponseBody>> {
        return exportPdfRepository.exportPDF(idUser)
    }
}
