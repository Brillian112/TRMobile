package com.example.tr.data.remote.model

import com.google.gson.annotations.SerializedName

data class Transaction(
    val id: Long,
    @SerializedName("user_id") val userId: Long,
    val subtotal: Double,
    @SerializedName("tax_amount") val taxAmount: Double,
    @SerializedName("total_amount") val totalAmount: Double,
    @SerializedName("payment_method") val paymentMethod: String,
    val status: String,
    @SerializedName("transactions_date") val transactionsDate: String,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?,
    val user: User?,
    val details: List<TransactionDetail>?
)

data class TransactionDetail(
    val id: Long,
    @SerializedName("menu_id") val menuId: Long,
    @SerializedName("transaction_id") val transactionId: Long,
    val quantity: Int,
    val price: Double,
    val subtotal: Double,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?,
    val menu: Menu?
)

data class TransactionRequest(
    @SerializedName("user_id") val userId: Long,
    val subtotal: Double,
    @SerializedName("tax_amount") val taxAmount: Double,
    @SerializedName("total_amount") val totalAmount: Double,
    @SerializedName("payment_method") val paymentMethod: String,
    val status: String,
    val details: List<TransactionDetailRequest>
)

data class TransactionDetailRequest(
    @SerializedName("menu_id") val menuId: Long,
    val quantity: Int,
    val price: Double,
    val subtotal: Double
)

data class TransactionResponse(
    val success: Boolean,
    val message: String?,
    val data: List<Transaction>
)

data class SingleTransactionResponse(
    val success: Boolean,
    val message: String?,
    val data: Transaction
)
