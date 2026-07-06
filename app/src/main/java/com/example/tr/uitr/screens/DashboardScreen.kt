package com.example.tr.uitr.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tr.ui.theme.BgColor
import com.example.tr.ui.theme.CardBg
import com.example.tr.ui.theme.DarkNavy
import com.example.tr.ui.theme.IconBgColor
import com.example.tr.ui.theme.PrimaryBlue
import com.example.tr.ui.theme.TextGray
import com.example.tr.uitr.navigation.Screen

// --- PENGATURAN WARNA KHUSUS UI INI ---


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavController) {
    Scaffold(
        containerColor = BgColor,
        bottomBar = { DashboardBottomNav() }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }
            item { TopBarSection() }
            item { RingkasanHariIniBanner() }
            item { SummaryGridSection() }
            item { AksesCepatSection(navController) }
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
fun TopBarSection() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Placeholder Profile Picture
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Person, contentDescription = "Profil", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = "Selamat datang,", color = TextGray, fontSize = 12.sp)
                Text(
                    text = "Manager Profile",
                    color = DarkNavy,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Icon(
            imageVector = Icons.Outlined.Notifications,
            contentDescription = "Notifikasi",
            tint = DarkNavy,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun RingkasanHariIniBanner() {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkNavy),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Ringkasan Hari Ini",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Pantau performa tokomu secara real-time.",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 13.sp
            )
        }
    }
}

@Composable
fun SummaryGridSection() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SummaryCard(
                modifier = Modifier.weight(1f),
                title = "Total Penjualan",
                value = "Rp 12.5M",
                icon = Icons.Outlined.Payments,
                indicatorColor = Color(0xFF3B82F6) // Biru
            )
            SummaryCard(
                modifier = Modifier.weight(1f),
                title = "Transaksi",
                value = "128",
                icon = Icons.Outlined.Receipt,
                indicatorColor = Color(0xFF8B5CF6) // Ungu
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SummaryCard(
                modifier = Modifier.weight(1f),
                title = "Item Terjual",
                value = "432",
                icon = Icons.Outlined.Inventory2,
                indicatorColor = Color(0xFFEAB308) // Kuning
            )
            SummaryCard(
                modifier = Modifier.weight(1f),
                title = "Staf Aktif",
                value = "8/12",
                icon = Icons.Outlined.Badge,
                indicatorColor = Color(0xFFEF4444) // Merah
            )
        }
    }
}

@Composable
fun SummaryCard(
    modifier: Modifier,
    title: String,
    value: String,
    icon: ImageVector,
    indicatorColor: Color
) {
    Card(
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            // Garis indikator warna di kiri
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(4.dp)
                    .background(indicatorColor)
            )
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = indicatorColor,
                    modifier = Modifier.size(20.dp)
                )
                Column {
                    Text(text = title, color = TextGray, fontSize = 12.sp)
                    Text(text = value, color = DarkNavy, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun AksesCepatSection(navController: NavController) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Akses Cepat",
                color = DarkNavy,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                text = "Lihat Semua",
                color = PrimaryBlue,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Grid 3x2 Manual dengan Navigasi
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                QuickAccessItem(
                    icon = Icons.Outlined.RestaurantMenu,
                    label = "Kelola\nMenu",
                    modifier = Modifier.weight(1f)
                ) { navController.navigate(Screen.Menu.route) }

                QuickAccessItem(
                    icon = Icons.Outlined.Group,
                    label = "Kelola\nKaryawan",
                    modifier = Modifier.weight(1f)
                ) { navController.navigate(Screen.Karyawan.route) }

                QuickAccessItem(
                    icon = Icons.Outlined.PointOfSale,
                    label = "Transaksi",
                    modifier = Modifier.weight(1f)
                ) { navController.navigate(Screen.Transaksi.route) }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                QuickAccessItem(
                    icon = Icons.Outlined.Storefront,
                    label = "Inventori",
                    modifier = Modifier.weight(1f)

                ) { navController.navigate(Screen.Inventori.route) }

                QuickAccessItem(
                    icon = Icons.Outlined.EventAvailable,
                    label = "Daftar\nHadir",
                    modifier = Modifier.weight(1f)
                ) { navController.navigate(Screen.Presensi.route) }

                QuickAccessItem(
                    icon = Icons.Outlined.Assessment,
                    label = "Laporan",
                    modifier = Modifier.weight(1f)
                ) { navController.navigate(Screen.Laporan.route) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickAccessItem(
    icon: ImageVector,
    label: String,
    modifier: Modifier,
    onClick: () -> Unit // Tambahkan parameter onClick
) {
    Card(
        onClick = onClick, // Terapkan aksi klik pada Card
        modifier = modifier.height(110.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(IconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = DarkNavy,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                color = DarkNavy,
                fontSize = 12.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                lineHeight = 16.sp
            )
        }
    }
}



@Composable
fun ActivityItem(icon: ImageVector, iconTint: Color, time: String, boldText: String, normalText: String) {
    Row(verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(BgColor), // Abu-abu muda
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = DarkNavy)) {
                        append(boldText)
                    }
                    withStyle(style = SpanStyle(color = DarkNavy)) {
                        append(normalText)
                    }
                },
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = time, color = TextGray, fontSize = 11.sp)
        }
    }
}

@Composable
fun DashboardBottomNav() {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Filled.GridView, contentDescription = "Beranda") },
            label = { Text("Beranda", fontWeight = FontWeight.Bold) },
            selected = true,
            onClick = { /* TODO */ },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PrimaryBlue,
                selectedTextColor = PrimaryBlue,
                indicatorColor = IconBgColor, // Latar belakang pil biru
                unselectedIconColor = TextGray,
                unselectedTextColor = TextGray
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Outlined.Inventory2, contentDescription = "Kelola") },
            label = { Text("Kelola") },
            selected = false,
            onClick = { /* TODO */ },
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = TextGray,
                unselectedTextColor = TextGray
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Outlined.Assessment, contentDescription = "Laporan") },
            label = { Text("Laporan") },
            selected = false,
            onClick = { /* TODO */ },
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = TextGray,
                unselectedTextColor = TextGray
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Outlined.Person, contentDescription = "Profil") },
            label = { Text("Profil") },
            selected = false,
            onClick = { /* TODO */ },
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = TextGray,
                unselectedTextColor = TextGray
            )
        )
    }
}