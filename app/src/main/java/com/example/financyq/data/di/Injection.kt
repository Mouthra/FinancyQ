package com.example.financyq.data.di

import android.content.Context
import com.example.financyq.data.api.ApiConfig
import com.example.financyq.data.local.UserPreferences
import com.example.financyq.data.repo.AddExpenditureRepository
import com.example.financyq.data.repo.AddIncomeRepository
import com.example.financyq.data.repo.DeleteExpenditureRepository
import com.example.financyq.data.repo.DeleteIncomeRepository
import com.example.financyq.data.repo.DetailExpenditureRepository
import com.example.financyq.data.repo.DetailIncomeRepository
import com.example.financyq.data.repo.EduFinanceRepository
import com.example.financyq.data.repo.ExportPdfRepository
import com.example.financyq.data.repo.PostImageRepository
import com.example.financyq.data.repo.TotalExpenditureRepository
import com.example.financyq.data.repo.TotalIncomeRepository
import com.example.financyq.data.repo.UpdateExpenditureRepository
import com.example.financyq.data.repo.UpdateIncomeRepository
import com.example.financyq.data.repo.UpdateProfileRepository
import com.example.financyq.data.repo.UserRepository
import com.example.financyq.data.repo.UsernameRepository

object Injection {
    fun provideEduFinanceRepository(context: Context): EduFinanceRepository{
        val userPreferences =UserPreferences.getInstance(context)
        val tokenFlow = userPreferences.tokenFlow
        val apiService = ApiConfig.getApiService(tokenFlow)
        return EduFinanceRepository.getInstance(apiService)
    }

    fun provideUserRepository(context: Context): UserRepository{
        val userPreferences =UserPreferences.getInstance(context)
        val tokenFlow = userPreferences.tokenFlow
        val apiService = ApiConfig.getApiService(tokenFlow)
        return UserRepository.getInstance(apiService, userPreferences)
    }

    fun provideDetailIncomeRepository(context: Context): DetailIncomeRepository {
        val userPreferences = UserPreferences.getInstance(context)
        val tokenFlow = userPreferences.tokenFlow
        val apiService = ApiConfig.getApiService(tokenFlow)
        return DetailIncomeRepository.getInstance(apiService)
    }

    fun provideDetailExpenditureRepository(context: Context): DetailExpenditureRepository {
        val userPreferences = UserPreferences.getInstance(context)
        val tokenFlow = userPreferences.tokenFlow
        val apiService = ApiConfig.getApiService(tokenFlow)
        return DetailExpenditureRepository.getInstance(apiService)
    }

    fun provideAddIncomeRepository(context: Context): AddIncomeRepository {
        val userPreferences = UserPreferences.getInstance(context)
        val tokenFlow = userPreferences.tokenFlow
        val apiService = ApiConfig.getApiService(tokenFlow)
        return AddIncomeRepository.getInstance(apiService, userPreferences)
    }

    fun provideAddExpenditureRepository(context: Context): AddExpenditureRepository {
        val userPreferences = UserPreferences.getInstance(context)
        val tokenFlow = userPreferences.tokenFlow
        val apiService = ApiConfig.getApiService(tokenFlow)
        return AddExpenditureRepository.getInstance(apiService, userPreferences)
    }

    fun provideUpdateIncomeRepository(context: Context): UpdateIncomeRepository {
        val userPreferences = UserPreferences.getInstance(context)
        val tokenFlow = userPreferences.tokenFlow
        val apiService = ApiConfig.getApiService(tokenFlow)
        return UpdateIncomeRepository.getInstance(apiService)
    }

    fun provideUpdateExpenditureRepository(context: Context): UpdateExpenditureRepository {
        val userPreferences = UserPreferences.getInstance(context)
        val tokenFlow = userPreferences.tokenFlow
        val apiService = ApiConfig.getApiService(tokenFlow)
        return UpdateExpenditureRepository.getInstance(apiService)
    }

    fun provideDeleteIncomeRepository(context: Context): DeleteIncomeRepository {
        val userPreferences = UserPreferences.getInstance(context)
        val tokenFlow = userPreferences.tokenFlow
        val apiService = ApiConfig.getApiService(tokenFlow)
        return DeleteIncomeRepository.getInstance(apiService)
    }

    fun provideDeleteExpenditureRepository(context: Context): DeleteExpenditureRepository {
        val userPreferences = UserPreferences.getInstance(context)
        val tokenFlow = userPreferences.tokenFlow
        val apiService = ApiConfig.getApiService(tokenFlow)
        return DeleteExpenditureRepository.getInstance(apiService)
    }

    fun provideTotalIncomeRepository(context: Context): TotalIncomeRepository {
        val userPreferences = UserPreferences.getInstance(context)
        val tokenFlow = userPreferences.tokenFlow
        val apiService = ApiConfig.getApiService(tokenFlow)
        return TotalIncomeRepository.getInstance(apiService)
    }

    fun provideTotalExpenditureRepository(context: Context): TotalExpenditureRepository {
        val userPreferences = UserPreferences.getInstance(context)
        val tokenFlow = userPreferences.tokenFlow
        val apiService = ApiConfig.getApiService(tokenFlow)
        return TotalExpenditureRepository.getInstance(apiService)
    }

    fun provideExportPdfRepository(context: Context): ExportPdfRepository {
        val userPreferences = UserPreferences.getInstance(context)
        val tokenFlow = userPreferences.tokenFlow
        val apiService = ApiConfig.getApiService(tokenFlow)
        return ExportPdfRepository.getInstance(apiService)
    }

    fun provideUsernameRepository(context: Context): UsernameRepository {
        val userPreferences = UserPreferences.getInstance(context)
        val tokenFlow = userPreferences.tokenFlow
        val apiService = ApiConfig.getApiService(tokenFlow)
        return UsernameRepository.getInstance(apiService)
    }

    fun providePostImageRepository(context: Context): PostImageRepository {
        val userPreferences = UserPreferences.getInstance(context)
        val tokenFlow = userPreferences.tokenFlow
        val apiService = ApiConfig.getApiService(tokenFlow)
        return PostImageRepository.getInstance(apiService)
    }

    fun provideUpdateProfileRepository(context: Context): UpdateProfileRepository {
        val userPreferences = UserPreferences.getInstance(context)
        val tokenFlow = userPreferences.tokenFlow
        val apiService = ApiConfig.getApiService(tokenFlow)
        return UpdateProfileRepository.getInstance(apiService)
    }

//    fun provideOcrRepository(context: Context): OcrRepository {
//        val ocrService = ApiConfig.getOcrService()
//        return OcrRepository.getInstance(ocrService)
//    }
}