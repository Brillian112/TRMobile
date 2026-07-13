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
}
