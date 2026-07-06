package com.example.tr.uitr.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tr.data.dummy.DummyDataSource
import com.example.tr.data.model.TransaksiData
import com.example.tr.ui.theme.BgLight
import com.example.tr.ui.theme.DarkNavy
import com.example.tr.ui.theme.TextGray
import com.example.tr.ui.theme.GreenBg
import com.example.tr.ui.theme.GreenText
import com.example.tr.ui.theme.RedBg
import com.example.tr.ui.theme.RedText
import java.text.NumberFormat
import java.util.Locale



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RiwayatTransaksiScreen(navController: NavController) {
    // STATE MANAGEMENT LOKAL
    val transaksiList = remember { mutableStateListOf(*DummyDataSource.dummyTransaksiList.toTypedArray()) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Hari Ini") }
    val filters = listOf("Hari Ini", "Minggu Ini", "Bulan Ini")

    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedTransaksi by remember { mutableStateOf<TransaksiData?>(null) }

    // Logika Filter (Berdasarkan rentang waktu dan pencarian ID invoice)
    val filteredList = transaksiList.filter {
        it.rentangWaktu == selectedFilter &&
                it.invoice.contains(searchQuery, ignoreCase = true)
    }

    // Kalkulasi Total Pendapatan dinamis (hanya yang statusnya "Selesai")
    val totalPendapatan = filteredList
        .filter { it.status == "Selesai" }
        .sumOf { it.totalHarga }

    Scaffold(
        containerColor = BgLight,
        topBar = { TopBarTransaksi() },
        bottomBar = { LaporanBottomNav(navController) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(4.dp)) }

            item {
                Text(
                    text = "Riwayat Transaksi",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkNavy
                )
            }

            // Card Total Pendapatan
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Total Pendapatan $selectedFilter",
                            color = TextGray,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Rp ${NumberFormat.getNumberInstance(Locale("id", "ID")).format(totalPendapatan)}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkNavy
                        )
                    }
                }
            }

            // Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Cari transaksi...", color = TextGray, fontSize = 14.sp) },
                    leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = "Search", tint = TextGray) },
                    shape = RoundedCornerShape(12.dp),
//                    colors = TextFieldDefaults.outlinedTextFieldColors(
//                        containerColor = Color.White,
//                        unfocusedBorderColor = Color(0xFFE2E8F0),
//                        focusedBorderColor = DarkNavy
//                    ),
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                )
            }

            // Filter Chips
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(filters) { filter ->
                        FilterChipTransaksi(
                            title = filter,
                            isSelected = filter == selectedFilter,
                            onClick = { selectedFilter = filter }
                        )
                    }
                }
            }

            // List Transaksi
            items(filteredList) { transaksi ->
                TransaksiCardItem(
                    transaksi = transaksi,
                    onDeleteClick = {
                        selectedTransaksi = transaksi
                        showDeleteDialog = true
                    }
                )
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }

    // Dialog Konfirmasi Hapus
    if (showDeleteDialog && selectedTransaksi != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus Transaksi", fontWeight = FontWeight.Bold) },
            text = { Text("Hapus data transaksi ${selectedTransaksi?.invoice}? Data ini akan hilang dari laporan.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        transaksiList.removeIf { it.id == selectedTransaksi?.id }
                        showDeleteDialog = false
                    }
                ) { Text("Hapus", color = Color.Red) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Batal") }
            }
        )
    }
}

@Composable
fun TopBarTransaksi() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(36.dp).clip(CircleShape).background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Person, contentDescription = "Profil", tint = Color.White, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text("KasirKu", color = DarkNavy, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        Icon(Icons.Outlined.Notifications, contentDescription = "Notifikasi", tint = DarkNavy)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterChipTransaksi(title: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) DarkNavy else Color.White,
        border = if (isSelected) null else BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Text(
            text = title,
            color = if (isSelected) Color.White else TextGray,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 13.sp,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
fun TransaksiCardItem(transaksi: TransaksiData, onDeleteClick: () -> Unit) {
    val isSelesai = transaksi.status == "Selesai"

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Card: Invoice, Status, & Tombol Hapus
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = transaksi.invoice, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = DarkNavy)
                    Spacer(modifier = Modifier.width(8.dp))

                    // Badge Status
                    Surface(
                        color = if (isSelesai) GreenBg else RedBg,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = transaksi.status,
                            color = if (isSelesai) GreenText else RedText,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                // Icon Hapus
                Icon(
                    imageVector = Icons.Outlined.DeleteOutline,
                    contentDescription = "Hapus",
                    tint = RedText,
                    modifier = Modifier.size(20.dp).clickable { onDeleteClick() }
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${transaksi.namaKasir} - ${transaksi.jumlahItem} items",
                color = TextGray,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // Footer Card: Total
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Total", color = TextGray, fontSize = 13.sp)

                Text(
                    text = "Rp ${NumberFormat.getNumberInstance(Locale("id", "ID")).format(transaksi.totalHarga)}",
                    color = if (isSelesai) GreenText else RedText,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    textDecoration = if (!isSelesai) TextDecoration.LineThrough else null // Efek coret jika dibatalkan
                )
            }
        }
    }
}

// Navigasi Bawah Khusus (Laporan Aktif)
@Composable
fun LaporanBottomNav(navController: NavController) {
    NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
        NavigationBarItem(
            icon = { Icon(Icons.Outlined.GridView, contentDescription = "Beranda") },
            label = { Text("Beranda") },
            selected = false,
            onClick = { navController.navigate("dashboard") },
            colors = NavigationBarItemDefaults.colors(unselectedIconColor = TextGray, unselectedTextColor = TextGray)
        )
        NavigationBarItem(
            icon = { Icon(Icons.Outlined.Inventory2, contentDescription = "Kelola") },
            label = { Text("Kelola") },
            selected = false,
            onClick = { navController.navigate("menu") },
            colors = NavigationBarItemDefaults.colors(unselectedIconColor = TextGray, unselectedTextColor = TextGray)
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Assessment, contentDescription = "Laporan") },
            label = { Text("Laporan", fontWeight = FontWeight.Bold) },
            selected = true,
            onClick = { /* Sudah di halaman Laporan/Transaksi */ },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = DarkNavy,
                selectedTextColor = DarkNavy,
                indicatorColor = Color(0xFFE2E8F0) // Background biru keabu-abuan terang
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Outlined.Person, contentDescription = "Profil") },
            label = { Text("Profil") },
            selected = false,
            onClick = { navController.navigate("profil") },
            colors = NavigationBarItemDefaults.colors(unselectedIconColor = TextGray, unselectedTextColor = TextGray)
        )
    }
}