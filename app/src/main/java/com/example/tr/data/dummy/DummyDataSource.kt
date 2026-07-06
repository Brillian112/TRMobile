package com.example.tr.data.dummy

import com.example.tr.data.model.DashboardData
import com.example.tr.data.model.KaryawanData
import com.example.tr.data.model.MenuData

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
        KaryawanData(id = "1", nama = "Andi", peran = "Kasir", password = "password", status = "Aktif"),
        KaryawanData(id = "2", nama = "Siti", peran = "Kasir", password = "password", status = "Aktif")
    )

}