package com.example.tr.uitr.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tr.uitr.navigation.Screen

@Composable
fun ProfilScreen(navController: NavController) {
    val utamaHijau = Color(0xFF0F6E52)
    val merahTombol = Color(0xFFC62828)
    val bgLight = Color(0xFFF8F9FA)

    Scaffold(
        containerColor = bgLight
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // 1. TOP HEADER (Profile Mini, Title, Notifikasi)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(modifier = Modifier.size(36.dp).background(Color.LightGray, shape = CircleShape))
                    Text("KasirKu", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = utamaHijau)
                }
                IconButton(onClick = { /* Aksi Notifikasi */ }) {
                    Icon(Icons.Default.Notifications, contentDescription = null, tint = utamaHijau)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. JUDUL HALAMAN
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(text = "Profil", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. CARD DATA DIRI UTAMA
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Foto Profil Besar dengan Badge "Kasir"
                    Box(
                        modifier = Modifier.size(100.dp),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        // Ganti R.drawable.avatar_placeholder dengan aset fotomu jika ada, sementara pakai Box abu-abu
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.LightGray, CircleShape)
                        )

                        // Badge Kasir Hijau di bawah foto
                        Surface(
                            color = utamaHijau,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.offset(y = 4.dp)
                        ) {
                            Text(
                                text = "Kasir",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Andi Setiawan", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Spacer(modifier = Modifier.height(16.dp))

                    // Garis Pembatas Tipis
                    HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 1.dp)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Detail Info Kasir
                    DetailInfoRow(label = "Username", value = "andi_kasir")
                    Spacer(modifier = Modifier.height(8.dp))
                    DetailInfoRow(label = "Bergabung", value = "1 Jan 2023")
                    Spacer(modifier = Modifier.height(8.dp))
                    DetailInfoRow(label = "Shift", value = "Pagi")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 4. MENU PILIHAN (Ganti Password & Tentang Aplikasi)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
            ) {
                Column {
                    MenuOpsiItem(
                        icon = Icons.Default.Lock,
                        title = "Ganti Password",
                        onClick = { /* Navigasi ke Ganti Password */ }
                    )
                    HorizontalDivider(color = Color(0xFFF5F5F5), thickness = 1.dp)
                    MenuOpsiItem(
                        icon = Icons.Default.Info,
                        title = "Tentang Aplikasi",
                        onClick = { /* Navigasi ke Tentang Aplikasi */ }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 5. TOMBOL KELUAR (LOGOUT)
            Button(
                onClick = {
                    // Balikkan ke Halaman Login dan hapus seluruh backstack
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = merahTombol),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Logout, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Keluar", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
                }
            }
        }
    }
}

@Composable
fun DetailInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 13.sp, color = Color.Gray)
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
    }
}

@Composable
fun MenuOpsiItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(icon, contentDescription = null, tint = Color.DarkGray, modifier = Modifier.size(20.dp))
            Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.Black)
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
    }
}
