package com.example.financyq.ui.photo

import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.transition.TransitionManager
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.financyq.R
import com.example.financyq.data.di.Result
import com.example.financyq.data.di.ViewModelFactory
import com.example.financyq.data.local.UserPreferences
import com.example.financyq.data.request.AddExpenditureRequest
import com.example.financyq.data.response.PostImageResponse
import com.example.financyq.data.response.ReceiptItem
import com.example.financyq.databinding.ActivityResultsBinding
import com.example.financyq.databinding.BottomSheetOcrBinding
import com.example.financyq.databinding.DialogCustomTitleBinding
import com.example.financyq.ui.adapter.OcrResultAdapter
import com.example.financyq.ui.manual.AddExpenditureViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.gson.Gson
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class ResultsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultsBinding
    private lateinit var prefs: UserPreferences
    private val viewModel: AddExpenditureViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var ocrResponse: PostImageResponse
    private lateinit var workingList: MutableList<ReceiptItem>
    private lateinit var adapter: OcrResultAdapter
    private var isCollapsed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = UserPreferences.getInstance(this)

        binding.btnBack.setOnClickListener {
            finish()
        }

        // 1) Parse JSON hasil OCR
        val json = intent.getStringExtra("ocr_response")
        if (json.isNullOrBlank()) {
            Toast.makeText(this, "Tidak ada data OCR", Toast.LENGTH_LONG).show()
            finish()
            return
        }
        ocrResponse = Gson().fromJson(json, PostImageResponse::class.java)

        // 2) Setup workingList & adapter
        workingList = ocrResponse.items.toMutableList()
        adapter = OcrResultAdapter { item, pos -> showBottomSheet(item, pos) }
        binding.rvDetailOcr.layoutManager = LinearLayoutManager(this)
        binding.rvDetailOcr.adapter = adapter
        adapter.submitList(workingList.toList())

        // 3) Tampilkan preview image jika ada
        intent.getStringExtra("image_uri")?.let { uriStr ->
            binding.previewImageView.setImageURI(Uri.parse(uriStr))
        }

        // 4) Hide loading di awal
        binding.loadingCard.visibility = View.GONE

        // 5) Tombol Simpan
        binding.btnSave.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Konfirmasi Simpan")
                .setMessage("Apakah kamu ingin menyimpan data ini? Periksa terlebih dahulu kesesuaian data dengan struk.")
                .setPositiveButton("Ya") { d,_ ->
                    d.dismiss()
                    uploadAllItems()
                }
                .setNegativeButton("Batal", null)
                .show()
        }

        binding.tvDetails.paintFlags =
            binding.tvDetails.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        binding.tvDetails.setOnClickListener {
            toggleLiftSection()
        }
    }

    private fun uploadAllItems() {
        binding.loadingCard.visibility = View.VISIBLE
        binding.btnSave.isEnabled = false

        val idUser = runBlocking { prefs.userIdFlow.first() }
        if (idUser == null) {
            binding.loadingCard.visibility = View.GONE
            binding.btnSave.isEnabled = true
            Toast.makeText(this, "User belum login", Toast.LENGTH_LONG).show()
            return
        }

        lifecycleScope.launch {
            kotlinx.coroutines.delay(2000L)
            workingList.forEach { item ->
                // 1) Ambil raw timestamp (bisa "yyyy-MM-dd HH:mm:ss")
                val raw = item.tanggal
                    ?: DateTimeFormatter.ISO_DATE.format(LocalDate.now())

                // 2) Ambil "yyyy-MM-dd" saja
                val dateOnly = raw.take(10)

                // 3) Parse & gabungkan dengan waktu sekarang
                val localDate = LocalDate.parse(dateOnly, DateTimeFormatter.ISO_DATE)
                val now       = LocalTime.now(ZoneId.systemDefault())
                val zdt       = ZonedDateTime.of(localDate, now, ZoneId.systemDefault())

                // 4) Format ke ISO_INSTANT (atau pattern custom jika mau)
                val isoTimestamp = DateTimeFormatter.ISO_INSTANT.format(zdt.toInstant())

                // 4) Kirim semua field termasuk timestamp lengkap
                val req = AddExpenditureRequest(
                    idUser    = idUser,
                    sumber    = item.sumber    ?: "foto",
                    jumlah    = item.totalPengeluaran ?: 0,
                    kategori  = item.kategori  ?: "Struk Belanja",
                    deskripsi = item.namaPengeluaran ?: "",
                    tanggal   = isoTimestamp   // <-- jam:menit:detik ikut terkirim
                )
                viewModel.addExpenditure(req).observe(this@ResultsActivity) { result ->
                    if (result is Result.Error) {
                        Toast.makeText(
                            this@ResultsActivity,
                            "Gagal simpan “${item.namaPengeluaran}”: ${result.error}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            binding.loadingCard.visibility = View.GONE
            binding.btnSave.isEnabled = true
            AlertDialog.Builder(this@ResultsActivity)
                .setCustomTitle(
                    DialogCustomTitleBinding.inflate(layoutInflater).also {
                        it.ivDialogIcon.setImageResource(R.drawable.baseline_check_24)
                        it.tvDialogTitle.text   = getString(R.string.congratulations)
                        it.tvDialogMessage.text = getString(R.string.input_data_expenditure)
                    }.root
                )
                .setPositiveButton(R.string.ok) { d,_ ->
                    d.dismiss()
                    finish()
                }
                .setCancelable(false)
                .show()
        }
    }

    private fun showBottomSheet(item: ReceiptItem, position: Int) {
        val sheet = BottomSheetOcrBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(this).apply { setContentView(sheet.root) }

        // Isi field
        sheet.EditDate.setText(item.tanggal?.take(10) ?: "")
        sheet.EditCategory.setText(item.kategori ?: "")
        sheet.EditNameExpenditure.setText(item.namaPengeluaran ?: "")
        sheet.EditTotalExpenditure.setText(item.totalPengeluaran?.toString() ?: "")
        sheet.EditSource.setText(item.sumber ?: "")
        sheet.cbApplyToAll.isChecked = false

        // DatePicker untuk EditDate
        sheet.EditDate.apply {
            isFocusable = false; isClickable = true
            setOnClickListener {
                val constraintsBuilder = CalendarConstraints.Builder()
                    .setValidator(DateValidatorPointBackward.now()) // hanya masa lalu & hari ini
                    .build()
                val picker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Pilih Tanggal")
                    .setCalendarConstraints(constraintsBuilder)
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .build()
                picker.show(supportFragmentManager, "BS_DATE_PICKER")
                picker.addOnPositiveButtonClickListener { millis ->
                    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    setText(sdf.format(Date(millis)))
                }
            }
        }

        // Save perubahan
        sheet.btnSave.setOnClickListener {
            // --- ambil semua input field ---
            val dateStr    = sheet.EditDate.text.toString().trim()
            val category   = sheet.EditCategory.text.toString().trim()
            val nameExp    = sheet.EditNameExpenditure.text.toString().trim()
            val totalStr   = sheet.EditTotalExpenditure.text.toString().trim()
            val source     = sheet.EditSource.text.toString().trim()

            // --- validasi jika ada field kosong ---
            if (dateStr.isEmpty() || category.isEmpty() || nameExp.isEmpty() || totalStr.isEmpty() || source.isEmpty()) {
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
                .setTitle("Konfirmasi Ubah")
                .setMessage("Yakin ingin menyimpan perubahan ini?")
                .setPositiveButton("Simpan") { _, _ ->
                    // 1) Ambil tanggal saja dari EditDate
                    val pickedDate = LocalDate.parse(
                        sheet.EditDate.text.toString(),
                        DateTimeFormatter.ISO_DATE
                    )
                    // 2) Ambil waktu sekarang di zona lokal
                    val zdt = ZonedDateTime.of(
                        pickedDate,
                        LocalTime.now(),
                        ZoneId.systemDefault()
                    )
                    // 3) Format jadi ISO_INSTANT: "2025-07-02T10:15:30Z"
                    val fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                    val isoTimestamp =  zdt.toLocalDateTime().format(fmt)

                    // ambil field lain
                    val newCat   = sheet.EditCategory.text.toString()
                    val newName  = sheet.EditNameExpenditure.text.toString()
                    val rawTotal = sheet.EditTotalExpenditure.text.toString().replace("\\D".toRegex(), "")
                    val newTotal = rawTotal.toIntOrNull() ?: item.totalPengeluaran ?: 0
                    val newSrc   = sheet.EditSource.text.toString()

                    // update workingList jika apply to all
                    if (sheet.cbApplyToAll.isChecked) {
                        workingList = workingList.map {
                            it.copy(tanggal = isoTimestamp, kategori = newCat)
                        }.toMutableList()
                    } else {
                        workingList[position] = workingList[position].copy(
                            tanggal          = isoTimestamp,
                            kategori         = newCat,
                            namaPengeluaran  = newName,
                            totalPengeluaran = newTotal,
                            sumber           = newSrc
                        )
                    }
                    adapter.submitList(workingList.toList())
                    dialog.dismiss()
                }
                .setNegativeButton("Batal", null)
                .show()
        }

        // Delete item
        sheet.btnDelete.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Konfirmasi Hapus")
                .setMessage("Yakin ingin menghapus item ini?")
                .setPositiveButton("Hapus") { _,_ ->
                    workingList.removeAt(position)
                    adapter.submitList(workingList.toList())
                    dialog.dismiss()
                }
                .setNegativeButton("Batal", null)
                .show()
        }

        // Close
        sheet.btnClose.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun toggleLiftSection() {
        val layout = binding.main
        val cs     = ConstraintSet().apply { clone(layout) }
        val margin = resources.getDimensionPixelSize(R.dimen.activity_vertical_margin)

        if (!isCollapsed) {
            // collapse: angkat tv_struk_data ke bawah tv_details
            cs.connect(
                binding.tvStrukData.id, ConstraintSet.TOP,
                binding.tvDetails.id, ConstraintSet.BOTTOM,
                margin
            )
        } else {
            // expand: kembalikan ke bawah previewImageView
            cs.connect(
                binding.tvStrukData.id, ConstraintSet.TOP,
                binding.previewImageView.id, ConstraintSet.BOTTOM,
                margin
            )
        }

        // mulai transisi animasi layout
        TransitionManager.beginDelayedTransition(layout)
        cs.applyTo(layout)

        // sekarang toggle visibility imageView
        binding.previewImageView.visibility =
            if (!isCollapsed) View.GONE else View.VISIBLE

        isCollapsed = !isCollapsed
    }
}
