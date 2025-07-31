package com.example.financyq.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.financyq.R
import com.example.financyq.data.di.Result
import com.example.financyq.data.di.ViewModelFactory
import com.example.financyq.data.local.UserPreferences
import com.example.financyq.data.response.TotalResponse
import com.example.financyq.databinding.FragmentHomeBinding
import com.example.financyq.ui.adapter.ShortcutAdapter
import com.example.financyq.ui.analizeq.TotalExpenditureViewModel
import com.example.financyq.ui.analizeq.TotalIncomeViewModel
import com.example.financyq.ui.details.DetailsExpenditureActivity
import com.example.financyq.ui.details.DetailsIncomeActivity
import com.example.financyq.ui.edufinance.EduFinanceViewModel
import com.example.financyq.ui.profile.UsernameViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.text.NumberFormat
import java.util.Locale

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val eduFinanceViewModel: EduFinanceViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }
    private val totalIncomeViewModel: TotalIncomeViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }
    private val totalExpenditureViewModel: TotalExpenditureViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }
    private val usernameViewModel: UsernameViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    private lateinit var shortcutAdapter: ShortcutAdapter
    private lateinit var userPreferences: UserPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userPreferences = UserPreferences.getInstance(requireContext())

        setupRecyclerView()
        setupAction()
        initialLoad()

        binding.tvValueIncome.visibility      = View.GONE
        binding.tvValueExpenditure.visibility = View.GONE
        binding.swipeRefresh.setOnRefreshListener {
            refreshData()
        }
    }

    private fun initialLoad() {
        // Show ProgressBar for initial load
        binding.progressBar.visibility = View.VISIBLE

        // Load user info and data
        loadUser()
        observeTotalIncome()
        observeTotalExpenditure()
        setObserver()
    }

    private fun refreshData() {
        // Show SwipeRefreshLayout spinner
        binding.swipeRefresh.isRefreshing = true

        // Reload all data
        loadUser()
        observeTotalIncome()
        observeTotalExpenditure()
        setObserver()
    }

    private fun loadUser() {
        val usernameKey = runBlocking { userPreferences.userNameFlow.first() }
        if (usernameKey.isNullOrBlank()) {
            Toast.makeText(requireContext(),
                R.string.id_user_not_found,
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        // show loader, hide nama
        binding.loadingUsername.visibility = View.VISIBLE
        binding.tvUsername.visibility    = View.GONE

        usernameViewModel.getUsername(usernameKey)
            .observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Result.Loading -> {
                        // LoadingWavy auto-play
                    }
                    is Result.Success -> {
                        binding.loadingUsername.visibility = View.GONE
                        binding.tvUsername.apply {
                            text       = result.data.username
                            visibility = View.VISIBLE
                        }
                        binding.swipeRefresh.isRefreshing = false
                    }
                    is Result.Error -> {
                        binding.loadingUsername.visibility = View.GONE
                        Toast.makeText(
                            requireContext(),
                            result.error,
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.swipeRefresh.isRefreshing = false
                    }
                }
            }
    }

    private fun setupRecyclerView() {
        shortcutAdapter = ShortcutAdapter()
        binding.rvScEducation.apply {
            layoutManager = LinearLayoutManager(requireActivity(),
                LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)
            adapter = shortcutAdapter
        }
    }

    private fun setObserver() {
        eduFinanceViewModel.getEducationFinance()
            .observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Result.Loading -> binding.progressBar.visibility = View.VISIBLE
                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE
                        shortcutAdapter.submitList(result.data)
                        binding.swipeRefresh.isRefreshing = false
                    }
                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(requireContext(),
                            result.error, Toast.LENGTH_SHORT).show()
                        binding.swipeRefresh.isRefreshing = false
                    }
                }
            }
    }

    private fun setupAction() {
        binding.apply {
            btnDetailsIncome.setOnClickListener {
                startActivity(Intent(requireContext(), DetailsIncomeActivity::class.java))
            }
            btnDetailsExpenditure.setOnClickListener {
                startActivity(Intent(requireContext(), DetailsExpenditureActivity::class.java))
            }
        }
    }

    private fun observeTotalIncome() {
        val userId = runBlocking { userPreferences.userIdFlow.first() } ?: return
        totalIncomeViewModel.getTotalIncome(userId)
            .observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Result.Loading -> {
                        binding.loadingValueIncome.visibility = View.VISIBLE
                        binding.tvValueIncome.visibility       = View.GONE
                    }
                    is Result.Success -> {
                        displayTotalIncome(result.data)
                        binding.swipeRefresh.isRefreshing = false
                    }
                    is Result.Error   -> {
                        binding.loadingValueIncome.visibility = View.GONE
                        Toast.makeText(requireContext(),
                            result.error, Toast.LENGTH_SHORT).show()
                        binding.swipeRefresh.isRefreshing = false
                    }
                }
            }
    }

    private fun observeTotalExpenditure() {
        val userId = runBlocking { userPreferences.userIdFlow.first() } ?: return
        totalExpenditureViewModel.getTotalExpenditure(userId)
            .observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Result.Loading -> {
                        binding.loadingValueExpenditure.visibility = View.VISIBLE
                        binding.tvValueExpenditure.visibility      = View.GONE
                    }
                    is Result.Success -> {
                        displayTotalExpenditure(result.data)
                        binding.swipeRefresh.isRefreshing = false
                    }
                    is Result.Error   -> {
                        binding.loadingValueExpenditure.visibility = View.GONE
                        Toast.makeText(requireContext(),
                            result.error, Toast.LENGTH_SHORT).show()
                        binding.swipeRefresh.isRefreshing = false
                    }
                }
            }
    }

    private fun displayTotalIncome(totalIncomeResponse: TotalResponse) {
        binding.loadingValueIncome.visibility = View.GONE
        binding.tvValueIncome.apply {
            text       = formatToRupiah(totalIncomeResponse.data?.total ?: 0)
            visibility = View.VISIBLE
        }
    }

    private fun displayTotalExpenditure(totalExpenditureResponse: TotalResponse) {
        binding.loadingValueExpenditure.visibility = View.GONE
        binding.tvValueExpenditure.apply {
            text       = formatToRupiah(totalExpenditureResponse.data?.total ?: 0)
            visibility = View.VISIBLE
        }
    }

    private fun formatToRupiah(value: Int): String {
        val localeID = Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
        return numberFormat.format(value)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
