package com.example.tr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tr.uitr.navigation.Screen
import com.example.tr.uitr.screens.DashboardScreen
import com.example.tr.uitr.screens.LoginScreen
import com.example.tr.ui.theme.TRTheme
import com.example.tr.uitr.screens.DaftarHadirScreen
import com.example.tr.uitr.screens.DashboardKasirScreen
import com.example.tr.uitr.screens.InventoriScreen
import com.example.tr.uitr.screens.KelolaKaryawanScreen
import com.example.tr.uitr.screens.KelolaMenuScreen
import com.example.tr.uitr.screens.RiwayatTransaksiScreen
import com.example.tr.uitr.screens.ProfilScreen
import com.example.tr.uitr.screens.LaporanScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tr.uitr.screens.AbsensiScreen
import com.example.tr.uitr.screens.PembayaranScreen
import com.example.tr.uitr.screens.TransaksiBaruScreen
import com.example.tr.uitr.screens.PembayaranSuksesScreen
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.tr.uitr.viewmodel.AttendanceViewModel
import com.example.tr.uitr.viewmodel.AuthViewModel
import com.example.tr.uitr.viewmodel.TransactionViewModel
import com.example.tr.uitr.viewmodel.MenuViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TokenManager dihapus sesuai permintaan
        val startDestination = Screen.Login.route

        setContent {
            TRTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val sharedAuthViewModel: AuthViewModel = viewModel()
                    val sharedTransactionViewModel: TransactionViewModel = viewModel()
                    val sharedAttendanceViewModel: AttendanceViewModel = viewModel()
                    val sharedMenuViewModel: MenuViewModel = viewModel()

                    NavHost(navController = navController, startDestination = startDestination) {
                        composable(Screen.Login.route) {
                            LoginScreen(navController = navController, viewModel = sharedAuthViewModel)
                        }
                        composable(Screen.Dashboard.route) {
                            DashboardScreen(navController)
                        }
                        composable(Screen.Menu.route) {
                            KelolaMenuScreen(navController)
                        }
                        composable(Screen.Karyawan.route) {
                            KelolaKaryawanScreen(navController)
                        }
                        composable(Screen.Transaksi.route) {
                            RiwayatTransaksiScreen(navController)
                        }
                        composable(Screen.Presensi.route) {
                            DaftarHadirScreen(navController)
                        }
                        composable(Screen.Inventori.route) {
                            InventoriScreen(navController)
                        }
                        composable(Screen.Profil.route) {
                            ProfilScreen(navController)
                        }
                        composable(Screen.Laporan.route) {
                            LaporanScreen(navController)
                        }
                        composable(Screen.DashboardKasir.route) {
                            DashboardKasirScreen(
                                navController = navController,
                                authViewModel = sharedAuthViewModel,
                                transactionViewModel = sharedTransactionViewModel,
                                attendanceViewModel = sharedAttendanceViewModel
                            )
                        }
                        composable(Screen.Absensi.route) {
                            AbsensiScreen(
                                navController = navController,
                                attendanceViewModel = sharedAttendanceViewModel
                            )
                        }
                        // --- RUTE TRANSAKSI BARU ---
                        composable(Screen.TransaksiBaru.route) {
                            TransaksiBaruScreen(
                                navController = navController,
                                menuViewModel = sharedMenuViewModel, // <-- pakai koma
                                transactionViewModel = sharedTransactionViewModel // <-- parameter terakhir
                            ) // <-- ditutup kurung dengan benar
                        }

                        // --- RUTE PEMBAYARAN ---
                        composable(Screen.Pembayaran.route) {
                            PembayaranScreen(
                                navController = navController,
                                transactionViewModel = sharedTransactionViewModel,
                                authViewModel = sharedAuthViewModel
                            )
                        }

                        // --- RUTE PEMBAYARAN SUKSES ---
                        composable(
                            route = Screen.PembayaranSukses.route + "/{uangDiterima}",
                            arguments = listOf(navArgument("uangDiterima") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val uangDiterimaText = backStackEntry.arguments?.getString("uangDiterima") ?: "0"
                            val uangDiterima = uangDiterimaText.toDoubleOrNull() ?: 0.0
                            PembayaranSuksesScreen(
                                navController = navController,
                                transactionViewModel = sharedTransactionViewModel,
                                uangDiterima = uangDiterima
                            )
                        }
                    }
                }
            }
        }
    }
}
