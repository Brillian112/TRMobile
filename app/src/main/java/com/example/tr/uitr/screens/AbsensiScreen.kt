package com.example.tr.uitr.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tr.uitr.viewmodel.AttendanceViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AbsensiScreen(
    navController: NavController,
    attendanceViewModel: AttendanceViewModel // Menggunakan shared/injected ViewModel
) {
    val utamaHijau = Color(0xFF0F6E52)
    val merahTombol = Color(0xFFC62828)
    val bgLight = Color(0xFFF8F9FA)

    val listAbsensi = attendanceViewModel.attendances
    val isLoading = attendanceViewModel.isLoading

    // Format Tanggal Hari Ini untuk Tampilan Card Utama
    val todayDisplay = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID")).format(Date())
    val todayIsoDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    // Ambil data absensi hari ini jika ada di dalam list dari server
    val absensiHariIni = listAbsensi.find {
        it.attendanceDate.startsWith(todayIsoDate)
    }

    // Logika State Tombol dan Teks Jam
    val sudahCheckIn = absensiHariIni != null
    // Cek apakah checkOutTime null, kosong, atau hanya berisi spasi (" ")
    val sudahCheckOut = sudahCheckIn && !absensiHariIni?.checkOutTime.isNullOrBlank() && absensiHariIni?.checkOutTime != " "

    val jamMasuk = if (sudahCheckIn) formatJamIso(absensiHariIni?.checkInTime) else "--:--"
    val jamKeluar = if (sudahCheckOut) formatJamIso(absensiHariIni?.checkOutTime) else "--:--"
    val statusHariIni = absensiHariIni?.status ?: "Belum Presensi"

    // Trigger pemanggilan list data saat layar pertama kali dibuka
    LaunchedEffect(Unit) {
        attendanceViewModel.fetchAttendances()
    }

    Scaffold(
        containerColor = bgLight
    ) { paddingValues ->
        if (isLoading && listAbsensi.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = utamaHijau)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))

                    // 1. TOP HEADER PROFILE
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(modifier = Modifier.size(40.dp).background(Color.LightGray, shape = CircleShape))
                            Text("KasirKu", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = utamaHijau)
                        }
                        IconButton(onClick = { /* Aksi Notifikasi */ }) {
                            Icon(Icons.Default.Notifications, contentDescription = null, tint = Color.DarkGray)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 2. JUDUL HALAMAN
                    Text(text = "Absensi Saya", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Spacer(modifier = Modifier.height(16.dp))

                    // 3. CARD UTAMA ABSENSI HARI INI
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(todayDisplay, color = Color.Gray, fontSize = 14.sp)
                                BadgeStatusAbsen(status = statusHariIni)
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Jam Masuk", fontSize = 12.sp, color = Color.Gray)
                                    Text(jamMasuk, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                }

                                // Pembatas Vertikal
                                Box(modifier = Modifier.width(1.dp).height(40.dp).background(Color(0xFFE0E0E0)))

                                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Jam Keluar", fontSize = 12.sp, color = Color.Gray)
                                    Text(jamKeluar, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = if (sudahCheckOut) Color.Black else Color.Gray)
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            // Logika Tombol Dinamis
                            Button(
                                onClick = {
                                    if (!sudahCheckIn) {
                                        attendanceViewModel.checkIn()
                                    } else if (!sudahCheckOut) {
                                        attendanceViewModel.checkOut()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (!sudahCheckIn) utamaHijau else merahTombol
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().height(46.dp),
                                enabled = !sudahCheckOut && !isLoading // Kunci tombol jika shift hari ini selesai
                            ) {
                                val textTombol = when {
                                    sudahCheckOut -> "Sudah Selesai Shift"
                                    sudahCheckIn -> "Absen Keluar"
                                    else -> "Absen Masuk"
                                }
                                Text(textTombol, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // 4. SUBJUDUL RIWAYAT
                    Text(text = "Riwayat Absensi", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }

                // 5. LIST DAFTAR RIWAYAT ABSENSI
                items(listAbsensi) { riwayat ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(text = riwayat.attendanceDate, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Black)
                                Text(
                                    text = "Masuk: ${formatJamIso(riwayat.checkInTime)} - Keluar: ${formatJamIso(riwayat.checkOutTime)}",
                                    fontSize = 13.sp,
                                    color = Color.Gray
                                )
                            }
                            BadgeStatusAbsen(status = riwayat.status)
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun BadgeStatusAbsen(status: String) {
    val normalized = status.lowercase()
    val isHadir = normalized == "hadir" || normalized == "success"
    val bgBadge = if (isHadir) Color(0xFFE8F5E9) else if (normalized == "belum presensi") Color(0xFFF1F5F9) else Color(0xFFFFEBEE)
    val textBadge = if (isHadir) Color(0xFF2E7D32) else if (normalized == "belum presensi") Color.Gray else Color(0xFFC62828)

    Box(
        modifier = Modifier
            .background(color = bgBadge, shape = RoundedCornerShape(50.dp))
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Text(text = status, color = textBadge, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

// Fungsi Helper mengubah Waktu ISO (2026-07-13T09:04:35.715Z) menjadi format Jam (09:04)
fun formatJamIso(isoString: String?): String {
    if (isoString.isNullOrBlank() || isoString == " ") return "--:--"
    return try {
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
        val date = parser.parse(isoString)
        if (date != null) formatter.format(date) else "--:--"
    } catch (e: Exception) {
        // Jika format string dari server bukan ISO melainkan jam biasa (fallback)
        isoString.take(5)
    }
}