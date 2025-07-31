package com.example.financyq.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.financyq.R
import com.example.financyq.data.di.Result
import com.example.financyq.data.di.ViewModelFactory
import com.example.financyq.data.local.UserPreferences
import com.example.financyq.data.request.UpdateExpenditureRequest
import com.example.financyq.data.response.TransactionsItem
import com.example.financyq.databinding.ActivityDetailsExpenditureBinding
import com.example.financyq.databinding.BottomSheetDetailExpenditureBinding
import com.example.financyq.ui.adapter.DetailsExpenditureAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class DetailsExpenditureActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailsExpenditureBinding
    private lateinit var userPrefs: UserPreferences
    private lateinit var adapter: DetailsExpenditureAdapter

    // ViewModels untuk Expenditure
    private val vmDetail by viewModels<DetailsExpenditureViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private val vmUpdate by viewModels<UpdateExpenditureViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private val vmDelete by viewModels<DeleteExpenditureViewModel> {
        ViewModelFactory.getInstance(this)
    }

    // Semua transaksi untuk difilter
    private var allTx: List<TransactionsItem> = emptyList()

    // Bulan (0–11) dan Tahun terpilih
    private var selMonth = Calendar.getInstance().get(Calendar.MONTH)
    private var selYear  = Calendar.getInstance().get(Calendar.YEAR)

    private enum class FilterMode { ALL, FOTO, MANUAL }
    private var filterMode = FilterMode.ALL

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsExpenditureBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userPrefs = UserPreferences.getInstance(this)
        setupUI()
        loadData()
    }

    private fun setupUI() {
        // 1) tombol back
        binding.btnBack.setOnClickListener { finish() }

        // 2) dropdown Bulan/Tahun
        updatePeriodLabel()
        binding.tvPeriodSelector.setOnClickListener {
            MonthYearPickerDialog(selMonth, selYear) { month, year ->
                selMonth = month
                selYear  = year
                updatePeriodLabel()
                applyCurrentFilter()
            }.show(supportFragmentManager, "MY_PICKER")
        }

        // 3) RecyclerView + adapter
        adapter = DetailsExpenditureAdapter { item -> showBottomSheet(item) }
        binding.rvDetailExpenditure.layoutManager = LinearLayoutManager(this)
        binding.rvDetailExpenditure.adapter = adapter

        // 4) segmented filter
        binding.periodToggle.addOnButtonCheckedListener { _, _, isChecked ->
            if (!isChecked) return@addOnButtonCheckedListener
            applyCurrentFilter()
        }

        binding.btnFilter.setOnClickListener {
            binding.periodToggle.clearChecked()
            filterMode = when (filterMode) {
                FilterMode.ALL    -> FilterMode.FOTO
                FilterMode.FOTO   -> FilterMode.MANUAL
                FilterMode.MANUAL -> FilterMode.ALL
            }
            binding.btnFilter.text = when (filterMode) {
                FilterMode.ALL    -> "Semua"
                FilterMode.FOTO   -> "Foto"
                FilterMode.MANUAL -> "Manual"
            }
            applyCurrentFilter()
        }
    }

    private fun updatePeriodLabel() {
        // ambil array bulan dari resources
        val monthNames = resources.getStringArray(R.array.month_names_id)
        // format dengan string resource
        val label = getString(
            R.string.transaction_period_label,
            monthNames[selMonth],
            selYear
        )
        binding.tvPeriodSelector.text = label
    }

    private fun loadData() {
        val uid = runBlocking { userPrefs.userIdFlow.first() } ?: return
        vmDetail.getDetailExpenditure(uid).observe(this) { res ->
            when (res) {
                is Result.Loading -> {
                    binding.progressBarDetailExpenditure.visibility = View.VISIBLE
                }
                is Result.Success -> {
                    binding.progressBarDetailExpenditure.visibility = View.GONE
                    allTx = res.data.transactions.orEmpty().filterNotNull()
                    applyCurrentFilter()
                }
                is Result.Error -> {
                    binding.progressBarDetailExpenditure.visibility = View.GONE
                    Toast.makeText(this, res.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun applyCurrentFilter() {
        // 1) filter periode (existing)
        val byPeriod = when (binding.periodToggle.checkedButtonId) {
            R.id.btnDaily   -> allTx.filterLast24h()
            R.id.btnWeekly  -> allTx.filterLast7d()
            R.id.btnMonthly -> allTx.filterMonth(selYear, selMonth)
            else             -> allTx
        }

        // 2) filter sumber berdasarkan filterMode (baru)
        val bySource = when (filterMode) {
            FilterMode.FOTO   -> byPeriod.filter  { it.sumber.equals("foto",   ignoreCase = true) }
            FilterMode.MANUAL -> byPeriod.filter  { it.sumber.equals("manual", ignoreCase = true) }
            FilterMode.ALL    -> byPeriod
        }

        // 3) sort & tampilkan
        val finalList = bySource.sortedByDescending { it.tanggal }
        adapter.submitList(finalList)
        binding.tvEmptyMessage.visibility =
            if (finalList.isEmpty()) View.VISIBLE else View.GONE
    }

    // ---- extensions utk filtering ----

    private fun List<TransactionsItem>.filterLast24h(): List<TransactionsItem> {
        val now = Date()
        val cut = Calendar.getInstance().apply {
            time = now; add(Calendar.DAY_OF_YEAR, -1)
        }.time
        return filter { it.tanggalString()?.after(cut) == true }
    }
    private fun List<TransactionsItem>.filterLast7d(): List<TransactionsItem> {
        val now = Date()
        val cut = Calendar.getInstance().apply {
            time = now; add(Calendar.DAY_OF_YEAR, -7)
        }.time
        return filter { it.tanggalString()?.after(cut) == true }
    }
    private fun List<TransactionsItem>.filterMonth(year: Int, month: Int): List<TransactionsItem> =
        filter {
            it.tanggal
                ?.take(7)               // "yyyy-MM"
                ?.let { ym ->
                    val (y,m) = ym.split("-")
                    y.toIntOrNull()==year && m.toIntOrNull()==month+1
                } == true
        }
    private fun TransactionsItem.tanggalString(): Date? =
        runCatching {
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                .parse(this.tanggal!!)
        }.getOrNull()

    // ---- bottom sheet edit / delete ----

    private fun showBottomSheet(item: TransactionsItem) {
        val sheet   = BottomSheetDetailExpenditureBinding.inflate(LayoutInflater.from(this))
        val dialog  = BottomSheetDialog(this).apply { setContentView(sheet.root) }

        // prefill
        sheet.EditDate.setText(item.tanggal?.take(10).orEmpty())
        sheet.EditCategory.setText(item.kategori)
        sheet.EditNameExpenditure.setText(item.deskripsi)
        sheet.EditTotalExpenditure.setText(item.jumlah.toString())
        sheet.EditSource.setText(item.sumber)

        // date picker
        sheet.EditDate.setOnClickListener {
            val constraintsBuilder = CalendarConstraints.Builder()
                .setValidator(DateValidatorPointBackward.now()) // hanya masa lalu & hari ini
                .build()
            val picker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Pilih Tanggal")
                .setCalendarConstraints(constraintsBuilder)
                .setSelection(
                    runCatching {
                        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            .parse(sheet.EditDate.text.toString())?.time
                    }.getOrNull() ?: MaterialDatePicker.todayInUtcMilliseconds()
                )
                .build()
            picker.show(supportFragmentManager, "DATE_PICKER")
            picker.addOnPositiveButtonClickListener { millis ->
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                sheet.EditDate.setText(sdf.format(Date(millis)))
            }
        }

        sheet.btnClose.setOnClickListener { dialog.dismiss() }
        sheet.btnDelete.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle(R.string.confirm)
                .setMessage(R.string.are_you_sure_want_to_delete_it)
                .setPositiveButton(R.string.yes) { _, _ ->
                    vmDelete.deleteExpenditure(item.idTransaksi!!).observe(this) { r ->
                        if (r is Result.Success) {
                            dialog.dismiss()
                            loadData()
                        }
                    }
                }
                .setNegativeButton(R.string.no, null)
                .show()
        }
        sheet.btnSave.setOnClickListener {
            // --- ambil semua nilai input ---
            val dateStr    = sheet.EditDate.text.toString().trim()
            val category   = sheet.EditCategory.text.toString().trim()
            val nameIncome = sheet.EditNameExpenditure.text.toString().trim()
            val totalStr   = sheet.EditTotalExpenditure.text.toString().trim()
            val source     = sheet.EditSource.text.toString().trim()

            // --- validasi jika ada yang kosong ---
            if (dateStr.isEmpty() || category.isEmpty() || nameIncome.isEmpty() || totalStr.isEmpty() || source.isEmpty()) {
                Toast.makeText(this, R.string.please_fill_in_all_columns, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // --- ambil angka bersih dari total (hilangkan Rp, titik, koma) ---
            val totalAmount = totalStr.replace("[^\\d]".toRegex(), "").toIntOrNull() ?: 0

            // --- validasi Rp0 ---
            if (totalAmount <= 0) {
                Toast.makeText(this, R.string.total_expenditure_cannot_be_zero, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            AlertDialog.Builder(this)
                .setTitle(R.string.confirm)
                .setMessage(R.string.are_you_sure_about_this_change)
                .setPositiveButton(R.string.yes) { _, _ ->
                    // 1) parse tanggal saja
                    val pickedDate = LocalDate.parse(sheet.EditDate.text.toString())

                    // 2) ambil waktu sekarang di zona lokal
                    val zdt = ZonedDateTime.of(pickedDate, LocalTime.now(), ZoneId.systemDefault())

                    // 3) ubah ke Instant (UTC) & format ISO_INSTANT
                    val isoTimestamp = DateTimeFormatter.ISO_INSTANT.format(zdt.toInstant())
                    val req = UpdateExpenditureRequest(
                        jumlah    = sheet.EditTotalExpenditure.text.toString().filter { it.isDigit() }.toInt(),
                        sumber    = sheet.EditSource.text.toString(),
                        kategori  = sheet.EditCategory.text.toString(),
                        deskripsi = sheet.EditNameExpenditure.text.toString(),
                        tanggal   = isoTimestamp
                    )
                    vmUpdate.updateExpenditure(item.idTransaksi!!, req)
                        .observe(this) { r ->
                            if (r is Result.Success) {
                                dialog.dismiss()
                                loadData()
                            }
                        }
                }
                .setNegativeButton(R.string.no, null)
                .show()
        }

        dialog.show()
    }
}
