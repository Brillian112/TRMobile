package com.example.tr.uitr.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Notifications
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

// 1. Model Data untuk Menu
data class MenuItem(
    val id: Int,
    val nama: String,
    val harga: Int,
    val kategori: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransaksiBaruScreen(navController: NavController) {
    val utamaHijau = Color(0xFF0F6E52) // Warna hijau utama kasir
    val bgLight = Color(0xFFF8F9FA)

    var searchQuery by remember { mutableStateOf("") }
    var kategoriTerpilih by remember { mutableStateOf("Semua") }

    // Dummy Data Menu sesuai gambar mockup
    val daftarMenu = listOf(
        MenuItem(1, "Nasi Goreng Spesial", 25000, "Makanan"),
        MenuItem(2, "Es Kopi Susu Gula Aren", 18000, "Minuman"),
        MenuItem(3, "Kentang Goreng", 15000, "Snack"),
        MenuItem(4, "Mie Kuah Spesial", 22000, "Makanan")
    )

    // Filter data berdasarkan search bar dan chip kategori
    val menuDisaring = daftarMenu.filter {
        (kategoriTerpilih == "Semua" || it.kategori == kategoriTerpilih) &&
                it.nama.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        containerColor = bgLight,
        bottomBar = {
            // Summary belanja bagian bawah (Total harga & Tombol proses)
            BottomBayarBar(navController = navController,warnaTema = utamaHijau, totalHarga = 25000, totalItem = 1)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            // 2. Bagian Atas: Nama Aplikasi & Notifikasi
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("KasirKu", fontWeight = FontWeight.Bold, color = Color.Gray, fontSize = 14.sp)
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = null,
                    tint = utamaHijau
                )
            }

            Text(
                text = "Transaksi Baru",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF333333)
            )
            Text(
                text = "Pilih menu untuk ditambahkan ke pesanan",
                fontSize = 13.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Cari menu...", color = Color.Gray) },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = Color.Gray) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = utamaHijau,
                    unfocusedBorderColor = Color.LightGray
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 4. Bar Kategori (Chips)
            val daftarKategori = listOf("Semua", "Makanan", "Minuman", "Snack")
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(daftarKategori) { kategori ->
                    val isSelected = kategoriTerpilih == kategori
                    FilterChip(
                        selected = isSelected,
                        onClick = { kategoriTerpilih = kategori },
                        label = { Text(kategori) },
                        colors = FilterChipDefaults.filterChipColors(
                            // Menentukan warna saat chip dipilih (Aktif)
                            selectedContainerColor = utamaHijau,
                            selectedLabelColor = Color.White,

                            // Menentukan warna saat chip tidak dipilih (Default)
                            containerColor = Color.White,
                            labelColor = Color.Gray
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 5. Grid Menu Makanan/Minuman (2 Kolom)
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(menuDisaring) { itemMenu ->
                    ItemMenuCard(itemMenu = itemMenu, warnaTema = utamaHijau)
                }
            }
        }
    }
}

// 6. Komponen Card Item Menu
@Composable
fun ItemMenuCard(itemMenu: MenuItem, warnaTema: Color) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            // Gambar makanan (Placeholder abu-abu)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(Color(0xFFE9ECEF)),
                contentAlignment = Alignment.Center
            ) {
                Text("Gambar Menu", color = Color.Gray, fontSize = 12.sp)
            }

            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = itemMenu.nama,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    color = Color(0xFF333333)
                )
                Text(
                    text = "Rp ${itemMenu.harga}",
                    fontSize = 12.sp,
                    color = warnaTema,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                )

                // Tombol Tambah Hijau
                Button(
                    onClick = { /* TODO: Aksi Masuk Keranjang Belanja */ },
                    colors = ButtonDefaults.buttonColors(containerColor = warnaTema),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    Text("+ Tambah", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// 7. Komponen Bottom Bar Ringkasan & Pembayaran
// 1. TAMBAHKAN PARAMETER 'navController: NavController' DI SINI
@Composable
fun BottomBayarBar(navController: NavController, warnaTema: Color, totalHarga: Int, totalItem: Int) {
    Surface(
        color = Color.White,
        tonalElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "$totalItem Item",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333)
                )
                Text(
                    text = "Total: Rp $totalHarga",
                    fontSize = 13.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
            }

            // Tombol Utama Pembayaran Hijau
            Button(
                // 2. PERBAIKAN DI SINI: ganti NavController.Navigate menjadi navController.navigate
                onClick = { navController.navigate(Screen.Pembayaran.route) },
                colors = ButtonDefaults.buttonColors(containerColor = warnaTema),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Proses Pembayaran", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}