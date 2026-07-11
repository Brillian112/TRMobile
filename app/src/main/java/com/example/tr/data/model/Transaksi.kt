package com.example.tr.data.model

data class Transaksi(
    val id: Long,                    // bigserial di database
    val userId: Long,                // Relasi ke tabel users (Kasir)
    val namaKasir: String,           // Diambil dari tabel users.name lewat join
    val subtotal: Double,            // decimal
    val taxAmount: Double,           // decimal (Pajak 10%)
    val totalAmount: Double,         // decimal
    val paymentMethod: String,       // text ("Tunai" / "QRIS")
    val status: String,              // text
    val transactionsDate: String,    // timestamp
    val items: List<TransactionDetail>, // Relasi ke tabel transaction_details
    // Tambahan untuk kebutuhan UI kembalian instan
    val uangTunaiDiterima: Double,
    val kembalian: Double = uangTunaiDiterima - totalAmount
)