package com.example.tr.data.dummy

import com.example.tr.data.model.DashboardData
import com.example.tr.data.model.KaryawanData
import com.example.tr.data.model.MenuData
import com.example.tr.data.model.TransaksiData

object DummyDataSource {
    val dashboardData = DashboardData(
        userName = "Budi",
        totalPenjualan = 5420000L,
        jumlahTransaksi = 42,
        itemTerjual = 128
    )

    val dummyMenuList = listOf(
        MenuData(
            id = "1",
            nama = "Nasi Goreng",
            deskripsi = "Nasi goreng spesial dengan telur dan sayuran segar.",
            harga = 25000L,
            kategori = "Makanan"
        ),
        MenuData(
            id = "2",
            nama = "Es Teh Manis",
            deskripsi = "Teh manis dingin menyegarkan, diseduh dari daun teh pilihan.",
            harga = 8000L,
            kategori = "Minuman"
        )
    )

    val dummyKaryawanList = listOf(
        KaryawanData(id = "1", nama = "Andi", peran = "Kasir", status = "Aktif"),
        KaryawanData(id = "2", nama = "Siti", peran = "Kasir", status = "Aktif")
    )


    val dummyTransaksiList = listOf(
        TransaksiData("1", "#INV-1024", "Selesai", "Andi", 3, 125000L, "Hari Ini"),
        TransaksiData("2", "#INV-1023", "Dibatalkan", "Andi", 1, 15000L, "Hari Ini"),
        TransaksiData("3", "#INV-1022", "Selesai", "Siti", 5, 250000L, "Minggu Ini"),
        TransaksiData("4", "#INV-1021", "Selesai", "Andi", 2, 80000L, "Bulan Ini")
    )

}