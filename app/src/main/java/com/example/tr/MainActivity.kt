package com.example.tr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.tr.uitr.navigation.Screen
import com.example.tr.uitr.screens.DashboardScreen
import com.example.tr.uitr.screens.LoginScreen
import com.example.tr.ui.theme.TRTheme
import com.example.tr.uitr.screens.DashboardKasirScreen
import com.example.tr.uitr.screens.KelolaKaryawanScreen
import com.example.tr.uitr.screens.KelolaMenuScreen
import com.example.tr.uitr.screens.PembayaranScreen
import com.example.tr.uitr.screens.PembayaranSuksesScreen
import com.example.tr.uitr.screens.TransaksiBaruScreen
import com.example.tr.uitr.screens.RiwayatTransaksiScreen
import com.example.tr.uitr.screens.AbsensiScreen
import com.example.tr.data.dummy.DummyDataSource
import com.example.tr.uitr.screens.ProfilScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TRTheme {
                val navController = rememberNavController()

                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                // TENTUKAN DI SINI: Hanya muncul di Beranda, Transaksi (Riwayat), Absensi, Profil, dan TransaksiBaru
                val ruteBottomBar = listOf(
                    Screen.DashboardKasir.route, // Beranda
                    Screen.Riwayat.route,        // Transaksi (Riwayat)
                    Screen.Absensi.route,        // Absensi
                    Screen.TransaksiBaru.route,  // Area transaksi baru/kasir
                    "profil"                     // Profil
                )

                Scaffold(
                    bottomBar = {
                        // Filter aktif kembali: Hanya merender bottom navbar jika rutenya ada di dalam list di atas
                        if (currentRoute in ruteBottomBar) {
                            NavigationBar(
                                containerColor = Color.White
                            ) {
                                NavigationBarItem(
                                    selected = currentRoute == Screen.DashboardKasir.route,
                                    onClick = {
                                        navController.navigate(Screen.DashboardKasir.route) {
                                            popUpTo(Screen.DashboardKasir.route) { inclusive = true }
                                        }
                                    },
                                    label = { Text("Beranda") },
                                    icon = { Icon(Icons.Default.Home, contentDescription = "Beranda") },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = Color(0xFF0F6E52),
                                        selectedTextColor = Color(0xFF0F6E52),
                                        indicatorColor = Color(0xFFA7F3D0)
                                    )
                                )
                                NavigationBarItem(
                                    selected = currentRoute == Screen.Riwayat.route,
                                    onClick = { navController.navigate(Screen.Riwayat.route) { launchSingleTop = true } },
                                    label = { Text("Transaksi") },
                                    icon = { Icon(Icons.Default.List, contentDescription = "Transaksi") },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = Color(0xFF0F6E52),
                                        selectedTextColor = Color(0xFF0F6E52),
                                        indicatorColor = Color(0xFFA7F3D0)
                                    )
                                )
                                NavigationBarItem(
                                    selected = currentRoute == Screen.Absensi.route,
                                    onClick = { navController.navigate(Screen.Absensi.route) { launchSingleTop = true } },
                                    label = { Text("Absensi") },
                                    icon = { Icon(Icons.Default.DateRange, contentDescription = "Absensi") },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = Color(0xFF0F6E52),
                                        selectedTextColor = Color(0xFF0F6E52),
                                        indicatorColor = Color(0xFFA7F3D0)
                                    )
                                )
                                NavigationBarItem(
                                    selected = currentRoute == "profil",
                                    onClick = { navController.navigate(Screen.Profil.route) { launchSingleTop = true } },
                                    label = { Text("Profil") },
                                    icon = { Icon(Icons.Default.AccountCircle, contentDescription = "Profil") },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = Color(0xFF0F6E52),
                                        selectedTextColor = Color(0xFF0F6E52),
                                        indicatorColor = Color(0xFFA7F3D0)
                                    )
                                )
                            }
                        }
                    }
                ) { paddingValues ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        NavHost(navController = navController, startDestination = Screen.Login.route) {
                            composable(Screen.Login.route) {
                                LoginScreen(navController)
                            }
                            composable(Screen.DashboardManager.route) {
                                DashboardScreen(navController)
                            }
                            composable(Screen.Menu.route) {
                                KelolaMenuScreen(navController)
                            }
                            composable(Screen.Karyawan.route) {
                                KelolaKaryawanScreen(navController)
                            }

                            // Karyawan / Kasir Area
                            composable(Screen.TransaksiBaru.route) {
                                TransaksiBaruScreen(navController = navController)
                            }
                            composable(Screen.DashboardKasir.route) {
                                DashboardKasirScreen(navController = navController)
                            }
                            composable(Screen.Pembayaran.route) {
                                PembayaranScreen(
                                    navController = navController,
                                    onTransaksiSelesai = { _, _ ->
                                        navController.navigate(Screen.PembayaranSukses.route) {
                                            popUpTo(Screen.Pembayaran.route) { inclusive = true }
                                        }
                                    }
                                )
                            }
                            composable(Screen.PembayaranSukses.route) {
                                PembayaranSuksesScreen(
                                    transaksi = DummyDataSource.dummyTransaksi,
                                    onTransaksiBaruClick = {
                                        navController.navigate(Screen.TransaksiBaru.route) {
                                            popUpTo(Screen.DashboardKasir.route) { inclusive = false }
                                        }
                                    },
                                    onCetakNotaClick = {},
                                    onBagikanClick = {}
                                )
                            }

                            // Rute Untuk Halaman Riwayat & Absensi
                            composable(Screen.Riwayat.route) {
                                RiwayatTransaksiScreen(
                                    navController = navController,
                                    listTransaksi = DummyDataSource.dummyTransaksiList
                                )
                            }
                            composable(Screen.Absensi.route) {
                                AbsensiScreen(
                                    navController = navController,
                                    listAbsensi = DummyDataSource.dummyAbsensiList
                                )
                            }

                            composable(Screen.Profil.route) {
                                ProfilScreen(navController = navController)
                            }
                        }
                    }
                }
            }
        }
    }
}