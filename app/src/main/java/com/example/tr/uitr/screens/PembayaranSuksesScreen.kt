package com.example.tr.uitr.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tr.uitr.navigation.Screen
import com.example.tr.uitr.viewmodel.TransactionViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PembayaranSuksesScreen(
    navController: NavController,
    transactionViewModel: TransactionViewModel,
    uangDiterima: Double
) {
    val utamaHijau = Color(0xFF0F6E52)
    val bgLight = Color(0xFFF8F9FA)
    
    // Data dari ViewModel (Asumsi transaksi terakhir yang baru dibuat)
    val totalTagihan = transactionViewModel.temporaryTotalAmount + (transactionViewModel.temporaryTotalAmount * 0.1)
    val items = transactionViewModel.temporarySelectedItems
    val kembalian = if (uangDiterima >= totalTagihan) uangDiterima - totalTagihan else 0.0
    
    val formatRupiah = NumberFormat.getNumberInstance(Locale("id", "ID"))
    val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))
    val tanggal = sdf.format(Date())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgLight)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // Icon Success
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(utamaHijau, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Filled.Check,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(48.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Pembayaran Berhasil",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF333333)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Receipt Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "KasirKu",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = utamaHijau
                )
                Text(
                    "Jl. Sudirman No. 123, Jakarta",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Text(
                    "Telp: 021-555-1234",
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), thickness = 1.dp, color = Color(0xFFEEEEEE))

                // Detail Transaksi
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("No. Transaksi", fontSize = 12.sp, color = Color.Gray)
                    Text("#INV-${System.currentTimeMillis().toString().takeLast(4)}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Tanggal", fontSize = 12.sp, color = Color.Gray)
                    Text(tanggal, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Kasir", fontSize = 12.sp, color = Color.Gray)
                    Text("Admin", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 1.dp, color = Color(0xFFEEEEEE))

                // Items
                items.forEach { item ->
                    val name = item["name"] as? String ?: "Menu Item"
                    val qty = item["quantity"] as? Int ?: 0
                    val price = (item["price"] as? Number)?.toDouble() ?: 0.0
                    val sub = (item["subtotal"] as? Number)?.toDouble() ?: 0.0

                    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(name, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            Text("Rp ${formatRupiah.format(sub)}", fontSize = 14.sp)
                        }
                        Text("$qty x Rp ${formatRupiah.format(price)}", fontSize = 12.sp, color = Color.Gray)
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 1.dp, color = Color(0xFFEEEEEE))

                // Calculation
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Subtotal", fontSize = 14.sp, color = Color.Gray)
                    Text("Rp ${formatRupiah.format(transactionViewModel.temporaryTotalAmount)}", fontSize = 14.sp)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Pajak (10%)", fontSize = 14.sp, color = Color.Gray)
                    Text("Rp ${formatRupiah.format(transactionViewModel.temporaryTotalAmount * 0.1)}", fontSize = 14.sp)
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("Rp ${formatRupiah.format(totalTagihan)}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = utamaHijau)
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), thickness = 1.dp, color = Color(0xFFEEEEEE))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Tunai", fontSize = 14.sp, color = Color.Gray)
                    Text("Rp ${formatRupiah.format(uangDiterima)}", fontSize = 14.sp)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Kembalian", fontSize = 14.sp, color = utamaHijau, fontWeight = FontWeight.Bold)
                    Text("Rp ${formatRupiah.format(kembalian)}", fontSize = 14.sp, color = utamaHijau, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Buttons
        Button(
            onClick = {
                navController.navigate(Screen.DashboardKasir.route) {
                    popUpTo(Screen.DashboardKasir.route) { inclusive = true }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = utamaHijau)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AddCircle, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Transaksi Baru", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(
                onClick = { /* Cetak */ },
                modifier = Modifier.weight(1f).height(50.dp),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, utamaHijau),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = utamaHijau)
            ) {
                Icon(Icons.Default.Print, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cetak Nota")
            }

            OutlinedButton(
                onClick = { /* Bagikan */ },
                modifier = Modifier.weight(1f).height(50.dp),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, utamaHijau),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = utamaHijau)
            ) {
                Icon(Icons.Default.Share, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Bagikan")
            }
        }
    }
}
