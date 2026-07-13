package com.example.tr.data.model

data class AttendanceData(
    val id: Long,
    val userId: Long,
    val attendanceDate: String,
    val checkInTime: String,
    val checkOutTime: String,
    val status: String // Contoh: "Hadir", "Terlambat", "Absen"
)