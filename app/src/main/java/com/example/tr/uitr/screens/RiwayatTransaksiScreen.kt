package com.example.tr.uitr.screens

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.tr.uitr.components.AppDrawer
import com.example.tr.uitr.components.StatusBadge
import com.example.tr.uitr.navigation.Screen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tr.data.remote.model.Transaction
import com.example.tr.uitr.viewmodel.TransactionViewModel
import com.example.tr.ui.theme.BgLight
import com.example.tr.ui.theme.DarkNavy
import com.example.tr.ui.theme.TextGray
import com.example.tr.ui.theme.GreenBg
import com.example.tr.ui.theme.GreenText
import com.example.tr.ui.theme.RedBg
import com.example.tr.ui.theme.RedText
import com.example.tr.ui.theme.YellowBg
import com.example.tr.ui.theme.YellowText
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RiwayatTransaksiScreen(navController: NavController, viewModel: TransactionViewModel = viewModel()) {
    // STATE MANAGEMENT FROM VIEWMODEL
    val transactions = viewModel.transactions
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Semua") }
    val filters = listOf("Semua", "Pending", "completed", "Batal")

    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedTransaction by remember { mutableStateOf<Transaction?>(null) }

    // Logika Filter (Berdasarkan status dan pencarian ID)
    val filteredList = transactions.filter {
        val matchesFilter = when (selectedFilter) {
            "Semua" -> true
            "completed" -> it.status.equals("completed", ignoreCase = true) || it.status.equals("Berhasil", ignoreCase = true)
            else -> it.status.equals(selectedFilter, ignoreCase = true)
        }
        val matchesSearch = it.id.toString().contains(searchQuery, ignoreCase = true) || (it.user?.name?.contains(searchQuery, ignoreCase = true) ?: false)
        matchesFilter && matchesSearch
    }

    // Kalkulasi Total Pendapatan dari SEMUA transaksi yang sudah selesai (bukan hanya yang difilter)
    val totalPendapatan = transactions
        .filter { it.status.equals("completed", ignoreCase = true) || it.status.equals("Berhasil", ignoreCase = true) }
        .sumOf { it.totalAmount }

    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawer(
                navController = navController,
                currentRoute = currentRoute,
                onCloseDrawer = { scope.launch { drawerState.close() } }
            )
        }
    ) {
        Scaffold(
            containerColor = BgLight,
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Riwayat Transaksi", fontWeight = FontWeight.Bold, color = DarkNavy) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Menu", tint = DarkNavy)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = BgLight)
                )
            }
        ) { paddingValues ->
            if (viewModel.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item { Spacer(modifier = Modifier.height(4.dp)) }

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
                                    text = "Total Pendapatan ($selectedFilter)",
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
                            placeholder = { Text("Cari transaksi (ID / Nama)...", color = TextGray, fontSize = 14.sp) },
                            leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = "Search", tint = TextGray) },
                            shape = RoundedCornerShape(12.dp),
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
                    items(filteredList) { transaction ->
                        TransactionCardItem(
                            transaction = transaction,
                            onUpdateStatus = { newStatus ->
                                viewModel.updateStatus(transaction.id, newStatus)
                            }
                        )
                    }

                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
        }
    }
}

@Composable
fun TopBarTransaksi() {
    // Deprecated in favor of Scaffold TopBar
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
fun TransactionCardItem(transaction: Transaction, onUpdateStatus: (String) -> Unit) {
    val status = transaction.status.lowercase()
    val isSelesai = status == "completed" || status == "berhasil"
    val isPending = status == "pending"

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Card: ID, Status, & Aksi
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "TX-${transaction.id}", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = DarkNavy)
                    Spacer(modifier = Modifier.width(8.dp))

                    // Badge Status
                    StatusBadge(status = transaction.status)
                }

                if (isPending) {
                    Row {
                        IconButton(
                            onClick = { 
                                // update database ke status completed
                                onUpdateStatus("completed")
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Filled.Check, contentDescription = "Selesaikan", tint = GreenText)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = { onUpdateStatus("Batal") },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Filled.Close, contentDescription = "Batalkan", tint = RedText)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${transaction.user?.name ?: "Unknown"} - ${transaction.paymentMethod}",
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
                    text = "Rp ${NumberFormat.getNumberInstance(Locale("id", "ID")).format(transaction.totalAmount)}",
                    color = if (isSelesai) GreenText else if (transaction.status.equals("Batal", ignoreCase = true)) RedText else DarkNavy,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    textDecoration = if (transaction.status.equals("Batal", ignoreCase = true)) TextDecoration.LineThrough else null
                )
            }
        }
    }
}
