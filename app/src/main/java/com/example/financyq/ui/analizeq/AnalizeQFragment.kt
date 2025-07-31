package com.example.financyq.ui.analizeq

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.financyq.FinancialData
import com.example.financyq.R
import com.example.financyq.data.di.Result
import com.example.financyq.data.di.ViewModelFactory
import com.example.financyq.data.local.UserPreferences
import com.example.financyq.databinding.FragmentAnalizeQBinding
import com.example.financyq.ui.manual.ManualActivity
import com.example.financyq.ui.photo.PhotoActivity
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AnalizeQFragment : Fragment() {

    private var _binding: FragmentAnalizeQBinding? = null
    private val binding get() = _binding!!

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var userPreferences: UserPreferences

    private var apiTotalIncome: Float? = null
    private var apiTotalExpenditure: Float? = null

    private val totalIncomeViewModel: TotalIncomeViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }
    private val totalExpenditureViewModel: TotalExpenditureViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }
    private val exportPdfViewModel: ExportPdfViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: android.view.ViewGroup?,
        savedInstanceState: Bundle?
    ): android.view.View {
        _binding = FragmentAnalizeQBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: android.view.View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userPreferences = UserPreferences.getInstance(requireContext())

        // Setup permission launcher
        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            if (granted) performPdfExport()
            else Toast.makeText(requireContext(),
                "Izin dibutuhkan untuk menyimpan file", Toast.LENGTH_SHORT).show()
        }

        // Pull-to-refresh
        binding.swipeRefresh.setOnRefreshListener {
            observeTotalIncome()
            observeTotalExpenditure()
        }

        observeTotalIncome()
        observeTotalExpenditure()
        setupAction()
    }

    private fun observeTotalIncome() {
        val userId = runBlocking { userPreferences.userIdFlow.first() } ?: return
        totalIncomeViewModel.getTotalIncome(userId)
            .observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Result.Loading -> showChartLoading(true)
                    is Result.Success -> {
                        apiTotalIncome = result.data.data?.total?.toFloat() ?: 0f
                        tryShowPieChart()
                        binding.swipeRefresh.isRefreshing = false
                    }
                    is Result.Error -> {
                        showChartLoading(false)
                        binding.swipeRefresh.isRefreshing = false
                        Toast.makeText(requireContext(), result.error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    private fun observeTotalExpenditure() {
        val userId = runBlocking { userPreferences.userIdFlow.first() } ?: return
        totalExpenditureViewModel.getTotalExpenditure(userId)
            .observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Result.Loading -> showChartLoading(true)
                    is Result.Success -> {
                        apiTotalExpenditure = result.data.data?.total?.toFloat() ?: 0f
                        tryShowPieChart()
                        binding.swipeRefresh.isRefreshing = false
                    }
                    is Result.Error -> {
                        showChartLoading(false)
                        binding.swipeRefresh.isRefreshing = false
                        Toast.makeText(requireContext(), result.error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    private fun tryShowPieChart() {
        val income = apiTotalIncome
        val expend = apiTotalExpenditure
        if (income != null && expend != null) {
            showPieChart(FinancialData(income, expend))
        }
    }

    private fun showPieChart(data: FinancialData) {
        val income = data.totalPemasukan
        val expend = data.totalPengeluaran

        // === handle “belum ada data” ===
        if (income == 0f && expend == 0f) {
            // kosongkan chart
            binding.pieChart.data = null
            binding.pieChart.invalidate()

            // kosongkan selisih
            binding.tvDifference.text = ""

            // tampilkan pesan “belum ada data”
            binding.tvStatusMessage.apply {
                text = getString(R.string.analysis_no_data)  // nanti tambahkan di strings.xml
                visibility = View.VISIBLE
            }

            // sembunyikan label selisih
            binding.tvDifferenceLabel.visibility = View.GONE
            return
        }
        // =================================

        // kalau ada data, jalankan seperti biasa:
        val entries = listOf(
            PieEntry(income,    "Pemasukan"),
            PieEntry(expend,    "Pengeluaran")
        )
        val dataSet = PieDataSet(entries, "").apply {
            colors = listOf(Color.parseColor("#4CAF50"), Color.parseColor("#F44336"))
            valueTextColor = Color.BLACK
            valueTextSize = 14f
        }
        val pieData = PieData(dataSet).apply {
            setValueFormatter(object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String =
                    NumberFormat.getCurrencyInstance(Locale("in","ID"))
                        .format(value.toInt())
            })
        }
        binding.pieChart.data = pieData
        binding.pieChart.apply {
            description.isEnabled   = false
            isDrawHoleEnabled       = true
            holeRadius              = 40f
            setEntryLabelColor(Color.BLACK)
            animateY(1000)
            legend.isEnabled        = false
            invalidate()
        }

        // selisih
        val diff = income - expend
        binding.tvDifference.text = NumberFormat
            .getCurrencyInstance(Locale("in","ID"))
            .format(diff.toInt())
        binding.tvDifferenceLabel.visibility = View.VISIBLE

        // status message Gen-Z style
        val positiveMsgs = listOf(
            "Wah, cuan bertambah, gaskeun! 🤑",
            "Dompet aman, fresh cash flow! 💸",
            "Level up finansial, cuan on point! 💯",
            "Saldo happy, mood happy! 🎉"
        )
        val neutralMsgs = listOf(
            "Seimbang, dompet stabil! 🤝",
            "Saldo stabil, keep it chill. \uD83D\uDE0C\uD83D\uDCB5",
            "Saldo pas–pasan, kece! 😎",
            "Uang masuk-keluar, semua balance. ⚖\uFE0F"
        )
        val negativeMsgs = listOf(
            "Wah, boros nih, sabar ya! 😅",
            "Dompet lagi diet, sabar dulu… 🥲",
            "Ups, saldo minus!, musti kontrol lagi! 🚨",
            "Uang menipis, vibes auto loyo \uD83D\uDE1E"
        )

        val statusText = when {
            diff >  0f -> positiveMsgs.random()
            diff == 0f -> neutralMsgs.random()
            else       -> negativeMsgs.random()
        }
        binding.tvStatusMessage.apply {
            text = statusText
            visibility = View.VISIBLE
        }

        showChartLoading(false)
    }

    private fun showChartLoading(isLoading: Boolean) {
        binding.loadingChart.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.pieChart.visibility = if (isLoading) View.INVISIBLE else View.VISIBLE
        binding.tvDifference.visibility = if (isLoading) View.GONE else View.VISIBLE
        binding.tvDifferenceLabel.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    private fun setupAction() {
        binding.btnFoto.setOnClickListener {
            startActivity(Intent(requireContext(), PhotoActivity::class.java))
        }
        binding.btnManual.setOnClickListener {
            startActivity(Intent(requireContext(), ManualActivity::class.java))
        }
        binding.btnExportPdf.setOnClickListener {
            exportPdf()
        }
    }

    private fun exportPdf() {
        // Android Q+ no permission, M-N need WRITE_EXTERNAL_STORAGE
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> performPdfExport()
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                val perm = Manifest.permission.WRITE_EXTERNAL_STORAGE
                if (ContextCompat.checkSelfPermission(requireContext(), perm)
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermissionLauncher.launch(perm)
                } else performPdfExport()
            }
            else -> performPdfExport()
        }
    }

    private fun performPdfExport() {
        val userId = runBlocking { userPreferences.userIdFlow.first() } ?: return
        exportPdfViewModel.exportPdf(userId)
            .observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Result.Loading -> {
                        Toast.makeText(requireContext(),
                            "Menyimpan PDF...", Toast.LENGTH_SHORT).show()
                    }
                    is Result.Success -> {
                        val fileName = "FinancyQ_Report_" +
                                SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date()) +
                                ".pdf"
                        val path = savePDFToDownloadsFolder(result.data, fileName)
                        if (path != null) {
                            Toast.makeText(requireContext(),
                                "PDF tersimpan di: $path", Toast.LENGTH_LONG).show()
                            openPDF(path)
                        } else {
                            Toast.makeText(requireContext(),
                                "Gagal menyimpan PDF", Toast.LENGTH_LONG).show()
                        }
                    }
                    is Result.Error -> {
                        Toast.makeText(requireContext(),
                            "Error: ${result.error}", Toast.LENGTH_LONG).show()
                    }
                }
            }
    }

    private fun savePDFToDownloadsFolder(body: ResponseBody, fileName: String): String? {
        return try {
            val downloads = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            if (!downloads.exists()) downloads.mkdirs()

            val file = File(downloads, fileName)
            body.byteStream().use { input ->
                FileOutputStream(file).use { out -> input.copyTo(out); out.flush() }
            }

            // biar langsung muncul di app Download
            android.media.MediaScannerConnection.scanFile(
                requireContext(),
                arrayOf(file.absolutePath),
                arrayOf("application/pdf"),
                null
            )

            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun openPDF(path: String) {
        val file = File(path)
        val uri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.fileprovider",
            file
        )
        Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(this)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
