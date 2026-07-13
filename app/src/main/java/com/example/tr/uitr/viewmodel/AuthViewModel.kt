package com.example.tr.uitr.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tr.data.remote.api.RetrofitClient
import com.example.tr.data.remote.model.LoginRequest
import com.example.tr.data.remote.model.User
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    var user by mutableStateOf<User?>(null)
        private set

    var token by mutableStateOf<String?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    // 1. Ubah parameter onSuccess agar bisa mengirimkan data User?
    fun login(name: String, password: String, onSuccess: (User?) -> Unit) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                // 1. Panggil API Login
                val response = RetrofitClient.instance.login(LoginRequest(name, password))

                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    val loggedInUser = loginResponse?.user // User ini belum ada role-nya

                    // Simpan token ke RetrofitClient agar API kedua bisa memakainya (Authorization: Bearer)
                    RetrofitClient.authToken = loginResponse?.token
                    token = loginResponse?.token

                    if (loggedInUser != null) {
                        try {
                            // 2. Lakukan API Chaining: Ambil data User lengkap berdasarkan ID
                            val userDetailResponse = RetrofitClient.instance.getUserById(loggedInUser.id)

                            // GANTI isSuccessful menjadi .success (Sesuai dengan nama variabel di data class SingleUserResponse kamu)
                            if (userDetailResponse.success) {

                                // GANTI .body()?.data menjadi langsung .data
                                val fullUser = userDetailResponse.data

                                user = fullUser // Simpan ke state
                                onSuccess(fullUser) // Kirim user yang LENGKAP ke LoginScreen
                            } else {
                                // Jika API kedua gagal (misal success: false), kembalikan user seadanya (fallback)
                                user = loggedInUser
                                onSuccess(loggedInUser)
                            }
                        } catch (e: Exception) {
                            // Jika koneksi putus saat memanggil API kedua (fallback)
                            user = loggedInUser
                            onSuccess(loggedInUser)
                        }
                    } else {
                        onSuccess(null)
                    }
                } else {
                    errorMessage = "Login gagal: ${response.code()}"
                }
            } catch (e: Exception) {
                errorMessage = "Terjadi kesalahan: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun logout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                RetrofitClient.instance.logout()
            } catch (e: Exception) {
                // Ignore error
            } finally {
                user = null
                token = null
                RetrofitClient.authToken = null // Hapus token saat logout
                onSuccess()
            }
        }
    }
}
