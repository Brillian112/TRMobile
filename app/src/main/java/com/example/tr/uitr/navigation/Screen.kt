package com.example.tr.uitr.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object DashboardManager : Screen("dashboard_manager")
    object DashboardKasir : Screen("dashboard_kasir")
    object Karyawan : Screen("karyawan")

    object Menu : Screen("menu")
    object Transaksi : Screen("transaksi")
    object Presensi : Screen("presensi")
    object Profil : Screen("profil")
    object Inventori : Screen("inventori")
    object Laporan : Screen("laporan") // Tambahkan baris ini

//karyawan
    object TransaksiBaru : Screen("transaksi_baru_screen")
    object Pembayaran : Screen("pembayaran_screen")
}