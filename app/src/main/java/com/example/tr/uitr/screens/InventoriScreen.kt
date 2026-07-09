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
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tr.data.dummy.DummyDataSource
import com.example.tr.data.model.InventoriData
import com.example.tr.ui.theme.GreenBg
import com.example.tr.ui.theme.GreenText
import com.example.tr.ui.theme.RedBg
import com.example.tr.ui.theme.RedText
import com.example.tr.ui.theme.YellowBg
import com.example.tr.ui.theme.YellowText


import java.util.UUID

// Warna Khusus Layar Inventori
private val DarkNavy = Color(0xFF1A365D) // Sedikit lebih gelap menyesuaikan desain
private val BgLight = Color(0xFFF8F9FE)
private val TextGray = Color(0xFF7A869A)

// Warna Badge Status


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoriScreen(navController: NavController) {
    // STATE LOKAL
    val inventoriList = remember { mutableStateListOf(*DummyDataSource.dummyInventoriList.toTypedArray()) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Semua") }
    val filters = listOf("Semua", "Stok Rendah", "Habis")

    // State untuk Dialog Tambah/Edit
    var showFormDialog by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf<InventoriData?>(null) }

    // Logika Filter
    val filteredList = inventoriList.filter { item ->
        val matchFilter = when (selectedFilter) {
            "Stok Rendah" -> item.status == "Rendah"
            "Habis" -> item.status == "Habis"
            else -> true
        }
        matchFilter && item.nama.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        containerColor = BgLight,
        topBar = { TopBarInventori() },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    selectedItem = null // Kosongkan untuk tambah baru
                    showFormDialog = true
                },
                containerColor = DarkNavy,
                contentColor = Color.White,
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Tambah")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Tambah Item", fontWeight = FontWeight.Bold)
                }
            }
        },
        bottomBar = { KelolaBottomNav(navController) } // Menggunakan navigasi "Kelola" aktif dari file KelolaMenuScreen
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(4.dp)) }

            // Header Teks (opsional, jika ingin persis gambar bisa di-skip karena judul ada di TopBar)

            // Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Cari item...", color = TextGray) },
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
                        FilterChipInventori(
                            title = filter,
                            isSelected = filter == selectedFilter,
                            onClick = { selectedFilter = filter }
                        )
                    }
                }
            }

            // List Item Inventori
            items(filteredList) { inventori ->
                InventoriCardItem(
                    inventori = inventori,
                    onEditClick = {
                        selectedItem = inventori
                        showFormDialog = true
                    }
                )
            }

            item { Spacer(modifier = Modifier.height(80.dp)) } // Hindari tumpang tindih dengan FAB
        }
    }

    // Dialog Tambah / Edit Form
    if (showFormDialog) {
        FormInventoriDialog(
            item = selectedItem,
            onDismiss = { showFormDialog = false },
            onSave = { nama, jumlah, satuan ->
                // Logika status otomatis berdasarkan stok
                val status = when {
                    jumlah <= 0.0 -> "Habis"
                    jumlah <= 5.0 -> "Rendah"
                    else -> "Cukup"
                }

                if (selectedItem == null) {
                    // Tambah Baru
                    inventoriList.add(InventoriData(UUID.randomUUID().toString(), nama, jumlah, satuan, status))
                } else {
                    // Update
                    val index = inventoriList.indexOfFirst { it.id == selectedItem!!.id }
                    if (index != -1) {
                        inventoriList[index] = selectedItem!!.copy(nama = nama, jumlah = jumlah, satuan = satuan, status = status)
                    }
                }
                showFormDialog = false
            }
        )
    }
}

@Composable
fun TopBarInventori() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(36.dp).clip(CircleShape).background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) { Icon(Icons.Filled.Person, contentDescription = "Profil", tint = Color.White, modifier = Modifier.size(20.dp)) }
            Spacer(modifier = Modifier.width(12.dp))
            Text("Inventori", color = DarkNavy, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
        Icon(Icons.Outlined.Notifications, contentDescription = "Notifikasi", tint = DarkNavy)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterChipInventori(title: String, isSelected: Boolean, onClick: () -> Unit) {
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
fun InventoriCardItem(inventori: InventoriData, onEditClick: () -> Unit) {
    val isHabis = inventori.status == "Habis"
    val isRendah = inventori.status == "Rendah"

    val badgeBg = when {
        isHabis -> RedBg
        isRendah -> YellowBg
        else -> GreenBg
    }
    val badgeText = when {
        isHabis -> RedText
        isRendah -> YellowText
        else -> GreenText
    }

    // Format jumlah: hilangkan desimal ".0" jika angkanya bulat (misal 15.0 jadi 15)
    val formattedJumlah = if (inventori.jumlah % 1.0 == 0.0) {
        inventori.jumlah.toInt().toString()
    } else {
        inventori.jumlah.toString()
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, if (isHabis) RedBg else Color.Transparent), // Efek border merah tipis jika habis seperti di desain
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            // Konten Utama
            Column {
                // Placeholder Gambar Bulat/Kotak
                Box(
                    modifier = Modifier.size(48.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFFE2E8F0)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.Inventory, contentDescription = "Image", tint = TextGray)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(text = inventori.nama, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DarkNavy)
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "$formattedJumlah ${inventori.satuan}",
                    fontSize = 13.sp,
                    color = if (isHabis) RedText else TextGray,
                    fontWeight = if (isHabis) FontWeight.Bold else FontWeight.Normal
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Badge Status dengan Titik (Dot)
                Surface(
                    color = badgeBg,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(badgeText))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = inventori.status,
                            color = badgeText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Tombol Edit di Pojok Kanan Atas
            Icon(
                imageVector = Icons.Outlined.Edit,
                contentDescription = "Edit",
                tint = DarkNavy,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(20.dp)
                    .clickable { onEditClick() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormInventoriDialog(
    item: InventoriData?,
    onDismiss: () -> Unit,
    onSave: (String, Double, String) -> Unit
) {
    var nama by remember { mutableStateOf(item?.nama ?: "") }
    var jumlahInput by remember { mutableStateOf(item?.jumlah?.toString() ?: "") }
    var satuan by remember { mutableStateOf(item?.satuan ?: "kg") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (item == null) "Tambah Item" else "Edit Item", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = nama,
                    onValueChange = { nama = it },
                    label = { Text("Nama Item") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = jumlahInput,
                        onValueChange = { jumlahInput = it },
                        label = { Text("Jumlah") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = satuan,
                        onValueChange = { satuan = it },
                        label = { Text("Satuan") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val jumlah = jumlahInput.toDoubleOrNull() ?: 0.0
                    if (nama.isNotBlank()) onSave(nama, jumlah, satuan)
                },
                colors = ButtonDefaults.buttonColors(containerColor = DarkNavy)
            ) { Text("Simpan") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}