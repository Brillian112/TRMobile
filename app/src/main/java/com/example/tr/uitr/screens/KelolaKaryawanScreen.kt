package com.example.tr.uitr.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tr.data.remote.model.User
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.tr.uitr.components.AppDrawer
import com.example.tr.uitr.components.StatusBadge
import com.example.tr.uitr.navigation.Screen
import com.example.tr.uitr.viewmodel.KaryawanViewModel
import com.example.tr.ui.theme.BgLight
import com.example.tr.ui.theme.DarkText
import com.example.tr.ui.theme.PrimaryBlue
import com.example.tr.ui.theme.PurplePrimary
import com.example.tr.ui.theme.TextGray
import com.example.tr.ui.theme.DarkNavy
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KelolaKaryawanScreen(navController: NavController, viewModel: KaryawanViewModel = viewModel()) {
    // STATE MANAGEMENT DARI VIEWMODEL
    val karyawanList = viewModel.employees

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Semua") }
    val categories = listOf("Semua", "Manager", "Employee")

    // State untuk Dialog Tambah/Edit/Hapus
    var showFormDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedKaryawan by remember { mutableStateOf<User?>(null) }

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
                    title = { Text("Kelola Karyawan", fontWeight = FontWeight.Bold, color = DarkNavy) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Menu", tint = DarkNavy)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = BgLight)
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        selectedKaryawan = null // Kosongkan state untuk Tambah baru
                        showFormDialog = true
                    },
                    containerColor = PurplePrimary,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = "Tambah")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Tambah Karyawan", fontWeight = FontWeight.Bold)
                    }
                }
            }
        ) { paddingValues ->
            if (viewModel.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item { Spacer(modifier = Modifier.height(4.dp)) }

                    // Search Bar
                    item {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Cari karyawan...", color = TextGray) },
                            leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = "Search", tint = TextGray) },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Filter Chips
                    item {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(categories) { category ->
                                CategoryChip(
                                    title = category,
                                    isSelected = category == selectedCategory,
                                    onClick = { selectedCategory = category }
                                )
                            }
                        }
                    }

                    // List Karyawan (Difilter berdasarkan kategori dan pencarian)
                    val filteredList = karyawanList.filter {
                        (selectedCategory == "Semua" || it.role == selectedCategory) &&
                                it.name.contains(searchQuery, ignoreCase = true)
                    }

                    items(filteredList) { karyawan ->
                        KaryawanCardItem(
                            karyawan = karyawan,
                            onEditClick = {
                                selectedKaryawan = karyawan
                                showFormDialog = true
                            },
                            onDeleteClick = {
                                selectedKaryawan = karyawan
                                showDeleteDialog = true
                            }
                        )
                    }

                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }

    // --- LOGIKA DIALOG (POP-UP) ---

    // Dialog Tambah/Edit
    if (showFormDialog) {
        FormKaryawanDialog(
            karyawan = selectedKaryawan,
            onDismiss = { showFormDialog = false },
            onSave = { nama, password, peran ->
                if (selectedKaryawan == null) {
                    viewModel.createEmployee(nama, password, peran)
                } else {
                    viewModel.updateEmployee(selectedKaryawan!!.id, nama, password, peran)
                }
                showFormDialog = false
            }
        )
    }

    // Dialog Konfirmasi Hapus
    if (showDeleteDialog && selectedKaryawan != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Konfirmasi Hapus") },
            text = { Text("Apakah Anda yakin ingin menghapus karyawan ${selectedKaryawan?.name}?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteEmployee(selectedKaryawan!!.id)
                        showDeleteDialog = false
                    }
                ) { Text("Hapus", color = Color.Red) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Batal") }
            }
        )
    }
}

@Composable
fun TopBarKaryawan() {
    // Deprecated in favor of Scaffold TopBar
}

@Composable
fun KaryawanCardItem(
    karyawan: User,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    // Generate warna avatar berdasarkan inisial (Contoh sederhana)
    val avatarColor = if (karyawan.name.startsWith("A", true)) Color(0xFFDCD6F7) else Color(0xFFD4B95E)
    val avatarTextColor = if (karyawan.name.startsWith("A", true)) PurplePrimary else Color.White

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Avatar Inisial
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(avatarColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = karyawan.name.take(1).uppercase(),
                            color = avatarTextColor,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(karyawan.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DarkText)
                        Text(karyawan.role, fontSize = 12.sp, color = TextGray)
                    }
                }

                // Badge Status (Berdasarkan role untuk demo)
                StatusBadge(status = karyawan.role)
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = BgLight, thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons (Edit & Hapus)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.clickable { onEditClick() },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.Edit, contentDescription = "Edit", tint = PrimaryBlue, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Edit", color = PrimaryBlue, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
                Spacer(modifier = Modifier.width(24.dp))
                Row(
                    modifier = Modifier.clickable { onDeleteClick() },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.Delete, contentDescription = "Hapus", tint = Color(0xFFD93025), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Hapus", color = Color(0xFFD93025), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

// Dialog Form Input (Untuk Tambah dan Edit)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormKaryawanDialog(
    karyawan: User?, // Jika null berarti "Tambah", jika ada isi berarti "Edit"
    onDismiss: () -> Unit,
    onSave: (String, String, String) -> Unit
) {
    var nama by remember { mutableStateOf(karyawan?.name ?: "") }
    var password by remember { mutableStateOf("") }
    var peran by remember { mutableStateOf(karyawan?.role ?: "Employee") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (karyawan == null) "Tambah Karyawan" else "Edit Karyawan", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = nama,
                    onValueChange = { nama = it },
                    label = { Text("Nama Karyawan") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Kata Sandi") },
                    modifier = Modifier.fillMaxWidth()
                )
                // Role Selection
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Peran: ", modifier = Modifier.weight(1f))
                    TextButton(onClick = { peran = "Manager" }, modifier = Modifier.weight(1f)) {
                        Text("Manager", color = if (peran == "Manager") PurplePrimary else Color.Gray)
                    }
                    TextButton(onClick = { peran = "Employee" }, modifier = Modifier.weight(1f)) {
                        Text("Employee", color = if (peran == "Employee") PurplePrimary else Color.Gray)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { if (nama.isNotBlank() && (karyawan != null || password.isNotBlank())) onSave(nama, password, peran) },
                colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary)
            ) { Text("Simpan") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}
