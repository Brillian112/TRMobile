package com.example.tr.uitr.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
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
import com.example.tr.uitr.navigation.Screen
import com.example.tr.uitr.viewmodel.AuthViewModel
import com.example.tr.uitr.viewmodel.TransactionViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PembayaranScreen(
    navController: NavController,
    transactionViewModel: TransactionViewModel,
    authViewModel: AuthViewModel = viewModel()
) {
    val utamaHijau = Color(0xFF0F6E52)
    val bgLight = Color(0xFFF8F9FA)
    val textUtama = Color(0xFF0F6E52)

    val currentUser = authViewModel.user
    val userId = currentUser?.id ?: 0L

    // 1. Ambil Total Tagihan secara dinamis dari kalkulasi transaksi sebelumnya
    // totalTagihan adalah jumlah yang harus dibayar (sudah termasuk pajak 10%)
    val subtotalBeforeTax = transactionViewModel.temporaryTotalAmount
    val taxAmount = subtotalBeforeTax * 0.1
    val totalTagihan = subtotalBeforeTax + taxAmount
    
    val totalTagihanLong = totalTagihan.toLong()
    val subtotal = subtotalBeforeTax // Kirim subtotal asli ke API

    var uangDiterimaText by remember { mutableStateOf("") }
    val isLoading = transactionViewModel.isLoading

    // Konversi text input numpad ke angka Long (agar aman untuk nominal besar)
    val uangDiterima = uangDiterimaText.toLongOrNull() ?: 0L
    val kembalian = if (uangDiterima >= totalTagihan) uangDiterima - totalTagihan else 0.0

    val formatTotalTagihan = NumberFormat.getNumberInstance(Locale("id", "ID")).format(totalTagihan)
    val formatKembalian = NumberFormat.getNumberInstance(Locale("id", "ID")).format(kembalian)

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
            Box(modifier = Modifier.padding(16.dp)) {
                Button(
                    onClick = {
                        // 2. AKSI REAL HIT API POST TRANSACTION
                        transactionViewModel.createTransaction(
                            userId = userId,
                            subtotal = subtotal,
                            taxAmount = taxAmount,
                            totalAmount = totalTagihan,
                            paymentMethod = "cash",
                            items = transactionViewModel.temporarySelectedItems
                        ) {
                            // Pindah ke halaman Sukses Transaksi
                            navController.navigate(Screen.PembayaranSukses.route + "/$uangDiterima") {
                                popUpTo(Screen.Pembayaran.route) { inclusive = true }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = utamaHijau,
                        disabledContainerColor = Color.LightGray
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = uangDiterima >= totalTagihan && !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Selesaikan Transaksi", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // CARD INFORMASI TAGIHAN (Dinamis dari API/Keranjang)
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
                        Text("Rp $formatTotalTagihan", color = textUtama, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    }
                    HorizontalDivider(color = Color(0xFFECEFF1))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Pelanggan", color = Color.Gray, fontSize = 14.sp)
                        Text("Umum (-)", color = Color.DarkGray, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Metode Pembayaran", color = Color.Gray, fontSize = 14.sp)
                        Text("Tunai", color = textUtama, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }

            // INPUTAN UANG DITERIMA
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Uang Diterima", color = Color(0xFFFFA000), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, utamaHijau, RoundedCornerShape(8.dp))
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Rp", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Gray)

                        val formatInputUang = NumberFormat.getNumberInstance(Locale("id", "ID")).format(uangDiterima)
                        Text(
                            text = if (uangDiterimaText.isEmpty()) "0" else formatInputUang,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Quick Cash Buttons
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        ButtonPilihanUang("Rp 25.000", modifier = Modifier.weight(1f), isSelected = uangDiterimaText == "25000", warnaTema = utamaHijau) { uangDiterimaText = "25000" }
                        ButtonPilihanUang("Rp 50.000", modifier = Modifier.weight(1f), isSelected = uangDiterimaText == "50000", warnaTema = utamaHijau) { uangDiterimaText = "50000" }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        ButtonPilihanUang("Rp 100.000", modifier = Modifier.weight(1f), isSelected = uangDiterimaText == "100000", warnaTema = utamaHijau) { uangDiterimaText = "100000" }
                        val nominalPas = totalTagihanLong.toString()
                        ButtonPilihanUang("Uang Pas", modifier = Modifier.weight(1f), isSelected = uangDiterimaText == nominalPas, warnaTema = utamaHijau) { 
                            uangDiterimaText = nominalPas 
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // CUSTOM KEYPAD
                    val tombolNumpad = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "000", "backspace")
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth().height(220.dp)
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
                                                    if (uangDiterimaText.length < 9) {
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

            // DISPLAY KEMBALIAN
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("KEMBALIAN", color = utamaHijau, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Rp $formatKembalian", color = utamaHijau, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

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