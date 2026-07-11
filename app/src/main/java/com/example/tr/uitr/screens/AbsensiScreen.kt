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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tr.data.model.AttendanceData

@Composable
fun AbsensiScreen(
    navController: NavController,
    listAbsensi: List<AttendanceData> // Menggunakan model data resmi dari folder model
) {
    val utamaHijau = Color(0xFF0F6E52)
    val merahTombol = Color(0xFFC62828)
    val bgLight = Color(0xFFF8F9FA)

    Scaffold(
        containerColor = bgLight
    ) { paddingValues ->
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
                            Text("23 Okt 2023", color = Color.Gray, fontSize = 14.sp)
                            BadgeStatusAbsen(status = "Hadir")
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Jam Masuk", fontSize = 12.sp, color = Color.Gray)
                                Text("07:45", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            }

                            // Pembatas Vertikal
                            Box(modifier = Modifier.width(1.dp).height(40.dp).background(Color(0xFFE0E0E0)))

                            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Jam Keluar", fontSize = 12.sp, color = Color.Gray)
                                Text("--:--", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = { /* Aksi Absen Keluar */ },
                            colors = ButtonDefaults.buttonColors(containerColor = merahTombol),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().height(46.dp)
                        ) {
                            Text("Absen Keluar", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 4. SUBJUDUL RIWAYAT
                Text(text = "Riwayat Absensi", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            }

            // 5. LIST DAFTAR RIWAYAT ABSENSI (Looping Data Asli)
            items(listAbsensi) { riwayat ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
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
                                text = "Masuk: ${riwayat.checkInTime} - Keluar: ${riwayat.checkOutTime}",
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

// Komponen Helper Internal untuk Badge Status Kontainer
@Composable
fun BadgeStatusAbsen(status: String) {
    val isHadir = status == "Hadir"
    val bgBadge = if (isHadir) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
    val textBadge = if (isHadir) Color(0xFF2E7D32) else Color(0xFFC62828)

    Box(
        modifier = Modifier
            .background(color = bgBadge, shape = RoundedCornerShape(50.dp))
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Text(
            text = status,
            color = textBadge,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}