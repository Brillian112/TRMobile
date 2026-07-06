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
import com.example.tr.uitr.screens.KelolaKaryawanScreen
import com.example.tr.uitr.screens.KelolaMenuScreen
import com.example.tr.uitr.screens.RiwayatTransaksiScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TRTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    NavHost(navController = navController, startDestination = Screen.Login.route) {
                        composable(Screen.Login.route) {
                            LoginScreen(navController)
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
                        // Layar lainnya akan ditambahkan di sini
                    }
                }
            }
        }
    }
}