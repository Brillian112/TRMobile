package com.example.tr.uitr.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tr.uitr.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardKasirScreen(navController: NavController) {
    val utamaHijau = Color(0xFF0F6E52) // Warna sesuai mockup kamu
    val bgLight = Color(0xFFF8F9FA)

    // State untuk Bottom Navigation yang aktif (0 = Beranda)
    var selectedItem by remember { mutableStateOf(0) }

    Scaffold(
        containerColor = bgLight,
        bottomBar = {
            // Cukup panggil seperti ini, beres!
            DashboardBottomNav(navController = navController, selectedItem = 0)
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

            // 2. BAGIAN HEADER (Profil & Notifikasi)
            item {
                HeaderSection(utamaHijau)
            }

            // 3. BAGIAN CARD SHIFT & KEHADIRAN
            item {
                ShiftCardSection(utamaHijau)
            }

            // 4. BAGIAN TOMBOL MENU UTAMA (Transaksi Baru & Riwayat)
            // Di dalam LazyColumn, pastikan saat memanggil MenuUtamaSection kodenya seperti ini:
            item {
                MenuUtamaSection(warnaTema = utamaHijau, navController = navController)
            }

            // Teks Label Ringkasan
            item {
                Text(
                    text = "Ringkasan Hari Ini",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // 5. BAGIAN RINGKASAN DATA (Transaksi & Total Penjualan)
            item {
                RingkasanItem(
                    judul = "Transaksi Hari Ini",
                    nilai = "12",
                    iconBackground = Color(0xFFEBE9F5),
                    iconTint = Color(0xFF6200EE),
                    icon = Icons.Outlined.ReceiptLong
                )
            }
            item {
                RingkasanItem(
                    judul = "Total Penjualan",
                    nilai = "Rp 1.250.000",
                    iconBackground = Color(0xFFFFF9E6),
                    iconTint = Color(0xFFFFA000),
                    icon = Icons.Outlined.AttachMoney
                )
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
fun HeaderSection(warnaTema: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Avatar bulat abu-abu sebagai placeholder foto
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.LightGray, CircleShape)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Halo, Andi!",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = warnaTema
            )
        }
        // Tombol Lonceng Notifikasi
        Icon(
            imageVector = Icons.Outlined.Notifications,
            contentDescription = "Notifikasi",
            tint = warnaTema,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun ShiftCardSection(warnaTema: Color) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Schedule, contentDescription = null, tint = warnaTema, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Shift Hari Ini", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.Gray)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text("08:00 - 16:00", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = warnaTema)
            Text("Senin, 24 Oktober 2023", fontSize = 12.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(16.dp))

            // Sub-container hijau untuk status kehadiran
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(warnaTema, RoundedCornerShape(12.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.HowToReg, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Status Kehadiran", color = Color.White, fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    // Badge Putih Transparan
                    Surface(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            text = "Sudah Hadir",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MenuUtamaSection(warnaTema: Color, navController: NavController) {
    Column {
        // 1. Card Transaksi Baru (Hijau Penuh)
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = warnaTema),
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    // Tambahkan baris print ini untuk tes apakah tombolnya benar-benar merespons klik
                    println("TES_KLIK: Tombol Transaksi Baru Berhasil Ditekan!")

                    // Perintah pindah halaman
                    navController.navigate(Screen.TransaksiBaru.route)
                }
        ) {
            Row(
                modifier = Modifier.padding(20.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                            .padding(6.dp)
                    ) {
                        Icon(Icons.Outlined.AddShoppingCart, contentDescription = null, tint = Color.White)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Buat Transaksi Baru", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("Mulai proses kasir untuk pelanggan", fontSize = 12.sp, color = Color.White.copy(alpha = 0.7f))
                }
                // Placeholder Ikon Mesin Kasir Besar di Kanan (Bisa menggunakan Icon bawaan)
                Icon(Icons.Outlined.PointOfSale, contentDescription = null, tint = Color.White.copy(alpha = 0.15f), modifier = Modifier.size(70.dp))
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 2. Card Lihat Riwayat (Putih ber-Border)
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, warnaTema),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { navController.navigate(Screen.TransaksiBaru.route) }
        ) {
            Row(
                modifier = Modifier.padding(20.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .background(warnaTema.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                        .padding(6.dp)
                ) {
                    Icon(Icons.Outlined.History, contentDescription = null, tint = warnaTema)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("Lihat Riwayat", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = warnaTema)
                    Text("Cek transaksi yang telah selesai", fontSize = 12.sp, color = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun RingkasanItem(
    judul: String,
    nilai: String,
    iconBackground: Color,
    iconTint: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(iconBackground, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconTint)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(judul, fontSize = 12.sp, color = Color.Gray)
                Text(nilai, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF333333))
            }
        }
    }
}

@Composable
fun DashboardBottomNav(navController: NavController, selectedItem: Int) { // <-- Tambahkan parameter navController di sini
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        // Nama menu dan ikonnya
        val items = listOf("Beranda", "Transaksi", "Absensi", "Profil")
        val icons = listOf(Icons.Filled.Home, Icons.Outlined.Receipt, Icons.Outlined.AssignmentInd, Icons.Outlined.Person)

        // Sesuaikan rute halaman dengan object yang ada di Screen.kt kamu
        val routes = listOf(
            Screen.DashboardKasir.route,  // Index 0: Beranda Kasir
            Screen.TransaksiBaru.route,   // Index 1: Transaksi Baru
            Screen.DashboardKasir.route,  // Index 2: Absensi (Arahkan sementara ke Beranda jika belum ada)
            Screen.DashboardKasir.route   // Index 3: Profil (Arahkan sementara ke Beranda jika belum ada)
        )

        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = { Icon(icons[index], contentDescription = item) },
                label = { Text(item, fontSize = 11.sp) },
                selected = selectedItem == index,
                onClick = {
                    // Logika berpindah halaman saat ikon navbar diklik
                    if (selectedItem != index) {
                        navController.navigate(routes[index]) {
                            // Agar ketika tombol back ditekan, tidak menumpuk halaman berulang kali
                            popUpTo(Screen.DashboardKasir.route) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    selectedTextColor = Color(0xFF0F6E52),
                    indicatorColor = Color(0xFF0F6E52), // Warna background bulat saat aktif
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray
                )
            )
        }
    }
}
