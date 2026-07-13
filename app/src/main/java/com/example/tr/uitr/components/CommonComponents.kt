package com.example.tr.uitr.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tr.ui.theme.*
import com.example.tr.uitr.navigation.Screen

@Composable
fun AppDrawer(
    navController: NavController,
    currentRoute: String?,
    onCloseDrawer: () -> Unit
) {
    ModalDrawerSheet(
        drawerContainerColor = Color.White,
        drawerShape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Drawer
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 24.dp, horizontal = 8.dp)
            ) {
                Surface(
                    modifier = Modifier.size(48.dp),
                    shape = CircleShape,
                    color = DarkNavy
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Filled.Store, contentDescription = null, tint = Color.White)
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("KasirKu", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = DarkNavy)
                    Text("Sistem Manajemen Toko", fontSize = 12.sp, color = TextGray)
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFF1F5F9))

            val menuItems = listOf(
                DrawerItem("Beranda", Screen.Dashboard.route, Icons.Filled.GridView),
                DrawerItem("Kelola Menu", Screen.Menu.route, Icons.Filled.RestaurantMenu),
                DrawerItem("Inventori", Screen.Inventori.route, Icons.Filled.Inventory2),
                DrawerItem("Data Karyawan", Screen.Karyawan.route, Icons.Filled.People),
                DrawerItem("Presensi", Screen.Presensi.route, Icons.Filled.AssignmentInd),
                DrawerItem("Riwayat Transaksi", Screen.Transaksi.route, Icons.Filled.Assessment),
                DrawerItem("Laporan", Screen.Laporan.route, Icons.Filled.BarChart),
                DrawerItem("Profil Saya", Screen.Profil.route, Icons.Filled.Person)
            )

            menuItems.forEach { item ->
                val isSelected = currentRoute == item.route
                NavigationDrawerItem(
                    icon = { Icon(item.icon, contentDescription = null) },
                    label = { Text(item.label, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                    selected = isSelected,
                    onClick = {
                        onCloseDrawer()
                        if (!isSelected) {
                            navController.navigate(item.route) {
                                popUpTo(Screen.Dashboard.route) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = Color(0xFFE2E8F0),
                        selectedIconColor = DarkNavy,
                        selectedTextColor = DarkNavy,
                        unselectedIconColor = TextGray,
                        unselectedTextColor = TextGray
                    ),
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFF1F5F9))

            NavigationDrawerItem(
                icon = { Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null) },
                label = { Text("Keluar") },
                selected = false,
                onClick = {
                    onCloseDrawer()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                colors = NavigationDrawerItemDefaults.colors(
                    unselectedIconColor = Color.Red,
                    unselectedTextColor = Color.Red
                )
            )
        }
    }
}

data class DrawerItem(val label: String, val route: String, val icon: ImageVector)

@Composable
fun StatusBadge(status: String) {
    val normalizedStatus = status.lowercase()
    val isSelesai = normalizedStatus == "completed" || normalizedStatus == "berhasil" || normalizedStatus == "hadir" || normalizedStatus == "manager" || normalizedStatus == "cukup"
    val isPending = normalizedStatus == "pending" || normalizedStatus == "izin" || normalizedStatus == "rendah" || normalizedStatus == "employee"

    val badgeColor = when {
        isSelesai -> GreenBg
        isPending -> YellowBg
        else -> RedBg
    }
    
    val textColor = when {
        isSelesai -> GreenText
        isPending -> YellowText
        else -> RedText
    }

    Surface(
        color = badgeColor,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, textColor.copy(alpha = 0.2f))
    ) {
        Text(
            text = status,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}
