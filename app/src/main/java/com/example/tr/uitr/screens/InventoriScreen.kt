package com.example.tr.uitr.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tr.data.remote.model.Ingredient
import com.example.tr.data.remote.model.IngredientRequest
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.tr.uitr.components.AppDrawer
import com.example.tr.uitr.viewmodel.IngredientViewModel
import com.example.tr.uitr.viewmodel.AuthViewModel
import com.example.tr.ui.theme.RedBg
import com.example.tr.ui.theme.RedText
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.layout.ContentScale
import com.example.tr.uitr.components.StatusBadge

private val DarkNavy = Color(0xFF1A365D)
private val BgLight = Color(0xFFF8F9FE)
private val TextGray = Color(0xFF7A869A)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoriScreen(
    navController: NavController,
    viewModel: IngredientViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    val userRole = authViewModel.user?.role ?: ""
    val ingredients = viewModel.ingredients
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Semua") }
    val filters = listOf("Semua", "Stok Rendah", "Habis")

    var showFormDialog by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf<Ingredient?>(null) }

    val filteredList = ingredients.filter { item ->
        val status = when {
            item.stockQuantity <= 0.0 -> "Habis"
            item.stockQuantity <= 5.0 -> "Stok Rendah"
            else -> "Cukup"
        }
        val matchFilter = when (selectedFilter) {
            "Stok Rendah" -> status == "Stok Rendah"
            "Habis" -> status == "Habis"
            else -> true
        }
        matchFilter && item.name.contains(searchQuery, ignoreCase = true)
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
                    title = { Text("Inventori", fontWeight = FontWeight.Bold, color = DarkNavy) },
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
                        selectedItem = null
                        showFormDialog = true
                    },
                    containerColor = DarkNavy,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = "Tambah")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Tambah Item", fontWeight = FontWeight.Bold)
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

                    item {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Cari item...", color = TextGray) },
                            leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = "Search", tint = TextGray) },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().height(56.dp)
                        )
                    }

                    item {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(filters) { filter ->
                                FilterChipInventori(
                                    title = filter,
                                    isSelected = filter == selectedFilter,
                                    onClick = { selectedFilter = filter }
                                )
                            }
                        }
                    }

                    items(filteredList) { ingredient ->
                        InventoriCardItem(
                            ingredient = ingredient,
                            onEditClick = {
                                selectedItem = ingredient
                                showFormDialog = true
                            }
                        )
                    }

                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }

    if (showFormDialog) {
        FormInventoriDialog(
            item = selectedItem,
            onDismiss = { showFormDialog = false },
            onSave = { name, quantity, unit, imageUri ->
                // CATATAN PENTING:
                // Karena API meminta File (multipart), kamu harus mengubah fungsi di ViewModel
                // agar bisa menerima 'imageUri' dan mengubahnya menjadi MultipartBody.Part

                // Untuk sementara, jika ViewModel belum diupdate, kodenya seperti ini:
                val request = IngredientRequest(name, quantity, unit, imageUri?.toString())
                if (selectedItem == null) {
                    viewModel.createIngredient(request) // Idealnya: viewModel.createIngredient(name, quantity, unit, imageUri, context)
                } else {
                    viewModel.updateIngredient(selectedItem!!.id, request)
                }
                showFormDialog = false
            }
        )
    }
}


@Composable
fun TopBarInventori() {
    // Deprecated
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterChipInventori(title: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) DarkNavy else Color.White,
        border = if (isSelected) null else BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Text(
            text = title,
            color = if (isSelected) Color.White else TextGray,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 13.sp,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
fun InventoriCardItem(ingredient: Ingredient, onEditClick: () -> Unit) {
    val status = when {
        ingredient.stockQuantity <= 0.0 -> "Habis"
        ingredient.stockQuantity <= 5.0 -> "Rendah"
        else -> "Cukup"
    }
    val isHabis = status == "Habis"

    val formattedJumlah = if (ingredient.stockQuantity % 1.0 == 0.0) {
        ingredient.stockQuantity.toInt().toString()
    } else {
        ingredient.stockQuantity.toString()
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, if (isHabis) RedBg else Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Column {
                if (!ingredient.imageUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(ingredient.imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Gambar ${ingredient.name}",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFE2E8F0))
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFE2E8F0)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Outlined.Inventory, contentDescription = "Image", tint = TextGray)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(text = ingredient.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DarkNavy)
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "$formattedJumlah ${ingredient.unit}",
                    fontSize = 13.sp,
                    color = if (isHabis) RedText else TextGray,
                    fontWeight = if (isHabis) FontWeight.Bold else FontWeight.Normal
                )

                Spacer(modifier = Modifier.height(12.dp))

                StatusBadge(status = status)
            }

            Icon(
                imageVector = Icons.Outlined.Edit,
                contentDescription = "Edit",
                tint = DarkNavy,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(20.dp)
                    .clickable { onEditClick() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormInventoriDialog(
    item: Ingredient?,
    onDismiss: () -> Unit,
    onSave: (String, Double, String, Uri?) -> Unit // <-- Tambahkan parameter Uri?
) {
    var nama by remember { mutableStateOf(item?.name ?: "") }
    var jumlahInput by remember { mutableStateOf(item?.stockQuantity?.toString() ?: "") }
    var satuan by remember { mutableStateOf(item?.unit ?: "kg") }

    // State untuk menyimpan URI gambar yang dipilih dari galeri
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    // Launcher untuk membuka Galeri HP
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (item == null) "Tambah Item" else "Edit Item", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                // --- KOTAK PEMILIH GAMBAR ---
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFE2E8F0))
                        .clickable { launcher.launch("image/*") }, // Buka galeri saat diklik
                    contentAlignment = Alignment.Center
                ) {
                    if (imageUri != null) {
                        // Tampilkan preview gambar yang baru dipilih
                        AsyncImage(
                            model = imageUri,
                            contentDescription = "Selected Image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else if (!item?.imageUrl.isNullOrEmpty()) {
                        // Tampilkan gambar lama jika sedang mode Edit
                        AsyncImage(
                            model = item!!.imageUrl,
                            contentDescription = "Current Image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        // Placeholder jika belum ada gambar
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Outlined.AddPhotoAlternate, contentDescription = "Pick Image", tint = TextGray, modifier = Modifier.size(32.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Klik untuk pilih gambar *", fontSize = 12.sp, color = TextGray)
                        }
                    }
                }

                OutlinedTextField(
                    value = nama,
                    onValueChange = { nama = it },
                    label = { Text("Nama Item *") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = jumlahInput,
                        onValueChange = { jumlahInput = it },
                        label = { Text("Jumlah *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = satuan,
                        onValueChange = { satuan = it },
                        label = { Text("Satuan *") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val jumlah = jumlahInput.toDoubleOrNull() ?: 0.0
                    // Validasi: pastikan nama terisi (gambar idealnya juga divalidasi jika wajib)
                    if (nama.isNotBlank()) {
                        onSave(nama, jumlah, satuan, imageUri)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = DarkNavy)
            ) { Text("Simpan") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}
