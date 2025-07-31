package com.example.financyq.data.di

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
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
import com.example.financyq.ui.analizeq.ExportPdfViewModel
import com.example.financyq.ui.analizeq.TotalExpenditureViewModel
import com.example.financyq.ui.analizeq.TotalIncomeViewModel
import com.example.financyq.ui.details.DeleteExpenditureViewModel
import com.example.financyq.ui.details.DeleteIncomeViewModel
import com.example.financyq.ui.details.DetailsExpenditureViewModel
import com.example.financyq.ui.details.DetailsIncomeViewModel
import com.example.financyq.ui.details.UpdateExpenditureViewModel
import com.example.financyq.ui.details.UpdateIncomeViewModel
import com.example.financyq.ui.edufinance.EduFinanceViewModel
import com.example.financyq.ui.login.LoginViewModel
import com.example.financyq.ui.manual.AddExpenditureViewModel
import com.example.financyq.ui.manual.AddIncomeViewModel
import com.example.financyq.ui.otp.OtpViewModel
import com.example.financyq.ui.photo.PostImageViewModel
import com.example.financyq.ui.profile.LogoutViewModel
import com.example.financyq.ui.profile.UpdateProfileViewModel
import com.example.financyq.ui.profile.UsernameViewModel
import com.example.financyq.ui.signup.SignUpViewModel

class ViewModelFactory(
    private val eduFinanceRepository: EduFinanceRepository,
    private val userRepository: UserRepository,
    private val detailIncomeRepository: DetailIncomeRepository,
    private val detailExpenditureRepository: DetailExpenditureRepository,
    private val addIncomeRepository: AddIncomeRepository,
    private val addExpenditureRepository: AddExpenditureRepository,
    private val updateIncomeRepository: UpdateIncomeRepository,
    private val updateExpenditureRepository: UpdateExpenditureRepository,
    private val deleteIncomeRepository: DeleteIncomeRepository,
    private val deleteExpenditureRepository: DeleteExpenditureRepository,
    private val totalIncomeRepository: TotalIncomeRepository,
    private val totalExpenditureRepository: TotalExpenditureRepository,
    private val usernameRepository: UsernameRepository,
    private val exportPdfRepository: ExportPdfRepository,
    private val postImageRepository: PostImageRepository,
    private val updateProfileRepository: UpdateProfileRepository
): ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(EduFinanceViewModel::class.java) -> {
                EduFinanceViewModel(eduFinanceRepository) as T
            }
            modelClass.isAssignableFrom(SignUpViewModel::class.java) -> {
                SignUpViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(OtpViewModel::class.java) -> {
                OtpViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(LogoutViewModel::class.java) -> {
                LogoutViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(DetailsIncomeViewModel::class.java) -> {
                DetailsIncomeViewModel(detailIncomeRepository) as T
            }
            modelClass.isAssignableFrom(DetailsExpenditureViewModel::class.java) -> {
                DetailsExpenditureViewModel(detailExpenditureRepository) as T
            }
            modelClass.isAssignableFrom(AddIncomeViewModel::class.java) -> {
                AddIncomeViewModel(addIncomeRepository) as T
            }
            modelClass.isAssignableFrom(AddExpenditureViewModel::class.java) -> {
                AddExpenditureViewModel(addExpenditureRepository) as T
            }
            modelClass.isAssignableFrom(UpdateIncomeViewModel::class.java) -> {
                UpdateIncomeViewModel(updateIncomeRepository) as T
            }
            modelClass.isAssignableFrom(UpdateExpenditureViewModel::class.java) -> {
                UpdateExpenditureViewModel(updateExpenditureRepository) as T
            }
            modelClass.isAssignableFrom(DeleteIncomeViewModel::class.java) -> {
                DeleteIncomeViewModel(deleteIncomeRepository) as T
            }
            modelClass.isAssignableFrom(DeleteExpenditureViewModel::class.java) -> {
                DeleteExpenditureViewModel(deleteExpenditureRepository) as T
            }
            modelClass.isAssignableFrom(TotalIncomeViewModel::class.java) -> {
                TotalIncomeViewModel(totalIncomeRepository) as T
            }
            modelClass.isAssignableFrom(TotalExpenditureViewModel::class.java) -> {
                TotalExpenditureViewModel(totalExpenditureRepository) as T
            }
            modelClass.isAssignableFrom(ExportPdfViewModel::class.java) -> {
                ExportPdfViewModel(exportPdfRepository) as T
            }
            modelClass.isAssignableFrom(UsernameViewModel::class.java) -> {
                UsernameViewModel(usernameRepository) as T
            }
            modelClass.isAssignableFrom(PostImageViewModel::class.java) -> {
                PostImageViewModel(postImageRepository) as T
            }
            modelClass.isAssignableFrom(UpdateProfileViewModel::class.java) -> {
                UpdateProfileViewModel(updateProfileRepository) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        @JvmStatic
        fun getInstance(context: Context): ViewModelFactory {
            val eduFinanceRepository = Injection.provideEduFinanceRepository(context)
            val userRepository = Injection.provideUserRepository(context)
            val detailIncomeRepository = Injection.provideDetailIncomeRepository(context)
            val detailExpenditureRepository = Injection.provideDetailExpenditureRepository(context)
            val addIncomeRepository = Injection.provideAddIncomeRepository(context)
            val addExpenditureRepository = Injection.provideAddExpenditureRepository(context)
            val updateIncomeRepository = Injection.provideUpdateIncomeRepository(context)
            val updateExpenditureRepository = Injection.provideUpdateExpenditureRepository(context)
            val deleteIncomeRepository = Injection.provideDeleteIncomeRepository(context)
            val deleteExpenditureRepository = Injection.provideDeleteExpenditureRepository(context)
            val totalIncomeRepository = Injection.provideTotalIncomeRepository(context)
            val totalExpenditureRepository = Injection.provideTotalExpenditureRepository(context)
            val exportPdfRepository = Injection.provideExportPdfRepository(context)
            val usernameRepository = Injection.provideUsernameRepository(context)
            val postImageRepository = Injection.providePostImageRepository(context)
            val updateProfileRepository = Injection.provideUpdateProfileRepository(context)

            return INSTANCE ?: synchronized(ViewModelFactory::class.java) {
                INSTANCE ?: ViewModelFactory(
                    eduFinanceRepository,
                    userRepository,
                    detailIncomeRepository,
                    detailExpenditureRepository,
                    addIncomeRepository,
                    addExpenditureRepository,
                    updateIncomeRepository,
                    updateExpenditureRepository,
                    deleteIncomeRepository,
                    deleteExpenditureRepository,
                    totalIncomeRepository,
                    totalExpenditureRepository,
                    usernameRepository,
                    exportPdfRepository,
                    postImageRepository,
                    updateProfileRepository)
                    .also { INSTANCE = it }
            }
        }
    }
}