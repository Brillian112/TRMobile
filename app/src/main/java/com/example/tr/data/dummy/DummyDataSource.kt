package com.example.tr.data.dummy

import com.example.tr.data.model.DashboardData
import com.example.tr.data.model.InventoriData
import com.example.tr.data.model.KaryawanData
import com.example.tr.data.model.KehadiranData
import com.example.tr.data.model.MenuData
import com.example.tr.data.model.TransactionDetail
import com.example.tr.data.model.Transaksi
import com.example.tr.data.model.AttendanceData
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
        KaryawanData(id = "1", nama = "Andi", peran = "Kasir", password = "password", status = "Aktif"),
        KaryawanData(id = "2", nama = "Siti", peran = "Kasir", password = "password", status = "Aktif")
    )

    val dummyDetail = listOf(
        TransactionDetail(
            id = 1,
            menuId = 10,
            menuName = "Cappuccino Hot",
            transactionId = 1024,
            quantity = 1,
            price = 22000.0,
            subtotal = 22000.0
        )
    )

    val dummyTransaksi = Transaksi(
        id = 1024,
        userId = 1,
        namaKasir = "Andi",
        subtotal = 22000.0,
        taxAmount = 2200.0,
        totalAmount = 24200.0,
        paymentMethod = "Tunai",
        status = "SUCCESS",
        transactionsDate = "23 Okt 2023, 08:30",
        items = dummyDetail,
        uangTunaiDiterima = 50000.0
    )

    // TAMBAHAN BARU: List data dummy untuk RiwayatTransaksiScreen (Sesuai Mockup Gambar)
    val dummyTransaksiList = listOf(
        Transaksi(
            id = 1024,
            userId = 1,
            namaKasir = "Andi",
            subtotal = 113636.0,
            taxAmount = 11364.0,
            totalAmount = 125000.0,
            paymentMethod = "Tunai",
            status = "SUCCESS",
            transactionsDate = "23 Okt 2023, 08:30",
            items = emptyList(),
            uangTunaiDiterima = 150000.0
        ),
        Transaksi(
            id = 1023,
            userId = 1,
            namaKasir = "Andi",
            subtotal = 318181.0,
            taxAmount = 31819.0,
            totalAmount = 350000.0,
            paymentMethod = "Tunai",
            status = "SUCCESS",
            transactionsDate = "23 Okt 2023, 09:15",
            items = emptyList(),
            uangTunaiDiterima = 350000.0
        ),
        Transaksi(
            id = 1022,
            userId = 1,
            namaKasir = "Andi",
            subtotal = 68181.0,
            taxAmount = 6819.0,
            totalAmount = 75000.0,
            paymentMethod = "Tunai",
            status = "SUCCESS",
            transactionsDate = "23 Okt 2023, 10:45",
            items = emptyList(),
            uangTunaiDiterima = 100000.0
        )
    )

    // TAMBAHAN BARU: List data dummy untuk AbsensiScreen (Sesuai Mockup Gambar)
    val dummyAbsensiList = listOf(
        AttendanceData(1, 1, "20 Okt 2023", "07:50", "17:05", "Hadir"),
        AttendanceData(2, 1, "19 Okt 2023", "08:15", "17:00", "Terlambat"),
        AttendanceData(3, 1, "18 Okt 2023", "07:40", "17:10", "Hadir")
    )

    val dummyTransaksiList = listOf(
        TransaksiData("1", "#INV-1024", "Selesai", "Andi", 3, 125000L, "Hari Ini"),
        TransaksiData("2", "#INV-1023", "Dibatalkan", "Andi", 1, 15000L, "Hari Ini"),
        TransaksiData("3", "#INV-1022", "Selesai", "Siti", 5, 250000L, "Minggu Ini"),
        TransaksiData("4", "#INV-1021", "Selesai", "Andi", 2, 80000L, "Bulan Ini")
    )

    //Data Dummy Absen
    val dummyKehadiranList = listOf(
        KehadiranData("1", "Andi", "08:00 WIB", "Hadir"),
        KehadiranData("2", "Siti", "-", "Izin")
    )

    //Data Dummy Inventory
    val dummyInventoriList = listOf(
        InventoriData("1", "Beras", 15.0, "kg", "Cukup"),
        InventoriData("2", "Kopi Arabika", 0.5, "kg", "Rendah"),
        InventoriData("3", "Gula Pasir", 0.0, "kg", "Habis")
    )

}