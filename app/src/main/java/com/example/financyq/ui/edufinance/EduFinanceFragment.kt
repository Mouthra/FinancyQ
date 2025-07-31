package com.example.financyq.ui.edufinance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.financyq.ui.adapter.EduFinanceAdapter
import com.example.financyq.data.di.Result
import com.example.financyq.data.di.ViewModelFactory
import com.example.financyq.databinding.FragmentEduFinanceBinding

class EduFinanceFragment : Fragment() {

    private var _binding: FragmentEduFinanceBinding? = null
    private val binding get() = _binding!!
    private val eduFinanceViewModel: EduFinanceViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }
    private lateinit var eduFinanceAdapter: EduFinanceAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEduFinanceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setObserver()
    }


    private fun setupRecyclerView() {
        eduFinanceAdapter = EduFinanceAdapter()
        binding.rvEducation.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = eduFinanceAdapter
        }
    }

    private fun setObserver() {
        eduFinanceViewModel.getEducationFinance().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success -> {
                    binding.pbNews.visibility = View.GONE
                    eduFinanceAdapter.submitList(result.data)
                }
                is Result.Error -> {
                    binding.pbNews.visibility = View.GONE
                    Toast.makeText(requireContext(), result.error, Toast.LENGTH_SHORT).show()
                }
                is Result.Loading -> {
                    binding.pbNews.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
