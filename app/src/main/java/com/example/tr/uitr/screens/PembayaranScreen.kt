package com.example.tr.uitr.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PembayaranScreen(
    navController: NavController,
    // Callback untuk melempar nominal uang diterima dan kembalian ke NavHost/ViewModel
    onTransaksiSelesai: (Int, Int) -> Unit
) {
    val utamaHijau = Color(0xFF0F6E52)
    val bgLight = Color(0xFFF8F9FA)
    val textUtama = Color(0xFF0F6E52)

    val totalTagihan = 24200
    var uangDiterimaText by remember { mutableStateOf("") }

    // Konversi text input numpad ke angka Int
    val uangDiterima = uangDiterimaText.toIntOrNull() ?: 0
    val kembalian = if (uangDiterima >= totalTagihan) uangDiterima - totalTagihan else 0

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Proses Pembayaran", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = textUtama) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = textUtama)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = bgLight,
        bottomBar = {
            // Tombol Selesaikan Transaksi di paling bawah
            Box(modifier = Modifier.padding(16.dp)) {
                Button(
                    onClick = {
                        // Memanggil callback navigasi sambil membawa data nominal
                        onTransaksiSelesai(uangDiterima, kembalian)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = utamaHijau,
                        disabledContainerColor = Color.LightGray
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    // Tombol hanya aktif jika uang yang dimasukkan sudah cukup
                    enabled = uangDiterima >= totalTagihan
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Selesaikan Transaksi", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // 1. CARD INFORMASI TAGIHAN
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Total Tagihan", color = Color.Gray, fontSize = 14.sp)
                        Text("Rp $totalTagihan", color = textUtama, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    }
                    HorizontalDivider(color = Color(0xFFECEFF1))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Pelangan", color = Color.Gray, fontSize = 14.sp)
                        Text("Umum (-)", color = Color.DarkGray, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Metode Pembayaran", color = Color.Gray, fontSize = 14.sp)
                        Text("Tunai", color = textUtama, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }

            // 2. INPUTAN UANG DITERIMA
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Uang Diterima", color = Color(0xFFFFA000), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    // Kotak display nominal uang masuk
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, utamaHijau, RoundedCornerShape(8.dp))
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Rp", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        Text(
                            text = if (uangDiterimaText.isEmpty()) "0" else uangDiterimaText,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Quick Cash Buttons (Uang Pas, 25k, 50k, 100k)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        ButtonPilihanUang("Rp 25.000", modifier = Modifier.weight(1f)) { uangDiterimaText = "25000" }
                        ButtonPilihanUang("Rp 50.000", modifier = Modifier.weight(1f), isSelected = true, warnaTema = utamaHijau) { uangDiterimaText = "50000" }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        ButtonPilihanUang("Rp 100.000", modifier = Modifier.weight(1f)) { uangDiterimaText = "100000" }
                        ButtonPilihanUang("Uang Pas", modifier = Modifier.weight(1f)) { uangDiterimaText = totalTagihan.toString() }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 3. CUSTOM KEYPAD / NUMPAD
                    val tombolNumpad = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "000", "backspace")
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                    ) {
                        items(tombolNumpad) { label ->
                            Box(
                                modifier = Modifier
                                    .height(48.dp)
                                    .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(8.dp))
                                    .clickable {
                                        when (label) {
                                            "backspace" -> {
                                                if (uangDiterimaText.isNotEmpty()) {
                                                    uangDiterimaText = uangDiterimaText.dropLast(1)
                                                }
                                            }
                                            "000" -> {
                                                if (uangDiterimaText.isNotEmpty() && uangDiterimaText != "0") {
                                                    uangDiterimaText += "000"
                                                }
                                            }
                                            else -> {
                                                if (uangDiterimaText == "0") {
                                                    uangDiterimaText = label
                                                } else {
                                                    if (uangDiterimaText.length < 9) { // Proteksi digit berlebih
                                                        uangDiterimaText += label
                                                    }
                                                }
                                            }
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                if (label == "backspace") {
                                    Text("⌫", color = Color(0xFFC62828), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                } else {
                                    Text(text = label, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color.DarkGray)
                                }
                            }
                        }
                    }
                }
            }

            // 4. DISPLAY KEMBALIAN
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)), // Background hijau muda transparan
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("KEMBALIAN", color = utamaHijau, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Rp $kembalian", color = utamaHijau, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// Komponen Helper untuk Tombol Rekomendasi Nominal Uang
@Composable
fun ButtonPilihanUang(
    text: String,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    warnaTema: Color = Color.Gray,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(40.dp)
            .background(
                color = if (isSelected) warnaTema else Color(0xFFF1F3F5),
                shape = RoundedCornerShape(8.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isSelected) Color.White else Color.DarkGray,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
    }
}