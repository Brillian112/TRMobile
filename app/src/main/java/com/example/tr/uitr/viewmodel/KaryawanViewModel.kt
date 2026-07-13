package com.example.tr.uitr.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tr.data.remote.api.RetrofitClient
import com.example.tr.data.remote.model.RegisterRequest
import com.example.tr.data.remote.model.User
import kotlinx.coroutines.launch

class KaryawanViewModel : ViewModel() {
    var employees = mutableStateListOf<User>()
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        fetchEmployees()
    }

    fun fetchEmployees() {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = RetrofitClient.instance.getManagerEmployees()
                if (response.success) {
                    employees.clear()
                    employees.addAll(response.data)
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

    fun createEmployee(name: String, password: String, role: String) {
        viewModelScope.launch {
            try {
                RetrofitClient.instance.createUser(RegisterRequest(name, password, role))
                fetchEmployees()
            } catch (e: Exception) {
                errorMessage = e.message
            }
        }
    }

    fun updateEmployee(id: Long, name: String, password: String, role: String) {
        viewModelScope.launch {
            try {
                RetrofitClient.instance.updateUser(id, RegisterRequest(name, password, role))
                fetchEmployees()
            } catch (e: Exception) {
                errorMessage = e.message
            }
        }
    }

    fun deleteEmployee(id: Long) {
        viewModelScope.launch {
            try {
                RetrofitClient.instance.deleteUser(id)
                fetchEmployees()
            } catch (e: Exception) {
                errorMessage = e.message
            }
        }
    }
}
