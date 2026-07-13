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
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.tr.uitr.components.AppDrawer
import com.example.tr.uitr.components.StatusBadge
import com.example.tr.uitr.navigation.Screen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tr.data.remote.model.Attendance
import com.example.tr.uitr.viewmodel.AttendanceViewModel
import com.example.tr.ui.theme.BgLight
import com.example.tr.ui.theme.DarkNavy
import com.example.tr.ui.theme.GreenIcon
import com.example.tr.ui.theme.RedIcon
import com.example.tr.ui.theme.TextGray
import com.example.tr.ui.theme.YellowIcon
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DaftarHadirScreen(navController: NavController, viewModel: AttendanceViewModel = viewModel()) {
    // STATE FROM VIEWMODEL
    val attendanceList = viewModel.attendances
    var searchQuery by remember { mutableStateOf("") }

    // State Simulasi Tanggal (Bisa diambil dari attendanceDate record pertama jika ada)
    val currentDate = "23 Okt 2023" // Tetap dummy atau format dari API

    // State Dialog Tambah Manual
    var showFormDialog by remember { mutableStateOf(false) }

    // Hitung Ringkasan Dinamis
    val totalHadir = attendanceList.count { it.status.equals("Hadir", ignoreCase = true) }
    val totalAbsen = attendanceList.count { it.status.equals("Absen", ignoreCase = true) }
    val totalIzin = attendanceList.count { it.status.equals("Izin", ignoreCase = true) }

    // Filter List berdasarkan pencarian nama
    val filteredList = attendanceList.filter {
        it.user?.name?.contains(searchQuery, ignoreCase = true) ?: false
    }

    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawer(
                navController = navController,
                currentRoute = currentRoute,
                onCloseDrawer = { scope.launch { drawerState.close() } }
            )
        }
    ) {
        Scaffold(
            containerColor = BgLight,
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Presensi", fontWeight = FontWeight.Bold, color = DarkNavy) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Menu", tint = DarkNavy)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = BgLight)
                )
            }
        ) { paddingValues ->
            if (viewModel.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
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

                    // Pemilih Tanggal
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
                            IconButton(onClick = { /* Implement Prev Date */ }, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Filled.ChevronLeft, contentDescription = "Prev", tint = DarkNavy)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Icon(Icons.Outlined.CalendarToday, contentDescription = "Calendar", tint = DarkNavy, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(currentDate, fontWeight = FontWeight.Bold, color = DarkNavy, fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            IconButton(onClick = { /* Implement Next Date */ }, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Filled.ChevronRight, contentDescription = "Next", tint = DarkNavy)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Kartu Ringkasan
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SummaryBox(Modifier.weight(1f), "HADIR", totalHadir.toString(), Icons.Filled.CheckCircle, GreenIcon)
                        SummaryBox(Modifier.weight(1f), "ABSEN", totalAbsen.toString(), Icons.Filled.Cancel, RedIcon)
                        SummaryBox(Modifier.weight(1f), "IZIN", totalIzin.toString(), Icons.Filled.Info, YellowIcon)
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Wadah Putih
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                        border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
                        modifier = Modifier.fillMaxWidth().weight(1f)
                    ) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "Data\nKehadiran", fontWeight = FontWeight.Bold, color = DarkNavy, fontSize = 16.sp, lineHeight = 20.sp)
                                OutlinedTextField(
                                    value = searchQuery,
                                    onValueChange = { searchQuery = it },
                                    placeholder = { Text("Cari karyawan...", fontSize = 12.sp, color = TextGray) },
                                    leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = "Search", modifier = Modifier.size(16.dp), tint = TextGray) },
                                    modifier = Modifier.width(180.dp).height(48.dp),
                                    shape = RoundedCornerShape(8.dp)
                                )
                            }
                            HorizontalDivider(color = Color(0xFFF8F9FE), thickness = 2.dp)

                            LazyColumn(modifier = Modifier.weight(1f)) {
                                items(filteredList) { attendance ->
                                    KehadiranItemRow(attendance)
                                    HorizontalDivider(color = Color(0xFFF8F9FE), thickness = 2.dp)
                                }
                            }

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
        }
    }

    if (showFormDialog) {
        FormAbsensiDialog(
            onDismiss = { showFormDialog = false },
            onSave = { userId, status ->
                viewModel.submitAttendance(userId, status)
                showFormDialog = false
            }
        )
    }
}

@Composable
fun TopBarKehadiran() {
    // Deprecated in favor of Scaffold TopBar
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
fun KehadiranItemRow(attendance: Attendance) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFFE2E8F0)),
                contentAlignment = Alignment.Center
            ) {
                Text(attendance.user?.name?.take(1)?.uppercase() ?: "?", color = TextGray, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = attendance.user?.name ?: "Unknown", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DarkNavy)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.AccessTime, contentDescription = "Waktu", modifier = Modifier.size(12.dp), tint = TextGray)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = attendance.checkInTime ?: "--:--", fontSize = 12.sp, color = TextGray)
                }
            }
        }

        StatusBadge(status = attendance.status)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormAbsensiDialog(onDismiss: () -> Unit, onSave: (Long, String) -> Unit) {
    var userId by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("Hadir") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tambah Absensi", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = userId,
                    onValueChange = { userId = it },
                    label = { Text("User ID") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                    )
                )
                Text("Status:", fontWeight = FontWeight.Bold, color = DarkNavy)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatusChip(
                        label = "Hadir",
                        isSelected = status == "Hadir",
                        selectedColor = GreenIcon,
                        onClick = { status = "Hadir" },
                        modifier = Modifier.weight(1f)
                    )
                    StatusChip(
                        label = "Izin",
                        isSelected = status == "Izin",
                        selectedColor = YellowIcon,
                        onClick = { status = "Izin" },
                        modifier = Modifier.weight(1f)
                    )
                    StatusChip(
                        label = "Absen",
                        isSelected = status == "Absen",
                        selectedColor = RedIcon,
                        onClick = { status = "Absen" },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { 
                    userId.toLongOrNull()?.let { onSave(it, status) } 
                }, 
                colors = ButtonDefaults.buttonColors(containerColor = DarkNavy),
                enabled = userId.isNotBlank()
            ) { 
                Text("Simpan") 
            }
        },
        dismissButton = { 
            TextButton(onClick = onDismiss) { Text("Batal") } 
        }
    )
}

@Composable
fun StatusChip(
    label: String,
    isSelected: Boolean,
    selectedColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        color = if (isSelected) selectedColor.copy(alpha = 0.1f) else Color.Transparent,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(
            1.dp,
            if (isSelected) selectedColor else Color.LightGray
        ),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier.padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                color = if (isSelected) selectedColor else TextGray,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                fontSize = 12.sp
            )
        }
    }
}
