package com.example.tr.data.model

data class InventoriData(
    val id: String,
    val nama: String,
    val jumlah: Double,
    val satuan: String, // misal: "kg", "liter", "pcs"
    val status: String  // "Cukup", "Rendah", "Habis"
)