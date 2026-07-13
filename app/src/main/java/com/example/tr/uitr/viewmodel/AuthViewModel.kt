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

    fun login(name: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = RetrofitClient.instance.login(LoginRequest(name, password))
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    user = loginResponse?.user
                    token = loginResponse?.token
                    
                    // Simpan token ke RetrofitClient agar API lain bisa menggunakannya
                    RetrofitClient.authToken = loginResponse?.token
                    
                    onSuccess()
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
