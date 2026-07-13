package com.example.tr.uitr.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tr.data.remote.api.RetrofitClient
import com.example.tr.data.remote.model.Transaction
import com.example.tr.data.remote.model.TransactionRequest
import kotlinx.coroutines.launch

class TransactionViewModel : ViewModel() {
    var transactions = mutableStateListOf<Transaction>()
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        fetchTransactions()
    }

    fun fetchTransactions() {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = RetrofitClient.instance.getTransactions()
                if (response.success) {
                    transactions.clear()
                    transactions.addAll(response.data)
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

    fun createTransaction(request: TransactionRequest) {
        viewModelScope.launch {
            try {
                RetrofitClient.instance.createTransaction(request)
                fetchTransactions()
            } catch (e: Exception) {
                errorMessage = e.message
            }
        }
    }

    fun updateStatus(id: Long, status: String) {
        viewModelScope.launch {
            try {
                RetrofitClient.instance.updateTransactionStatus(id, status)
                fetchTransactions()
            } catch (e: Exception) {
                errorMessage = e.message
            }
        }
    }
}
