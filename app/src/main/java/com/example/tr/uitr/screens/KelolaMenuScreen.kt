package com.example.tr.uitr.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.tr.data.remote.model.Menu
import com.example.tr.uitr.viewmodel.MenuViewModel
import com.example.tr.uitr.viewmodel.AuthViewModel
import com.example.tr.ui.theme.BgLight
import com.example.tr.ui.theme.DarkText
import com.example.tr.ui.theme.PurplePrimary
import com.example.tr.ui.theme.TextGray
import com.example.tr.ui.theme.DarkNavy
import com.example.tr.uitr.components.AppDrawer
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KelolaMenuScreen(
    navController: NavController,
    viewModel: MenuViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.fetchMenus()
        viewModel.fetchCategories()
    }
    LaunchedEffect(viewModel.errorMessage) {
        viewModel.errorMessage?.let { error ->
            android.widget.Toast.makeText(context, "Error: $error", android.widget.Toast.LENGTH_LONG).show()
            viewModel.clearErrorMessage()
        }
    }

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryName by remember { mutableStateOf("Semua") }

    val menuList = viewModel.menus
    val apiCategories = viewModel.categories

    var showFormDialog by remember { mutableStateOf(false) }
    var selectedMenu by remember { mutableStateOf<Menu?>(null) }

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
                    title = { Text("Kelola Menu", fontWeight = FontWeight.Bold, color = DarkNavy) },
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
                        selectedMenu = null
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
                        Text("Tambah Menu", fontWeight = FontWeight.Bold)
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
                            placeholder = { Text("Cari menu...", color = TextGray) },
                            leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = "Search", tint = TextGray) },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Filter Chips
                    item {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            item {
                                CategoryChip(
                                    title = "Semua",
                                    isSelected = selectedCategoryName == "Semua",
                                    onClick = { selectedCategoryName = "Semua" }
                                )
                            }
                            items(apiCategories) { category ->
                                CategoryChip(
                                    title = category.name,
                                    isSelected = category.name == selectedCategoryName,
                                    onClick = { selectedCategoryName = category.name }
                                )
                            }
                        }
                    }

                    // List Menu
                    val filteredList = menuList.filter { menu ->
                        val matchesCategory = if (selectedCategoryName == "Semua") {
                            true
                        } else {
                            val categoryName = menu.category?.name ?: apiCategories.find { it.id == menu.categoryId }?.name
                            categoryName == selectedCategoryName
                        }
                        matchesCategory && menu.name.contains(searchQuery, ignoreCase = true)
                    }

                    items(filteredList) { menu ->
                        // 1. Ambil nama kategori langsung dari objek relasi jika Laravel menyediakannya
                        // 2. Jika tidak ada, cari di list dengan mengonversi ke String + memangkas spasi (trim) untuk keamanan penuh
                        val categoryName = menu.category?.name
                            ?: apiCategories.find { it.id.toString().trim() == menu.categoryId?.toString()?.trim() }?.name
                            ?: "No Category"

                        MenuCardItem(
                            menu = menu,
                            categoryName = categoryName,
                            onEditClick = {
                                selectedMenu = menu
                                showFormDialog = true
                            },
                            onDeleteClick = {

                                viewModel.deleteMenu(menu.id)
                            }
                        )
                    }

                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }

    if (showFormDialog) {
        FormMenuDialog(
            menu = selectedMenu,
            categories = viewModel.categories,
            onDismiss = { showFormDialog = false },
            // Cari blok FormMenuDialog di KelolaMenuScreen.kt kamu dan sesuaikan bagian onSave:
            onSave = { name, description, price, categoryId, imageUri ->
                if (selectedMenu == null) {
                    // Logika Create Menu
                    if (imageUri == null) {
                        android.widget.Toast.makeText(context, "Gambar wajib dipilih!", android.widget.Toast.LENGTH_SHORT).show()
                    } else {
                        viewModel.createMenuMultipart(
                            context = context,
                            name = name,
                            description = description,
                            price = price,
                            categoryId = categoryId,
                            imageUri = imageUri
                        )
                        showFormDialog = false
                    }
                } else {
                    // Logika Update Menu -> Ambil URL gambar lama dari objek selectedMenu!!
                    viewModel.updateMenuMultipart(
                        context = context,
                        id = selectedMenu!!.id,
                        name = name,
                        description = description,
                        price = price,
                        categoryId = categoryId,
                        imageUri = imageUri,
                        existingImageUrl = selectedMenu!!.imageUrl // Teruskan gambar lama ke sini
                    )
                    showFormDialog = false
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryChip(title: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) PurplePrimary else Color.White,
        border = if (isSelected) null else BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Text(
            text = title,
            color = if (isSelected) Color.White else DarkText,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 13.sp,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
fun MenuCardItem(menu: Menu, categoryName: String, onEditClick: () -> Unit, onDeleteClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(Color(0xFFE2E8F0))
            ) {
                if (!menu.imageUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = menu.imageUrl,
                        contentDescription = menu.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Outlined.Restaurant,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.align(Alignment.Center).size(48.dp)
                    )
                }

                Surface(
                    color = Color.White.copy(alpha = 0.9f),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(
                        text = categoryName,
                        color = PurplePrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = menu.name,
                        color = DarkText,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Row {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = "Edit",
                            tint = DarkText,
                            modifier = Modifier.size(20.dp).clickable { onEditClick() }
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Icon(
                            imageVector = Icons.Outlined.DeleteOutline,
                            contentDescription = "Hapus",
                            tint = DarkText,
                            modifier = Modifier.size(20.dp).clickable { onDeleteClick() }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = menu.description ?: "",
                    color = TextGray,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Rp ${NumberFormat.getNumberInstance(Locale("id", "ID")).format(menu.price)}",
                    color = PurplePrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormMenuDialog(
    menu: Menu?,
    categories: List<com.example.tr.data.remote.model.Category>,
    onDismiss: () -> Unit,
    onSave: (name: String, description: String, price: Double, categoryId: Long, imageUri: Uri?) -> Unit
) {
    var name by remember { mutableStateOf(menu?.name ?: "") }
    var description by remember { mutableStateOf(menu?.description ?: "") }
    var price by remember { mutableStateOf(menu?.price?.toString() ?: "") }
    var categoryId by remember { mutableStateOf(menu?.categoryId) }

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (menu == null) "Tambah Menu" else "Edit Menu") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nama") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Deskripsi") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Harga") }, modifier = Modifier.fillMaxWidth())

                OutlinedCard(
                    onClick = { galleryLauncher.launch("image/*") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Filled.Image, contentDescription = null, tint = PurplePrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (selectedImageUri != null) "Gambar Terpilih ✓" else "Pilih Gambar Menu",
                            color = if (selectedImageUri != null) Color(0xFF2E7D32) else DarkText,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                var expanded by remember { mutableStateOf(false) }
                Box {
                    OutlinedTextField(
                        value = categories.find { it.id == categoryId }?.name ?: "Pilih Kategori",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Kategori") },
                        trailingIcon = {
                            IconButton(onClick = { expanded = true }) {
                                Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category.name) },
                                onClick = {
                                    categoryId = category.id
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalCategoryId = categoryId ?: (categories.firstOrNull()?.id ?: 0L)
                    onSave(
                        name,
                        description,
                        price.toDoubleOrNull() ?: 0.0,
                        finalCategoryId,
                        selectedImageUri
                    )
                },
                // Tombol Simpan hanya aktif jika input dasar telah terisi untuk memenuhi validasi API
                enabled = name.isNotBlank() && description.isNotBlank() && price.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary)
            ) {
                Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal", color = TextGray) }
        }
    )
}