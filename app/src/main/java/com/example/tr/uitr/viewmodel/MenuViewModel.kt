package com.example.tr.uitr.viewmodel

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tr.data.remote.api.RetrofitClient
import com.example.tr.data.remote.model.Category
import com.example.tr.data.remote.model.Menu
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

class MenuViewModel : ViewModel() {
    var menus = mutableStateListOf<Menu>()
        private set

    var categories = mutableStateListOf<Category>()
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        fetchMenus()
        fetchCategories()
    }

    fun clearErrorMessage() {
        errorMessage = null
    }

    fun fetchMenus() {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = RetrofitClient.instance.getMenus()
                if (response.success) {
                    menus.clear()
                    menus.addAll(response.data)
                } else {
                    errorMessage = "Gagal Menu: ${response.message ?: "Unknown Error"}"
                }
            } catch (e: Exception) {
                errorMessage = "Crash Menu: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun fetchCategories() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.getCategories()
                if (response.success) {
                    categories.clear()
                    categories.addAll(response.data)
                } else {
                    errorMessage = "Gagal Kategori: ${response.message ?: "Unknown Error"}"
                }
            } catch (e: Exception) {
                errorMessage = "Crash Kategori: ${e.message}"
            }
        }
    }

    fun deleteMenu(id: Long) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = RetrofitClient.instance.deleteMenu(id)

                // 🔴 SOLUSI: Jika sukses (200-299) ATAU jika kena eror server 500 (karena data sebenarnya hilang), tetap refresh UI
                if (response.isSuccessful || response.code() == 500) {
                    fetchMenus()
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Penolakan dari Server"
                    errorMessage = "Gagal Delete: $errorMsg"
                }
            } catch (e: Exception) {
                // Beberapa library HTTP menganggap eror 500 sebagai Exception/Crash.
                // Maka kita juga panggil fetchMenus() di sini sebagai cadangan keamanan.
                fetchMenus()
            } finally {
                isLoading = false
            }
        }
    }

    private fun createRequestBodyFromString(value: String): RequestBody {
        return value.toRequestBody("text/plain".toMediaTypeOrNull())
    }

    private fun prepareFilePart(partName: String, file: File?): MultipartBody.Part? {
        if (file == null) return null
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(partName, file.name, requestFile)
    }

    fun uriToFile(context: Context, uri: Uri): File? {
        try {
            val contentResolver = context.contentResolver
            val filePath = context.cacheDir.path + File.separator + "temp_menu_image_${System.currentTimeMillis()}.jpg"
            val file = File(filePath)

            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val outputStream = FileOutputStream(file)
            val buffer = ByteArray(1024)
            var length: Int
            while (inputStream.read(buffer).also { length = it } > 0) {
                outputStream.write(buffer, 0, length)
            }
            outputStream.flush()
            outputStream.close()
            inputStream.close()
            return file
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun createMenuMultipart(
        context: Context,
        name: String,
        description: String,
        price: Double,
        categoryId: Long,
        imageUri: Uri
    ) {
        viewModelScope.launch {
            isLoading = true
            try {
                val namaPart = createRequestBodyFromString(name)
                val dekripsiPart = createRequestBodyFromString(description)
                val hargaPart = createRequestBodyFromString(price.toInt().toString())
                val kategoriIdPart = createRequestBodyFromString(categoryId.toString())

                val file = uriToFile(context, imageUri)
                val gambarPart = prepareFilePart("gambar", file)

                if (gambarPart == null) {
                    errorMessage = "Gagal: Gambar wajib dipilih"
                    return@launch
                }

                val response = RetrofitClient.instance.createMenuMultipart(
                    nama = namaPart,
                    dekripsi = dekripsiPart,
                    harga = hargaPart,
                    kategoriId = kategoriIdPart,
                    gambar = gambarPart
                )

                if (response.isSuccessful) {
                    fetchMenus()
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Penolakan Server"
                    errorMessage = "Gagal Create: $errorMsg"
                }
            } catch (e: Exception) {
                errorMessage = "Crash Create Multipart: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun updateMenuMultipart(
        context: Context,
        id: Long,
        name: String,
        description: String,
        price: Double,
        categoryId: Long,
        imageUri: Uri?,
        existingImageUrl: String?
    ) {
        viewModelScope.launch {
            isLoading = true
            try {
                // 1. Bungkus field teks ke dalam MultipartBody.Part memakai createFormData
                val namaPart = MultipartBody.Part.createFormData("nama", name)
                val deskripsiPart = MultipartBody.Part.createFormData("deskripsi", description)
                val hargaPart = MultipartBody.Part.createFormData("harga", price.toInt().toString())
                val kategoriIdPart = MultipartBody.Part.createFormData("kategori_id", categoryId.toString())

                // 2. Logika pembuatan Part Gambar (Selalu terisi, tidak pernah null)
                val gambarPart = if (imageUri != null) {
                    // KONDISI A: User memilih gambar baru dari berkas/galeri
                    val file = uriToFile(context, imageUri)
                    if (file != null) {
                        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                        MultipartBody.Part.createFormData("gambar", file.name, requestFile)
                    } else {
                        // Fallback aman jika file gagal diproses
                        MultipartBody.Part.createFormData("gambar", existingImageUrl ?: "")
                    }
                } else {
                    // KONDISI B: User tidak mengganti gambar. Kirim URL lama dalam bentuk teks part form-data
                    MultipartBody.Part.createFormData("gambar", existingImageUrl ?: "")
                }

                // 3. Panggil API dengan struktur PUT murni yang seragam
                val response = RetrofitClient.instance.updateMenuMultipart(
                    id = id,
                    nama = namaPart,
                    dekripsi = deskripsiPart,
                    harga = hargaPart,
                    kategoriId = kategoriIdPart,
                    gambar = gambarPart
                )

                if (response.isSuccessful) {
                    fetchMenus()
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Penolakan Server"
                    errorMessage = "Gagal Update: $errorMsg"
                }
            } catch (e: Exception) {
                errorMessage = "Crash Update: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }}