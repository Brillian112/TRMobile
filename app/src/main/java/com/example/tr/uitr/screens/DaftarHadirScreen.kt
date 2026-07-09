package com.example.tr.uitr.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.room.util.copy
import com.example.tr.data.dummy.DummyDataSource
import com.example.tr.data.model.KehadiranData
import com.example.tr.ui.theme.BgLight
import com.example.tr.ui.theme.DarkNavy
import com.example.tr.ui.theme.GreenIcon
import com.example.tr.ui.theme.RedIcon
import com.example.tr.ui.theme.TextGray
import com.example.tr.ui.theme.YellowIcon
import java.util.UUID

// Warna Khusus Layar Daftar Hadir


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DaftarHadirScreen(navController: NavController) {
    // STATE LOKAL
    val kehadiranList = remember { mutableStateListOf(*DummyDataSource.dummyKehadiranList.toTypedArray()) }
    var searchQuery by remember { mutableStateOf("") }

    // State Simulasi Tanggal
    var currentDate by remember { mutableStateOf("23 Okt 2023") }

    // State Dialog Tambah Manual
    var showFormDialog by remember { mutableStateOf(false) }

    // Hitung Ringkasan Dinamis
    val totalHadir = kehadiranList.count { it.status == "Hadir" }
    val totalAbsen = kehadiranList.count { it.status == "Absen" }
    val totalIzin = kehadiranList.count { it.status == "Izin" }

    // Filter List berdasarkan pencarian
    val filteredList = kehadiranList.filter {
        it.nama.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        containerColor = BgLight,
        topBar = { TopBarKehadiran() },
        bottomBar = { KelolaBottomNav(navController) } // Gunakan navigasi dari Kelola Menu
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // Header Teks
            Text("Daftar Hadir", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = DarkNavy)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Kelola presensi karyawan hari ini.", fontSize = 13.sp, color = TextGray)
            Spacer(modifier = Modifier.height(16.dp))

            // Pemilih Tanggal (Date Picker Dummy)
            Card(
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.wrapContentWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { currentDate = "22 Okt 2023" },
                        modifier = Modifier.size(24.dp)
                    ) { Icon(Icons.Filled.ChevronLeft, contentDescription = "Prev", tint = DarkNavy) }

                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(Icons.Outlined.CalendarToday, contentDescription = "Calendar", tint = DarkNavy, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(currentDate, fontWeight = FontWeight.Bold, color = DarkNavy, fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(12.dp))

                    IconButton(
                        onClick = { currentDate = "24 Okt 2023" },
                        modifier = Modifier.size(24.dp)
                    ) { Icon(Icons.Filled.ChevronRight, contentDescription = "Next", tint = DarkNavy) }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Kartu Ringkasan (Hadir, Absen, Izin)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SummaryBox(Modifier.weight(1f), "HADIR", totalHadir.toString(), Icons.Outlined.CheckCircleOutline, GreenIcon)
                SummaryBox(Modifier.weight(1f), "ABSEN", totalAbsen.toString(), Icons.Outlined.HighlightOff, RedIcon)
                SummaryBox(Modifier.weight(1f), "IZIN", totalIzin.toString(), Icons.Outlined.Info, YellowIcon)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Wadah Putih untuk Data Kehadiran dan List
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
                modifier = Modifier.fillMaxWidth().weight(1f) // Mengisi sisa ruang ke bawah
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Header Card & Search
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Data\nKehadiran",
                            fontWeight = FontWeight.Bold,
                            color = DarkNavy,
                            fontSize = 16.sp,
                            lineHeight = 20.sp
                        )

                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Cari karyawan...", fontSize = 12.sp, color = TextGray) },
                            leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = "Search", modifier = Modifier.size(16.dp), tint = TextGray) },
                            modifier = Modifier.width(180.dp).height(48.dp),
                            shape = RoundedCornerShape(8.dp),
//                            colors = TextFieldDefaults.outlinedTextFieldColors(
//                                unfocusedBorderColor = Color(0xFFE2E8F0),
//                                containerColor = BgLight
//                            )
                        )
                    }

                    HorizontalDivider(color = Color(0xFFF8F9FE), thickness = 2.dp)

                    // List Presensi
                    LazyColumn(modifier = Modifier.weight(1f)) {
                        items(filteredList) { kehadiran ->
                            KehadiranItemRow(kehadiran)
                            HorizontalDivider(color = Color(0xFFF8F9FE), thickness = 2.dp)
                        }
                    }

                    // Tombol Tambah Manual (Diletakkan di dalam Card paling bawah)
                    Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        Button(
                            onClick = { showFormDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = DarkNavy),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().height(50.dp)
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = "Tambah", tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Tambah Absensi Manual", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // Dialog Tambah Absensi Manual
    if (showFormDialog) {
        FormAbsensiDialog(
            onDismiss = { showFormDialog = false },
            onSave = { nama, jam, status ->
                kehadiranList.add(KehadiranData(id = UUID.randomUUID().toString(), nama = nama, jam = jam, status = status))
                showFormDialog = false
            }
        )
    }
}

@Composable
fun TopBarKehadiran() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(36.dp).clip(CircleShape).background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) { Icon(Icons.Filled.Person, contentDescription = "Profil", tint = Color.White, modifier = Modifier.size(20.dp)) }
            Spacer(modifier = Modifier.width(12.dp))
            Text("KasirKu", color = DarkNavy, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        Icon(Icons.Outlined.Notifications, contentDescription = "Notifikasi", tint = DarkNavy)
    }
}

@Composable
fun SummaryBox(modifier: Modifier, title: String, count: String, icon: ImageVector, color: Color) {
    Card(
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(imageVector = icon, contentDescription = title, tint = color, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = title, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextGray)
            Text(text = count, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
}

@Composable
fun KehadiranItemRow(kehadiran: KehadiranData) {
    val badgeColor = when (kehadiran.status) {
        "Hadir" -> Color(0xFFD1FAE5) // Hijau muda
        "Absen" -> Color(0xFFFEE2E2) // Merah muda
        else -> Color(0xFFFEF3C7)    // Kuning muda
    }

    val badgeTextColor = when (kehadiran.status) {
        "Hadir" -> GreenIcon
        "Absen" -> RedIcon
        else -> YellowIcon
    }

    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Avatar Inisial
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFFE2E8F0)),
                contentAlignment = Alignment.Center
            ) {
                Text(kehadiran.nama.take(1).uppercase(), color = TextGray, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = kehadiran.nama, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DarkNavy)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.AccessTime, contentDescription = "Waktu", modifier = Modifier.size(12.dp), tint = TextGray)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = kehadiran.jam, fontSize = 12.sp, color = TextGray)
                }
            }
        }

        // Badge Status
        Surface(
            color = badgeColor,
            shape = RoundedCornerShape(4.dp),
            border = BorderStroke(1.dp, badgeTextColor.copy(alpha = 0.3f))
        ) {
            Text(
                text = kehadiran.status,
                color = badgeTextColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormAbsensiDialog(onDismiss: () -> Unit, onSave: (String, String, String) -> Unit) {
    var nama by remember { mutableStateOf("") }
    var jam by remember { mutableStateOf("08:00 WIB") }
    var status by remember { mutableStateOf("Hadir") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tambah Absensi", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = nama, onValueChange = { nama = it }, label = { Text("Nama Karyawan") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = jam, onValueChange = { jam = it }, label = { Text("Jam Kedatangan") }, modifier = Modifier.fillMaxWidth())
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Status: ", modifier = Modifier.weight(1f))
                    TextButton(onClick = { status = "Hadir" }, modifier = Modifier.weight(1f)) { Text("Hadir", color = if (status == "Hadir") GreenIcon else Color.Gray) }
                    TextButton(onClick = { status = "Izin" }, modifier = Modifier.weight(1f)) { Text("Izin", color = if (status == "Izin") YellowIcon else Color.Gray) }
                }
            }
        },
        confirmButton = {
            Button(onClick = { if (nama.isNotBlank()) onSave(nama, jam, status) }, colors = ButtonDefaults.buttonColors(containerColor = DarkNavy)) { Text("Simpan") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Batal") } }
    )
}