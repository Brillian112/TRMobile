package com.example.tr.data.model

data class TransaksiData(
    val id: String,
    val invoice: String,
    val status: String, // "Selesai" atau "Dibatalkan"
    val namaKasir: String,
    val jumlahItem: Int,
    val totalHarga: Long,
    val rentangWaktu: String // "Hari Ini", "Minggu Ini", atau "Bulan Ini" untuk keperluan filter dummy
)