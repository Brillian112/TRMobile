package com.example.tr.data.remote.model

import com.google.gson.annotations.SerializedName

data class Attendance(
    val id: Long,
    @SerializedName("user_id") val userId: Long,
    @SerializedName("attendance_date") val attendanceDate: String,
    @SerializedName("check_in_time") val checkInTime: String?,
    @SerializedName("check_out_time") val checkOutTime: String?,
    val status: String,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?,
    val user: User?
)

data class AttendanceRequest(
    @SerializedName("user_id") val userId: Long,
    val status: String
)

data class AttendanceResponse(
    val success: Boolean,
    val message: String?,
    val data: List<Attendance>
)

data class SingleAttendanceResponse(
    val success: Boolean,
    val message: String?,
    val data: Attendance
)
