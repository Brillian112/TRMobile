package com.example.tr.data.model

data class KaryawanData(
    val id: String,
    val nama: String,
    val password : String,
    val peran: String,
    val status: String // "Aktif" atau "Nonaktif"
)