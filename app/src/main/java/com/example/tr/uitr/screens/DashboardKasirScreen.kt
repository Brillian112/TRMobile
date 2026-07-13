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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.tr.uitr.navigation.Screen

// Import ViewModel (Sesuaikan package jika berbeda)
import com.example.tr.uitr.viewmodel.AuthViewModel
import com.example.tr.uitr.viewmodel.TransactionViewModel
import com.example.tr.uitr.viewmodel.AttendanceViewModel

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class BottomNavItem(
    val title: String,
    val route: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector
)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardKasirScreen(
    navController: NavController,
    authViewModel: AuthViewModel, // Menerima shared ViewModel
    transactionViewModel: TransactionViewModel,
    attendanceViewModel: AttendanceViewModel
) {
    val utamaHijau = Color(0xFF0F6E52)
    val bgLight = Color(0xFFF8F9FA)

    // Ambil Nama User yang sedang login dari shared ViewModel
    val namaKasir = authViewModel.user?.name ?: "Kasir"

    LaunchedEffect(Unit) {
        transactionViewModel.fetchTransactions()
        attendanceViewModel.fetchAttendances()
    }

    val todayDateObj = Date()
    val apiDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val displayDateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID"))

    val todayApiString = apiDateFormat.format(todayDateObj)
    val todayDisplayString = displayDateFormat.format(todayDateObj)

    // Kalkulasi SEMUA Transaksi Sukses
    val semuaTransaksiSukses = transactionViewModel.transactions.filter {
        it.status.equals("completed", ignoreCase = true) || it.status.equals("berhasil", ignoreCase = true)
    }
    val jumlahTransaksi = semuaTransaksiSukses.size
    val totalPenjualan = semuaTransaksiSukses.sumOf { it.totalAmount }

    // Kalkulasi Kehadiran Hari Ini
    val kehadiranHariIni = attendanceViewModel.attendances.find {
        it.attendanceDate?.startsWith(todayApiString) == true || it.createdAt?.startsWith(todayApiString) == true
    }

    val checkIn = kehadiranHariIni?.checkInTime ?: "--:--"
    val checkOut = kehadiranHariIni?.checkOutTime ?: "--:--"
    val statusKehadiran = kehadiranHariIni?.status ?: "Belum Presensi"

    // Daftar menu navigasi bawah
    val items = listOf(
        BottomNavItem("Home", Screen.DashboardKasir.route, Icons.Outlined.Home, Icons.Filled.Home),
        BottomNavItem("Transaksi", Screen.TransaksiBaru.route, Icons.Outlined.ReceiptLong, Icons.Filled.ReceiptLong),
//        BottomNavItem("Inventori", Screen.Inventori.route, Icons.Outlined.Inventory, Icons.Filled.Inventory),
        BottomNavItem("Absensi", Screen.Absensi.route, Icons.Outlined.Settings, Icons.Filled.Settings),
        BottomNavItem("Profil", Screen.Profil.route, Icons.Outlined.Person, Icons.Filled.Person)
    )

    // Mendapatkan rute saat ini untuk indikator pilihan
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(containerColor = bgLight,
        // --- TAMBAHKAN BOTTOM BAR DI SINI ---
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                items.forEach { item ->
                    val isSelected = currentRoute == item.route
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            if (!isSelected) {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        label = { Text(item.title, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) item.selectedIcon else item.icon,
                                contentDescription = item.title
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = utamaHijau,
                            selectedTextColor = utamaHijau,
                            indicatorColor = utamaHijau.copy(alpha = 0.2f) // Warna latar belakang indikator
                        )
                    )
                }
            }
        }) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }
            item { HeaderSection(warnaTema = utamaHijau, namaKasir = namaKasir) }
            item { ShiftCardSection(utamaHijau, todayDisplayString, "$checkIn - $checkOut", statusKehadiran) }
            item { MenuUtamaSection(utamaHijau, navController) }

            item {
                Text("Ringkasan Keseluruhan", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF333333), modifier = Modifier.padding(top = 8.dp))
            }

            item {
                RingkasanItem("Total Transaksi", jumlahTransaksi.toString(), Color(0xFFEBE9F5), Color(0xFF6200EE), Icons.Outlined.ReceiptLong)
            }
            item {
                val formatRupiah = NumberFormat.getNumberInstance(Locale("id", "ID")).format(totalPenjualan)
                RingkasanItem("Total Penjualan", "Rp $formatRupiah", Color(0xFFFFF9E6), Color(0xFFFFA000), Icons.Outlined.AttachMoney)
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
fun HeaderSection(warnaTema: Color, namaKasir: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.LightGray, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Person, contentDescription = "Profil", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(12.dp))
            // Menampilkan nama dinamis
            Text(
                text = "Halo, $namaKasir!",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = warnaTema
            )
        }
        Icon(
            imageVector = Icons.Outlined.Notifications,
            contentDescription = "Notifikasi",
            tint = warnaTema,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun ShiftCardSection(warnaTema: Color, tanggal: String, waktuShift: String, status: String) {
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
            // Menampilkan Waktu dan Tanggal Dinamis
            Text(waktuShift, fontSize = 26.sp, fontWeight = FontWeight.Bold, color = warnaTema)
            Text(tanggal, fontSize = 12.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(16.dp))

            // Mengubah warna background badge tergantung status
            val statusColor = if (status.contains("Hadir", ignoreCase = true)) warnaTema else Color(0xFFD32F2F)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(statusColor, RoundedCornerShape(12.dp))
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
                    Surface(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        // Menampilkan status dinamis
                        Text(
                            text = status,
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

// ... (MenuUtamaSection dan RingkasanItem tetap sama persis seperti yang kamu buat sebelumnya)
@Composable
fun MenuUtamaSection(warnaTema: Color, navController: NavController) {
    Column {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = warnaTema),
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    println("TES_KLIK: Tombol Transaksi Baru Berhasil Ditekan!")
//                    navController.navigate(Screen.TransaksiBaru.route)
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
                Icon(Icons.Outlined.PointOfSale, contentDescription = null, tint = Color.White.copy(alpha = 0.15f), modifier = Modifier.size(70.dp))
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, warnaTema),
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
//                    navController.navigate(Screen.Riwayat.route)
                }
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