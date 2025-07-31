package com.example.financyq.ui.photo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.financyq.R
import com.example.financyq.data.di.Result
import com.example.financyq.data.di.ViewModelFactory
import com.example.financyq.data.response.PostImageResponse
import com.example.financyq.databinding.ActivityPhotoBinding
import com.google.gson.Gson
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class PhotoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPhotoBinding
    private var currentImageUri: Uri? = null

    // Ambil instance ViewModel yang benar
    private val postImageViewModel: PostImageViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhotoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            btnGallery.setOnClickListener { launcherGallery.launch("image/*") }
            btnCamera.setOnClickListener { startCamera() }
            btnAnalyze.setOnClickListener { analyzeImage() }
            btnBack.setOnClickListener { finish() }
        }
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            currentImageUri = it
            showCropConfirmation(it)
        }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        currentImageUri?.let { uri ->
            launcherCamera.launch(uri)
        }
    }

    private val launcherCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            currentImageUri?.let {
                showCropConfirmation(it)
            }
        }
    }

    private fun showCropConfirmation(uri: Uri) {
        AlertDialog.Builder(this)
            .setTitle("PERHATIAN!")
            .setMessage("Pastikan kamu sudah memasukkan keseluruhan struk agar hasil OCR akurat. Lanjutkan memotong?")
            .setPositiveButton("Lanjutkan") { dialog, _ ->
                dialog.dismiss()
                cropImage(uri)
            }
            .setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }

    private fun cropImage(uri: Uri) {
        CropImage.activity(uri)
            .setGuidelines(CropImageView.Guidelines.ON)
            .start(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                currentImageUri = result.uri
                binding.previewImageView.setImageURI(result.uri)
            }
        }
    }

    private fun analyzeImage() {
        val uri = currentImageUri ?: run {
            Toast.makeText(this, R.string.empty_image_warning, Toast.LENGTH_SHORT).show()
            return
        }

        // 1) ubah Uri ke File, compress
        val imageFile: File = uriToFile(uri, this).reduceFileImage()
        val reqBody = imageFile.asRequestBody("image/jpeg".toMediaType())
        val part = MultipartBody.Part.createFormData("file", imageFile.name, reqBody)

        // 2) panggil observer
        postImageViewModel.postImage(part).observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    // tampilkan loading, disable button
                    binding.loadingCard.visibility = View.VISIBLE
                    binding.btnAnalyze.isEnabled = false
                }
                is Result.Success -> {
                    // hide loading, enable button
                    binding.loadingCard.visibility = View.GONE
                    binding.btnAnalyze.isEnabled = true
                    // proses hasil
                    handleOcrSuccess(result.data, imageFile)
                }
                is Result.Error -> {
                    // hide loading, enable button
                    binding.loadingCard.visibility = View.GONE
                    binding.btnAnalyze.isEnabled = true
                    Toast.makeText(this, "Error: ${result.error}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun handleOcrSuccess(responseBody: PostImageResponse, imageFile: File) {
        val items = responseBody.items

        if (items.isNullOrEmpty()) {
            // ✅ Jika kosong, tampilkan dialog error dan JANGAN pindah ke ResultsActivity
            AlertDialog.Builder(this)
                .setTitle("Foto Tidak Valid")
                .setMessage("Foto bukan struk belanja atau tidak dapat dikenali. Silakan coba lagi dengan foto yang lebih jelas.")
                .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                .show()
            return
        }

        // ✅ Kalau ada items, baru lanjut
        val json = Gson().toJson(responseBody)
        val imageUriString = Uri.fromFile(imageFile).toString()

        Intent(this, ResultsActivity::class.java).apply {
            putExtra("ocr_response", json)
            putExtra("image_uri", imageUriString)
            startActivity(this)
        }
    }
}
