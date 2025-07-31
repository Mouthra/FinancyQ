package com.example.financyq.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.financyq.R
import com.example.financyq.data.api.ApiConfig
import com.example.financyq.data.di.Result
import com.example.financyq.data.di.ViewModelFactory
import com.example.financyq.data.local.UserPreferences
import com.example.financyq.data.repo.UserRepository
import com.example.financyq.data.request.LogoutRequest
import com.example.financyq.databinding.FragmentProfileBinding
import com.example.financyq.ui.about.AboutFinancyQActivity
import com.example.financyq.ui.about.AboutUsActivity
import com.example.financyq.ui.about.PrivacyPolicyActivity
import com.example.financyq.ui.welcome.WelcomeActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var userPreferences: UserPreferences
    private lateinit var userRepository: UserRepository

    private val logoutViewModel: LogoutViewModel by viewModels {
        ViewModelFactory.getInstance(requireActivity())
    }

    private val usernameViewModel: UsernameViewModel by viewModels {
        ViewModelFactory.getInstance(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userPreferences = UserPreferences.getInstance(requireContext())
        val apiService = ApiConfig.getApiService(userPreferences.tokenFlow)
        userRepository = UserRepository.getInstance(apiService, userPreferences)

        setupAction()
        loadUser()
    }

    private fun loadUser() {
        // 1. Ambil stored username key
        val usernameKey = runBlocking { userPreferences.userNameFlow.first() }
        if (usernameKey.isNullOrBlank()) {
            Toast.makeText(requireContext(),
                R.string.id_user_not_found,
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        // 2. Awal: tampilkan loader, sembunyikan TextView
        binding.loadingUsername.visibility = View.VISIBLE
        binding.getUsername.visibility    = View.GONE

        // 3. Observe ViewModel
        usernameViewModel.getUsername(usernameKey)
            .observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Result.Loading -> {
                        // biarkan animasi LoadingWavy berjalan
                    }
                    is Result.Success -> {
                        // hide loader, tampilkan username
                        binding.loadingUsername.visibility = View.GONE
                        binding.getUsername.apply {
                            text       = result.data.username
                            visibility = View.VISIBLE
                        }
                    }
                    is Result.Error -> {
                        // hide loader, error feedback
                        binding.loadingUsername.visibility = View.GONE
                        Toast.makeText(
                            requireContext(),
                            result.error,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
    }


    private fun setupAction() {
        binding.apply {
            btnAboutFinancyq.setOnClickListener {
                startActivity(Intent(requireContext(), AboutFinancyQActivity::class.java))
            }
            btnPrivacyPolicy.setOnClickListener {
                startActivity(Intent(requireContext(), PrivacyPolicyActivity::class.java))
            }
            btnLogout.setOnClickListener {
                showLogoutConfirmationDialog()
            }
            cardView.setOnClickListener {
                startActivity(Intent(requireContext(), UserActivity::class.java))
            }
        }
    }

    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.confirm_logout)
            .setMessage(R.string.message_logout)
            .setPositiveButton(R.string.yes) { dialog, _ ->
                logoutUser()
                dialog.dismiss()
            }
            .setNegativeButton(R.string.No) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun logoutUser() {
        val token = runBlocking { userPreferences.tokenFlow.first() }
        if (token != null) {
            val logoutRequest = LogoutRequest(token)
            logoutViewModel.logout(logoutRequest).observe(requireActivity()) { result ->
                when (result) {
                    is Result.Loading -> {
                        // Show loading indicator if needed
                    }
                    is Result.Success -> {
                        lifecycleScope.launch(Dispatchers.IO) {
                            userPreferences.clearToken()
                            userPreferences.clearUserId()
                            userPreferences.clearIdtransactionexpenditure()
                            userPreferences.clearIdtansactionincome()
                            userPreferences.clearUsername()

                            withContext(Dispatchers.Main) {
                                Toast.makeText(requireActivity(), result.data.message, Toast.LENGTH_SHORT).show()
                                navigateToWelcomeScreen()
                            }
                        }
                    }
                    is Result.Error -> {
                        Toast.makeText(requireActivity(), result.error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            Toast.makeText(requireActivity(), "Token is null", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToWelcomeScreen() {
        val intent = Intent(requireContext(), WelcomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
