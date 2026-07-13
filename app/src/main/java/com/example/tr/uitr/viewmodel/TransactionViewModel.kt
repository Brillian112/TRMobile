package com.example.tr.uitr.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tr.data.remote.api.RetrofitClient
import com.example.tr.data.remote.model.Transaction
import com.example.tr.data.remote.model.TransactionDetailRequest
import com.example.tr.data.remote.model.TransactionRequest
import kotlinx.coroutines.launch

class TransactionViewModel : ViewModel() {
    var transactions = mutableStateListOf<Transaction>()
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    // Untuk menampung total harga belanja sementara
    var temporaryTotalAmount by mutableStateOf(0.0)

    // Untuk menampung daftar item belanjaan sementara (menu_id dan quantity)
    var temporarySelectedItems by mutableStateOf<List<Map<String, Any>>>(emptyList())

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

    fun createTransaction(
        userId: Long,
        subtotal: Double,
        taxAmount: Double,
        totalAmount: Double,
        paymentMethod: String,
        items: List<Map<String, Any>>,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null // Reset error sebelum mulai
            try {
                val details = items.map {
                    TransactionDetailRequest(
                        menuId = it["menu_id"] as Long,
                        quantity = it["quantity"] as Int,
                        price = (it["price"] as? Number)?.toDouble() ?: 0.0,
                        subtotal = (it["subtotal"] as? Number)?.toDouble() ?: 0.0
                    )
                }

                val request = TransactionRequest(
                    userId = userId,
                    subtotal = subtotal,
                    taxAmount = taxAmount,
                    totalAmount = totalAmount,
                    paymentMethod = paymentMethod,
                    status = "completed",
                    details = details
                )

                val response = RetrofitClient.instance.createTransaction(request)

                // Cek kondisi sukses sesuai dengan struktur MenuViewModel kamu (response.success)
                if (response.success) {
                    android.util.Log.d("KASIRKU_TRANSAKSI", "Transaksi Berhasil Disimpan!")
                    onSuccess() // 🔴 Ini yang memicu perpindahan halaman di UI!
                } else {
                    // Jika server merespons tapi memberikan success = false
                    android.util.Log.e("KASIRKU_TRANSAKSI", "Gagal dari Server: ${response.message}")
                }
            } catch (e: Exception) {
                // Jika koneksi internet putus, endpoint salah, atau aplikasi crash
                android.util.Log.e("KASIRKU_TRANSAKSI", "Exception Terjadi: ${e.message}", e)
            } finally {
                isLoading = false
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
