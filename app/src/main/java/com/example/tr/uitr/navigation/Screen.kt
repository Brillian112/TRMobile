package com.example.tr.uitr.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Dashboard : Screen("dashboard")
    object Karyawan : Screen("karyawan")

    object Menu : Screen("menu")
    object Transaksi : Screen("transaksi")
    object Presensi : Screen("presensi")
    object Profil : Screen("profil")
    object Inventori : Screen("inventori")
    object Laporan : Screen("laporan") // Tambahkan baris ini

    object DashboardKasir : Screen("dashboard_kasir")

    object Absensi : Screen("absensi")

    object TransaksiBaru : Screen("transaksi_baru")

    object Pembayaran : Screen("pembayaran")

    object PembayaranSukses : Screen("pembayaran_sukses")

}