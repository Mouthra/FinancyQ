package com.example.financyq.ui.details

import android.app.Dialog
import android.os.Bundle
import android.view.ViewGroup
import android.widget.NumberPicker
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.financyq.R
import java.util.*

/**
 * DialogFragment sederhana: dua NumberPicker (bulan & tahun).
 * @param initialMonth 0-based (Jan=0)
 * @param initialYear  e.g. 2025
 * @param onPick       callback (month: 0–11, year)
 */
class MonthYearPickerDialog(
    private val initialMonth: Int,
    private val initialYear: Int,
    private val onPick: (month: Int, year: Int) -> Unit
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = layoutInflater.inflate(R.layout.dialog_month_year_picker, null)
        val monthPicker = view.findViewById<NumberPicker>(R.id.monthPicker)
        val yearPicker  = view.findViewById<NumberPicker>(R.id.yearPicker)

        // ambil nama bulan dari string-array
        val monthNames = resources.getStringArray(R.array.month_names_id)
        monthPicker.minValue = 0
        monthPicker.maxValue = monthNames.size - 1
        monthPicker.displayedValues = monthNames
        monthPicker.value = initialMonth

        // setup tahun (misal rentang sekarang -5 .. +5)
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val startYear = currentYear - 5
        val endYear   = currentYear + 5
        yearPicker.minValue = startYear
        yearPicker.maxValue = endYear
        yearPicker.value = initialYear

        return AlertDialog.Builder(requireContext())
            .setView(view)
            .setTitle(R.string.select_month_year)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                onPick(monthPicker.value, yearPicker.value)
            }
            .setNegativeButton(android.R.string.cancel, null)
            .create().apply {
                window?.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }
    }
}
