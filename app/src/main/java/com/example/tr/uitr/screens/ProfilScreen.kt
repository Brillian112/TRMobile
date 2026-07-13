package com.example.tr.uitr.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.tr.ui.theme.BgLight
import com.example.tr.ui.theme.DarkNavy
import com.example.tr.ui.theme.PurplePrimary
import com.example.tr.ui.theme.TextGray
import com.example.tr.uitr.navigation.Screen
import com.example.tr.uitr.viewmodel.AuthViewModel
import com.example.tr.uitr.components.AppDrawer
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilScreen(navController: NavController, authViewModel: AuthViewModel = viewModel()) {
    
    val user = authViewModel.user
    val userName = user?.name ?: "User"
    val userRole = user?.role ?: "Employee"

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
                    title = { Text("Profil", fontWeight = FontWeight.Bold, color = DarkNavy) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Menu", tint = DarkNavy)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = BgLight)
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(20.dp))
                
                // Profile Header
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(PurplePrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = userName.take(1).uppercase(),
                        color = Color.White,
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(text = userName, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = DarkNavy)
                Text(text = userRole, fontSize = 16.sp, color = TextGray)
                
                Spacer(modifier = Modifier.height(40.dp))
                
                // Menu Options
                ProfileMenuItem(icon = Icons.Outlined.Person, label = "Edit Profil")
                ProfileMenuItem(icon = Icons.Outlined.Settings, label = "Pengaturan")
                ProfileMenuItem(icon = Icons.Outlined.HelpOutline, label = "Bantuan")
                
                Spacer(modifier = Modifier.weight(1f))
                
                Button(
                    onClick = {
                        authViewModel.logout {
                            navController.navigate(Screen.Login.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEE2E2)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Filled.Logout, contentDescription = "Logout", tint = Color.Red)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Keluar dari Akun", color = Color.Red, fontWeight = FontWeight.Bold)
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
fun ProfileMenuItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String) {
    Surface(
        onClick = { /* Handle Click */ },
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = DarkNavy, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = label, modifier = Modifier.weight(1f), fontSize = 16.sp, color = DarkNavy)
            Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = TextGray)
        }
    }
}
