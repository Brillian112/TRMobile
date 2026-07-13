package com.example.tr.uitr.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tr.data.remote.api.RetrofitClient
import com.example.tr.data.remote.model.Attendance
import com.example.tr.data.remote.model.AttendanceRequest
import kotlinx.coroutines.launch

class AttendanceViewModel : ViewModel() {
    var attendances = mutableStateListOf<Attendance>()
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        fetchAttendances()
    }

    fun fetchAttendances() {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = RetrofitClient.instance.getAttendances()
                if (response.success) {
                    attendances.clear()
                    attendances.addAll(response.data)
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

    fun submitAttendance(userId: Long, status: String) {
        viewModelScope.launch {
            try {
                RetrofitClient.instance.createAttendance(AttendanceRequest(userId, status))
                fetchAttendances()
            } catch (e: Exception) {
                errorMessage = e.message
            }
        }
    }

    // Di dalam AttendanceViewModel.kt kamu:

    fun checkIn() {
        viewModelScope.launch {
            isLoading = true
            try {
                // 1. Dapatkan jam saat ini dengan format murni HH:mm:ss (Contoh: "16:25:00")
                val jamMurni = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())

                // 2. Bungkus ke dalam Map sesuai dengan Key yang diminta backend (misal: "check_in_time")
                // Hubungi teman backend untuk memastikan nama key-nya ("check_in_time" atau "checkInTime")
                val requestBody = mapOf("check_in_time" to jamMurni)

                // 3. Kirim data ke API
                val response = RetrofitClient.instance.checkIn(requestBody)

                if (response.isSuccessful) {
                    fetchAttendances() // Refresh data UI jika berhasil
                } else {
                    android.util.Log.e("KASIRKU_API_ERROR", "Gagal: ${response.code()} | ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                android.util.Log.e("KASIRKU_API_ERROR", "Exception: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }

    fun checkOut() {
        viewModelScope.launch {
            isLoading = true
            try {
                // 1. Dapatkan jam saat ini dengan format murni HH:mm:ss (Contoh: "17:05:12")
                val jamMurni = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())

                // 2. Bungkus ke dalam Map sesuai dengan Key yang diminta backend untuk checkout
                // Pastikan nama key-nya (apakah "check_out_time" atau "checkOutTime") cocok dengan backend
                val requestBody = mapOf("check_out_time" to jamMurni)

                // 3. Kirim data ke API PUT /api/attendances/check-out
                val response = RetrofitClient.instance.checkOut(requestBody)

                if (response.isSuccessful) {
                    fetchAttendances() // Refresh list data di UI agar Jam Keluar langsung terisi
                } else {
                    android.util.Log.e("KASIRKU_API_ERROR", "Check-out Gagal: ${response.code()} | ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                android.util.Log.e("KASIRKU_API_ERROR", "Exception saat Check-out: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }

}
