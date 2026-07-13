package com.example.tr.uitr.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tr.data.remote.api.ApiService
import com.example.tr.data.remote.api.RetrofitClient
import com.example.tr.data.remote.model.Ingredient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class IngredientViewModel : ViewModel() {
    var ingredients = mutableStateListOf<Ingredient>()
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        fetchIngredients()
    }

    private fun createPartFromString(descriptionString: String): RequestBody {
        return descriptionString.toRequestBody("text/plain".toMediaTypeOrNull())
    }

    fun fetchIngredients() {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = RetrofitClient.instance.getIngredients()
                if (response.success) {
                    withContext(Dispatchers.Main) {
                        ingredients.clear()
                        ingredients.addAll(response.data)
                    }
                } else {
                    errorMessage = response.message
                }
            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }

    // --- UPDATE PADA FUNGSI createIngredient ---
    fun createIngredient(context: Context, name: String, quantity: Double, unit: String, imageUri: Uri?) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val imagePart = prepareImagePart(context, imageUri)
                val quantityStr = if (quantity % 1 == 0.0) quantity.toInt().toString() else quantity.toString()

                val response = RetrofitClient.instance.createIngredient(
                    name = MultipartBody.Part.createFormData("nama", name),         // 🔴 Tetap nama
                    unit = MultipartBody.Part.createFormData("unit", unit),         // 🔴 UBAH DARI "satuan" MENJADI "unit"
                    quantity = MultipartBody.Part.createFormData("stok", quantityStr), // 🔴 Tetap stok
                    gambar = imagePart
                )

                if (response.success) {
                    fetchIngredients()
                } else {
                    errorMessage = response.message
                }
            } catch (e: retrofit2.HttpException) {
                val errorBodyJson = e.response()?.errorBody()?.string()
                Log.e("INVENTORI_ERROR", "HTTP Error: $errorBodyJson")
                errorMessage = "Gagal menambah data: $errorBodyJson"
            } catch (e: Exception) {
                Log.e("INVENTORI_ERROR", "Error: ${e.message}")
                errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }

    // --- UPDATE PADA FUNGSI updateIngredient ---
    // 🔴 Tambahkan parameter currentImageUrl: String? di akhir fungsi
    fun updateIngredient(
        context: Context,
        id: Long,
        name: String,
        quantity: Double,
        unit: String,
        imageUri: Uri?,
        currentImageUrl: String? // 🔴 Tampung URL gambar lama
    ) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val quantityStr = if (quantity % 1 == 0.0) quantity.toInt().toString() else quantity.toString()

                // 🔴 STRATEGI MULTIPART BARU:
                val imagePart = if (imageUri != null) {
                    // Skenario A: Kasir pilih foto baru -> Kirim berkas fisik hasil kompresi
                    prepareImagePart(context, imageUri)
                } else {
                    // Skenario B: Kasir gak ganti foto -> Kirim string URL foto lama dalam bentuk Part teks
                    // Trik ini membuat header tetap multipart/form-data, tetapi isinya string URL
                    val fallbackUrl = currentImageUrl ?: ""
                    MultipartBody.Part.createFormData("gambar", fallbackUrl)
                }

                if (imagePart == null) {
                    errorMessage = "Gagal memproses gambar"
                    isLoading = false
                    return@launch
                }

                // Eksekusi API menggunakan struktur Multipart murni yang disukai backend
                val response = RetrofitClient.instance.updateIngredient(
                    id = id,
                    method = MultipartBody.Part.createFormData("_method", "PUT"),
                    name = MultipartBody.Part.createFormData("nama", name),
                    unit = MultipartBody.Part.createFormData("unit", unit),
                    quantity = MultipartBody.Part.createFormData("stok", quantityStr),
                    gambar = imagePart // Aman, selalu terisi dan mematuhi validasi
                )

                if (response.success) {
                    fetchIngredients()
                } else {
                    errorMessage = response.message
                }
            } catch (e: retrofit2.HttpException) {
                val errorBodyJson = e.response()?.errorBody()?.string()
                Log.e("INVENTORI_ERROR", "HTTP Error: $errorBodyJson")
                errorMessage = "Gagal update data: $errorBodyJson"
            } catch (e: Exception) {
                Log.e("INVENTORI_ERROR", "Error: ${e.message}")
                errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }

    private fun prepareImagePart(context: Context, fileUri: Uri?): MultipartBody.Part? {
        if (fileUri == null) return null
        return try {
            val contentResolver = context.contentResolver
            val mimeType = contentResolver.getType(fileUri) ?: "image/jpeg"
            val ext = if (mimeType.contains("png")) "png" else "jpg"

            val file = File(context.cacheDir, "temp_upload_${System.currentTimeMillis()}.$ext")
            
            val inputStream: InputStream? = contentResolver.openInputStream(fileUri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            if (bitmap != null) {
                val outputStream = FileOutputStream(file)
                val format = if (ext == "png") Bitmap.CompressFormat.PNG else Bitmap.CompressFormat.JPEG
                bitmap.compress(format, 80, outputStream)
                outputStream.flush()
                outputStream.close()
                bitmap.recycle()
            } else {
                return null
            }

            val requestFile = file.asRequestBody(mimeType.toMediaTypeOrNull())
            val partName = "gambar.$ext"

            Log.d("INVENTORI_DEBUG", "Sending file: $partName, Mime: $mimeType, Size: ${file.length()} bytes")

            MultipartBody.Part.createFormData("gambar", partName, requestFile)
        } catch (e: Exception) {
            Log.e("INVENTORI_ERROR", "Gagal menyiapkan gambar: ${e.message}")
            null
        }
    }

    fun deleteIngredient(id: Long) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = RetrofitClient.instance.deleteIngredient(id)
                if (response.isSuccessful) {
                    fetchIngredients()
                } else {
                    errorMessage = "Gagal menghapus bahan"
                }
            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }
}
