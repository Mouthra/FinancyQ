package com.example.financyq.ui.manual

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.financyq.R
import com.example.financyq.data.di.Result
import com.example.financyq.data.di.ViewModelFactory
import com.example.financyq.data.local.UserPreferences
import com.example.financyq.data.request.AddExpenditureRequest
import com.example.financyq.databinding.DialogCustomTitleBinding
import com.example.financyq.databinding.FragmentExpenditureBinding
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

class ExpenditureFragment : Fragment() {

    private var _binding: FragmentExpenditureBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddExpenditureViewModel by viewModels {
        ViewModelFactory.getInstance(requireActivity())
    }

    private lateinit var userPreferences: UserPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExpenditureBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userPreferences = UserPreferences.getInstance(requireContext())
        binding.SourceEditText.setText(R.string.manual_text)

        // Pasang listener untuk DateEditText
        binding.DateEditText.apply {
            // Pastikan clickable dan tidak focusable agar keyboard tidak muncul
            isFocusable = false
            isClickable = true
            setOnClickListener {
                showDatePicker()
            }
        }

        binding.saveExpenditure.setOnClickListener {
            val pickedDateStr = binding.DateEditText.text.toString()
            val category = binding.CategoryEditText.text.toString()
            val name = binding.NameExpenditureEditText.text.toString()
            val amountString = binding.TotalExpenditureEditText.text.toString().replace("[Rp.]".toRegex(), "")
            val amount = amountString.toIntOrNull()
            val source = binding.SourceEditText.text.toString()

            if (amount == 0) {
                binding.TotalExpenditureEditTextLayout.error =
                    getString(R.string.total_expenditure_cannot_be_zero)
                return@setOnClickListener
            } else {
                binding.TotalExpenditureEditTextLayout.error = null
            }

            if (pickedDateStr.isNotEmpty() && category.isNotEmpty() && name.isNotEmpty() && amount != null) {

                val localDate = LocalDate.parse(
                    pickedDateStr,
                    DateTimeFormatter.ISO_DATE
                )
                // 2) Ambil waktu sekarang + zona lokal, lalu konversi ke OffsetDateTime
                val zdt: ZonedDateTime = ZonedDateTime.of(
                    localDate,
                    LocalTime.now(),
                    ZoneId.systemDefault()
                )
                val instantUtc: Instant = zdt.toInstant()

                // 3) Format ke ISO_INSTANT, misal "2025-07-02T07:35:20Z"
                val isoTimestamp: String = DateTimeFormatter.ISO_INSTANT.format(instantUtc)
                // -> "2025-07-02T14:35:20+07:00"

                val idUser = runBlocking { userPreferences.userIdFlow.first() }

                if (idUser != null ) {
                    val addExpenditureRequest = AddExpenditureRequest(
                        idUser = idUser,
                        sumber = source,
                        jumlah = amount,
                        kategori = category,
                        deskripsi = name,
                        tanggal = isoTimestamp
                    )

                    viewModel.addExpenditure(addExpenditureRequest).observe(viewLifecycleOwner) { result ->
                        when (result) {
                            is Result.Loading -> {
                                binding.loadingCard.visibility = View.VISIBLE
                            }
                            is Result.Success -> {
                                binding.loadingCard.visibility = View.GONE
                                showSuccessDialogAndNavigate()
                            }
                            is Result.Error -> {
                                binding.loadingCard.visibility = View.GONE
                                Toast.makeText(
                                    requireContext(),
                                    R.string.failed_to_add_expenditure,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        R.string.id_user_not_found,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(requireContext(), R.string.please_fill_in_all_columns, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showDatePicker() {
        val constraintsBuilder = CalendarConstraints.Builder()
            .setValidator(DateValidatorPointBackward.now()) // hanya masa lalu & hari ini
            .build()

        val picker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Pilih Tanggal")
            .setCalendarConstraints(constraintsBuilder)
            .build()
        picker.show(childFragmentManager, "DATE_PICKER")
        picker.addOnPositiveButtonClickListener { selection ->
            // selection = epoch millis UTC
            val isoFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            binding.DateEditText.setText(isoFormat.format(Date(selection)))
        }
    }

    private fun showSuccessDialogAndNavigate() {
        // 1. Inflate binding untuk custom title
        val titleBinding = DialogCustomTitleBinding.inflate(layoutInflater).apply {
            // set icon & title (jika perlu override default)
            ivDialogIcon.setImageResource(R.drawable.baseline_check_24)
            tvDialogTitle.text = getString(R.string.congratulations)
            tvDialogMessage.text = getString(R.string.input_data_expenditure)
        }
        // 2. Build dialog dengan custom title binding.root
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setCustomTitle(titleBinding.root)
            .setPositiveButton(R.string.ok) { dialog, _ ->
                dialog.dismiss()
                clearEditTextFields(
                    binding.DateEditText,
                    binding.CategoryEditText,
                    binding.NameExpenditureEditText,
                    binding.TotalExpenditureEditText,
                    binding.SourceEditText
                )
                activity?.onBackPressed()
            }
            .setCancelable(false)
            .show()
    }

    private fun clearEditTextFields(vararg editTexts: EditText) {
        for (editText in editTexts) {
            editText.text.clear()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
