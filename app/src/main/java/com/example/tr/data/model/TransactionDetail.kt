package com.example.tr.data.model

data class TransactionDetail(
    val id: Long,               // bigserial
    val menuId: Long,           // bigint (Relasi ke tabel menus)
    val menuName: String,       // Diambil dari tabel menus.name
    val transactionId: Long,    // bigint
    val quantity: Int,          // int
    val price: Double,          // decimal
    val subtotal: Double        // decimal (quantity * price)
)