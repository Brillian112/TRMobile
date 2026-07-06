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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tr.data.dummy.DummyDataSource
import com.example.tr.data.model.KaryawanData
import com.example.tr.ui.theme.BgLight
import com.example.tr.ui.theme.DarkText
import com.example.tr.ui.theme.PrimaryBlue
import com.example.tr.ui.theme.PurplePrimary
import com.example.tr.ui.theme.TextGray
import java.util.UUID

// Menggunakan warna yang sama dari layar menu


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KelolaKaryawanScreen(navController: NavController) {
    // STATE MANAGEMENT LOKAL (Agar fungsi tambah, edit, hapus berfungsi)
    val karyawanList = remember { mutableStateListOf(*DummyDataSource.dummyKaryawanList.toTypedArray()) }

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Semua") }
    val categories = listOf("Semua", "Aktif", "Nonaktif")

    // State untuk Dialog Tambah/Edit/Hapus
    var showFormDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedKaryawan by remember { mutableStateOf<KaryawanData?>(null) }

    Scaffold(
        containerColor = BgLight,
        topBar = { TopBarKaryawan() },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    selectedKaryawan = null // Kosongkan state untuk Tambah baru
                    showFormDialog = true
                },
                containerColor = PurplePrimary,
                contentColor = Color.White,
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Tambah")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Tambah Karyawan", fontWeight = FontWeight.Bold)
                }
            }
        },
        bottomBar = { KelolaBottomNav(navController) } // Gunakan BottomNav dari file sebelumnya
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
                    text = "Kelola Karyawan",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkText
                )
            }

            // Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Cari karyawan...", color = TextGray) },
                    leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = "Search", tint = TextGray) },
                    shape = RoundedCornerShape(12.dp),
//                    colors = TextFieldDefaults.outlinedTextFieldColors(
//                        containerColor = Color.White,
//                        unfocusedBorderColor = Color(0xFFE2E8F0),
//                        focusedBorderColor = PurplePrimary
//                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Filter Chips
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(categories) { category ->
                        CategoryChip( // Gunakan komponen dari KelolaMenuScreen
                            title = category,
                            isSelected = category == selectedCategory,
                            onClick = { selectedCategory = category }
                        )
                    }
                }
            }

            // List Karyawan (Difilter berdasarkan kategori dan pencarian)
            val filteredList = karyawanList.filter {
                (selectedCategory == "Semua" || it.status == selectedCategory) &&
                        it.nama.contains(searchQuery, ignoreCase = true)
            }

            items(filteredList) { karyawan ->
                KaryawanCardItem(
                    karyawan = karyawan,
                    onEditClick = {
                        selectedKaryawan = karyawan
                        showFormDialog = true
                    },
                    onDeleteClick = {
                        selectedKaryawan = karyawan
                        showDeleteDialog = true
                    }
                )
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }

    // --- LOGIKA DIALOG (POP-UP) ---

    // Dialog Tambah/Edit
    if (showFormDialog) {
        FormKaryawanDialog(
            karyawan = selectedKaryawan,
            onDismiss = { showFormDialog = false },
            onSave = { nama, peran, status ->
                if (selectedKaryawan == null) {
                    // Tambah Karyawan Baru
                    karyawanList.add(
                        KaryawanData(id = UUID.randomUUID().toString(), nama = nama, peran = peran, status = status)
                    )
                } else {
                    // Update Karyawan Lama
                    val index = karyawanList.indexOfFirst { it.id == selectedKaryawan!!.id }
                    if (index != -1) {
                        karyawanList[index] = selectedKaryawan!!.copy(nama = nama, peran = peran, status = status)
                    }
                }
                showFormDialog = false
            }
        )
    }

    // Dialog Konfirmasi Hapus
    if (showDeleteDialog && selectedKaryawan != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Konfirmasi Hapus") },
            text = { Text("Apakah Anda yakin ingin menghapus karyawan ${selectedKaryawan?.nama}?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        karyawanList.removeIf { it.id == selectedKaryawan?.id }
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
fun TopBarKaryawan() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Person, contentDescription = "Profil", tint = Color.White, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text("KasirKu", color = PurplePrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        Icon(Icons.Outlined.Notifications, contentDescription = "Notifikasi", tint = DarkText)
    }
}

@Composable
fun KaryawanCardItem(
    karyawan: KaryawanData,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    // Generate warna avatar berdasarkan inisial (Contoh sederhana)
    val avatarColor = if (karyawan.nama.startsWith("A", true)) Color(0xFFDCD6F7) else Color(0xFFD4B95E)
    val avatarTextColor = if (karyawan.nama.startsWith("A", true)) PurplePrimary else Color.White

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Avatar Inisial
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(avatarColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = karyawan.nama.take(1).uppercase(),
                            color = avatarTextColor,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(karyawan.nama, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DarkText)
                        Text(karyawan.peran, fontSize = 12.sp, color = TextGray)
                    }
                }

                // Badge Status
                val isAktif = karyawan.status == "Aktif"
                Surface(
                    color = if (isAktif) Color(0xFFE6F4EA) else Color(0xFFFCE8E6),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = if (isAktif) Icons.Filled.CheckCircle else Icons.Filled.Cancel,
                            contentDescription = null,
                            tint = if (isAktif) Color(0xFF1E8E3E) else Color(0xFFD93025),
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = karyawan.status,
                            color = if (isAktif) Color(0xFF1E8E3E) else Color(0xFFD93025),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = BgLight, thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons (Edit & Hapus)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.clickable { onEditClick() },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.Edit, contentDescription = "Edit", tint = PrimaryBlue, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Edit", color = PrimaryBlue, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
                Spacer(modifier = Modifier.width(24.dp))
                Row(
                    modifier = Modifier.clickable { onDeleteClick() },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.Delete, contentDescription = "Hapus", tint = Color(0xFFD93025), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Hapus", color = Color(0xFFD93025), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

// Dialog Form Input (Untuk Tambah dan Edit)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormKaryawanDialog(
    karyawan: KaryawanData?, // Jika null berarti "Tambah", jika ada isi berarti "Edit"
    onDismiss: () -> Unit,
    onSave: (String, String, String) -> Unit
) {
    var nama by remember { mutableStateOf(karyawan?.nama ?: "") }
    var peran by remember { mutableStateOf(karyawan?.peran ?: "Kasir") }
    var status by remember { mutableStateOf(karyawan?.status ?: "Aktif") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (karyawan == null) "Tambah Karyawan" else "Edit Karyawan", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = nama,
                    onValueChange = { nama = it },
                    label = { Text("Nama Karyawan") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = peran,
                    onValueChange = { peran = it },
                    label = { Text("Peran") },
                    modifier = Modifier.fillMaxWidth()
                )
                // Sederhana: Toggle Status menggunakan tombol
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Status: ", modifier = Modifier.weight(1f))
                    TextButton(onClick = { status = "Aktif" }, modifier = Modifier.weight(1f)) {
                        Text("Aktif", color = if (status == "Aktif") PurplePrimary else Color.Gray)
                    }
                    TextButton(onClick = { status = "Nonaktif" }, modifier = Modifier.weight(1f)) {
                        Text("Nonaktif", color = if (status == "Nonaktif") Color.Red else Color.Gray)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { if (nama.isNotBlank()) onSave(nama, peran, status) },
                colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary)
            ) { Text("Simpan") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}