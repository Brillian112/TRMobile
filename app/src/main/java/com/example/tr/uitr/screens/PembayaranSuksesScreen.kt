package com.example.tr.uitr.screens // 1. Perbaikan Package

import androidx.compose.foundation.BorderStroke // 2. Tambah Import
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset // 3. Tambah Import
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tr.data.model.Transaksi
import com.example.tr.data.model.TransactionDetail

@Composable
fun PembayaranSuksesScreen(
    transaksi: Transaksi,
    onTransaksiBaruClick: () -> Unit,
    onCetakNotaClick: () -> Unit,
    onBagikanClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Box(
            modifier = Modifier
                .size(72.dp)
                .background(Color(0xFF004D40), shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Sukses",
                tint = Color.White,
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Pembayaran Berhasil",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "KasirKu",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00695C)
                )
                Text(
                    text = "Jl. Sudirman No. 123, Jakarta\nTelp: 021-555-1234",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp)
                )

                DashedDivider(modifier = Modifier.padding(vertical = 16.dp))

                RowDetail(label = "No. Transaksi", value = "#INV-${transaksi.id}", isBoldValue = true)
                RowDetail(label = "Tanggal", value = transaksi.transactionsDate)
                RowDetail(label = "Kasir", value = transaksi.namaKasir)

                DashedDivider(modifier = Modifier.padding(vertical = 16.dp))

                transaksi.items.forEach { detail ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = detail.menuName,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = Color.Black
                            )
                            Text(
                                text = "Rp ${formatRupiah(detail.subtotal)}",
                                fontSize = 14.sp,
                                color = Color.Black
                            )
                        }
                        Text(
                            text = "${detail.quantity} x Rp ${formatRupiah(detail.price)}",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }

                DashedDivider(modifier = Modifier.padding(vertical = 16.dp))

                RowDetail(label = "Subtotal", value = "Rp ${formatRupiah(transaksi.subtotal)}")
                RowDetail(label = "Pajak (10%)", value = "Rp ${formatRupiah(transaksi.taxAmount)}")

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Total", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Text(
                        text = "Rp ${formatRupiah(transaksi.totalAmount)}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00695C)
                    )
                }

                DashedDivider(modifier = Modifier.padding(vertical = 16.dp))

                RowDetail(label = "Tunai", value = "Rp ${formatRupiah(transaksi.uangTunaiDiterima)}")
                RowDetail(
                    label = "Kembalian",
                    value = "Rp ${formatRupiah(transaksi.kembalian)}",
                    labelColor = Color(0xFF00695C),
                    valueColor = Color(0xFF00695C),
                    isBoldValue = true
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onTransaksiBaruClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00695C)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Transaksi Baru", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onCetakNotaClick,
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF00695C)),
                border = BorderStroke(1.dp, Color(0xFF00695C)) // 4. Perbaikan Border
            ) {
                Text("🖨️ Cetak Nota", fontSize = 14.sp, fontWeight = FontWeight.Medium)
            }

            OutlinedButton(
                onClick = onBagikanClick,
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF00695C)),
                border = BorderStroke(1.dp, Color(0xFF00695C)) // 4. Perbaikan Border
            ) {
                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Bagikan", fontSize = 14.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
fun RowDetail(
    label: String,
    value: String,
    isBoldValue: Boolean = false,
    labelColor: Color = Color.Black,
    valueColor: Color = Color.Black
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 13.sp, color = labelColor)
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = if (isBoldValue) FontWeight.Bold else FontWeight.Normal,
            color = valueColor
        )
    }
}

@Composable
fun DashedDivider(modifier: Modifier = Modifier, color: Color = Color(0xFFE0E0E0)) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
    ) {
        // 5. Perbaikan: Menggunakan drawLine
        drawLine(
            color = color,
            start = Offset(0f, 0f),
            end = Offset(size.width, 0f),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 12f), 0f),
            strokeWidth = 1.dp.toPx()
        )
    }
}

// 6. Perbaikan format rupiah agar lebih stabil
fun formatRupiah(amount: Double): String {
    return "%,.0f".format(amount).replace(",", ".")
}
