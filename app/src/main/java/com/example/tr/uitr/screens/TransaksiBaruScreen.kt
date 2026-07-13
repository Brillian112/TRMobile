package com.example.tr.uitr.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Remove
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.tr.data.remote.model.Menu // Menggunakan model Menu dari remote
import com.example.tr.uitr.navigation.Screen
import com.example.tr.uitr.viewmodel.MenuViewModel
import com.example.tr.uitr.viewmodel.TransactionViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransaksiBaruScreen(
    navController: NavController,
    menuViewModel: MenuViewModel = viewModel(),
    transactionViewModel: TransactionViewModel
) {
    val utamaHijau = Color(0xFF0F6E52)
    val bgLight = Color(0xFFF8F9FA)

    var searchQuery by remember { mutableStateOf("") }
    var kategoriTerpilih by remember { mutableStateOf("Semua") }

    // State Lokal Keranjang Belanja: Map<ID_Menu (Long), Jumlah_Beli>
    val keranjangBelanja = remember { mutableStateMapOf<Long, Int>() }

    // Menggunakan variabel state asli dari MenuViewModel kamu
    val daftarMenu = menuViewModel.menus
    val daftarKategoriApi = menuViewModel.categories
    val isLoading = menuViewModel.isLoading

    // Trigger hit API untuk memastikan data segar saat masuk halaman
    LaunchedEffect(Unit) {
        menuViewModel.fetchMenus()
        menuViewModel.fetchCategories()
    }

    // Filter data berdasarkan search bar dan chip kategori
    val menuDisaring = daftarMenu.filter { itemMenu ->
        // Sesuaikan 'it.category' atau 'it.categoryId' tergantung struktur model Menu kamu
        // Di sini diasumsikan itemMenu memiliki properti kategori berbentuk String atau nama kategori objeknya
        val matchesKategori = kategoriTerpilih == "Semua" ||
                itemMenu.category?.name.equals(kategoriTerpilih, ignoreCase = true)


        val matchesSearch = itemMenu.name.contains(searchQuery, ignoreCase = true)

        matchesKategori && matchesSearch
    }

    // Hitung total item & harga secara otomatis dari keranjang belanja
    val totalItem = keranjangBelanja.values.sum()
    val totalHarga = keranjangBelanja.entries.sumOf { (id, jumlah) ->
        val menu = daftarMenu.find { it.id == id }
        // Asumsi harga di model Menu berupa Int atau Long
        ((menu?.price ?: 0).toInt()) * jumlah
    }

    Scaffold(
        containerColor = bgLight,
        bottomBar = {
            if (totalItem > 0) {
                BottomBayarBar(
                    navController = navController,
                    warnaTema = utamaHijau,
                    totalHarga = totalHarga,
                    totalItem = totalItem,
                    onProsesKlik = {
                        // 1. Simpan total harga belanja ke shared ViewModel
                        transactionViewModel.temporaryTotalAmount = totalHarga.toDouble()

                        // 2. Petakan isi keranjang lokal ke dalam bentuk List Map untuk payload API backend
                        transactionViewModel.temporarySelectedItems = keranjangBelanja.map { (menuId, qty) ->
                            val menu = daftarMenu.find { it.id == menuId }
                            val price = menu?.price ?: 0.0
                            mapOf(
                                "menu_id" to menuId,
                                "name" to (menu?.name ?: "Unknown"),
                                "quantity" to qty,
                                "price" to price,
                                "subtotal" to price * qty
                            )
                        }

                        // 3. Baru lakukan pindah halaman (navigasi)
                        navController.navigate(Screen.Pembayaran.route)
                    }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("KasirKu", fontWeight = FontWeight.Bold, color = Color.Gray, fontSize = 14.sp)
                Icon(Icons.Outlined.Notifications, contentDescription = null, tint = utamaHijau)
            }

            Text(text = "Transaksi Baru", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF333333))
            Text(text = "Pilih menu untuk ditambahkan ke pesanan", fontSize = 13.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(16.dp))

            // Search Bar
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

            // Bar Kategori menggunakan data asli dari API Kategori
            val kategoriList = remember(daftarKategoriApi) {
                listOf("Semua") + daftarKategoriApi.map { it.name }
            }

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(kategoriList) { kategori ->
                    val isSelected = kategoriTerpilih == kategori
                    FilterChip(
                        selected = isSelected,
                        onClick = { kategoriTerpilih = kategori },
                        label = { Text(kategori) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = utamaHijau,
                            selectedLabelColor = Color.White,
                            containerColor = Color.White,
                            labelColor = Color.Gray
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Grid List Menu
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize().weight(1f), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = utamaHijau)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize().weight(1f)
                ) {
                    items(menuDisaring) { itemMenu ->
                        val jumlahBeli = keranjangBelanja[itemMenu.id] ?: 0
                        ItemMenuCard(
                            itemMenu = itemMenu,
                            warnaTema = utamaHijau,
                            jumlahBeli = jumlahBeli,
                            onTambah = { keranjangBelanja[itemMenu.id] = jumlahBeli + 1 },
                            onKurang = {
                                if (jumlahBeli > 1) {
                                    keranjangBelanja[itemMenu.id] = jumlahBeli - 1
                                } else {
                                    keranjangBelanja.remove(itemMenu.id)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ItemMenuCard(
    itemMenu: Menu,
    warnaTema: Color,
    jumlahBeli: Int,
    onTambah: () -> Unit,
    onKurang: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Box(
                modifier = Modifier.fillMaxWidth().height(120.dp).background(Color(0xFFE9ECEF)),
                contentAlignment = Alignment.Center
            ) {
                Text("Gambar Menu", color = Color.Gray, fontSize = 12.sp)
            }

            Column(modifier = Modifier.padding(12.dp)) {
                Text(text = itemMenu.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 1, color = Color(0xFF333333))

                val formatHarga = NumberFormat.getNumberInstance(Locale("id", "ID")).format(itemMenu.price)
                Text(text = "Rp $formatHarga", fontSize = 12.sp, color = warnaTema, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 4.dp, bottom = 12.dp))

                if (jumlahBeli == 0) {
                    Button(
                        onClick = onTambah,
                        colors = ButtonDefaults.buttonColors(containerColor = warnaTema),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        Text("+ Tambah", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedIconButton(
                            onClick = onKurang,
                            modifier = Modifier.size(32.dp),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, warnaTema)
                        ) {
                            Icon(Icons.Filled.Remove, contentDescription = null, tint = warnaTema, modifier = Modifier.size(16.dp))
                        }

                        Text(text = jumlahBeli.toString(), fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF333333))

                        IconButton(
                            onClick = onTambah,
                            modifier = Modifier.size(32.dp).background(warnaTema, RoundedCornerShape(8.dp))
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BottomBayarBar(navController: NavController, warnaTema: Color, totalHarga: Int, totalItem: Int, onProsesKlik: () -> Unit) {
    Surface(color = Color.White, tonalElevation = 8.dp, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "$totalItem Item Terpilih", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF333333))
                val formatTotal = NumberFormat.getNumberInstance(Locale("id", "ID")).format(totalHarga)
                Text(text = "Total: Rp $formatTotal", fontSize = 16.sp, color = warnaTema, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = onProsesKlik,
                colors = ButtonDefaults.buttonColors(containerColor = warnaTema),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Proses Pembayaran", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}