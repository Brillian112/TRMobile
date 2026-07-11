package com.example.tr.uitr.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tr.data.model.Transaksi

@Composable
fun RiwayatTransaksiScreen(
    navController: NavController,
    listTransaksi: List<Transaksi> // Oper list transaksi dari dummy data atau DB
) {
    val utamaHijau = Color(0xFF0F6E52)
    val hijauMuda = Color(0xFFA7F3D0)
    val bgLight = Color(0xFFF9FAFB)

    // State untuk memantau filter waktu yang aktif
    var filterAktif by remember { mutableStateOf("Hari Ini") }

    Scaffold(
        containerColor = bgLight
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))

                // 1. TOP PROFILE HEADER
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        // Foto Profil (Ganti dengan icon atau drawable-mu jika ada)
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color.LightGray, shape = CircleShape)
                        )
                        Text(
                            text = "KasirKu",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = utamaHijau
                        )
                    }
                    IconButton(onClick = { /* Aksi Notifikasi */ }) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notifikasi", tint = Color.DarkGray)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 2. JUDUL HALAMAN
                Text(
                    text = "Riwayat Saya",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937)
                )
                Text(
                    text = "Daftar transaksi yang diproses oleh Andi.",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 3. FILTER TAB (Hari Ini, Minggu Ini, Bulan Ini)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val opsiFilter = listOf("Hari Ini", "Minggu Ini", "Bulan Ini")
                    opsiFilter.forEach { opsi ->
                        val isSelected = filterAktif == opsi
                        Box(
                            modifier = Modifier
                                .background(
                                    color = if (isSelected) hijauMuda else Color.White,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { filterAktif = opsi }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = opsi,
                                color = if (isSelected) utamaHijau else Color.DarkGray,
                                fontWeight = FontWeight.Medium,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 4. RINGKASAN DATA (TOTAL TRANSAKSI & PENDAPATAN)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Card Total Transaksi
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Total Transaksi", color = Color.Gray, fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("12", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                    }

                    // Sisi kanan kosong menyamakan space grid di mockup gambar
                    Spacer(modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Card Total Pendapatan
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Total Pendapatan (Hari Ini)", color = Color.Gray, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Rp 1.500.000", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = utamaHijau)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            // 5. DAFTAR NOTA TRANSAKSI (LIST ITEM)
            items(listTransaksi) { transaksi ->
                ItemRiwayatCard(transaksi = transaksi)
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

// Komponen Card per Item Transaksi
@Composable
fun ItemRiwayatCard(transaksi: Transaksi) {
    val utamaHijau = Color(0xFF0F6E52)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                // Kotak Icon Nota Abu-Abu
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color(0xFFF3F4F6), shape = RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("📄", fontSize = 18.sp)
                }

                // Info Invoice & Detail
                Column {
                    Text(
                        text = "#INV-${transaksi.id}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${transaksi.transactionsDate.split(",")[1].trim().take(5)} WIB • Pelanggan Umum",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            // Harga Sisi Kanan & Badge Status
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "Rp ${String.format("%,2f", transaksi.totalAmount).split(",")[0].replace(",", ".")}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(6.dp))
                // Badge Selesai
                Box(
                    modifier = Modifier
                        .background(Color(0xFFE0E7FF), shape = RoundedCornerShape(50.dp)) // Ungu soft muda
                        .padding(horizontal = 10.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "Selesai",
                        color = Color(0xFF6366F1), // Teks ungu
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}