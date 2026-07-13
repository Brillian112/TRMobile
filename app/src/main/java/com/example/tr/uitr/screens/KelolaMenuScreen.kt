package com.example.tr.uitr.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tr.data.remote.model.Menu
import com.example.tr.data.remote.model.MenuRequest
import com.example.tr.uitr.viewmodel.MenuViewModel
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
fun KelolaMenuScreen(navController: NavController, viewModel: MenuViewModel = viewModel()) {

    LaunchedEffect(Unit) {
        viewModel.fetchMenus()
        viewModel.fetchCategories()
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

                    // Header dihapus karena sudah ada di TopBar
                    
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
                        val categoryName = menu.category?.name ?: apiCategories.find { it.id == menu.categoryId }?.name ?: "No Category"
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
            onSave = { name, description, price, isAvailable, categoryId ->
                val request = MenuRequest(name, description, price, isAvailable, categoryId, null)
                if (selectedMenu == null) {
                    viewModel.createMenu(request)
                } else {
                    viewModel.updateMenu(selectedMenu!!.id, request)
                }
                showFormDialog = false
            }
        )
    }
}

@Composable
fun TopBarMenu() {
    // Deprecated in favor of Scaffold TopBar
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
                Icon(
                    imageVector = Icons.Outlined.Restaurant,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.align(Alignment.Center).size(48.dp)
                )

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
    onSave: (String, String?, Double, Boolean, Long?) -> Unit
) {
    var name by remember { mutableStateOf(menu?.name ?: "") }
    var description by remember { mutableStateOf(menu?.description ?: "") }
    var price by remember { mutableStateOf(menu?.price?.toString() ?: "") }
    var isAvailable by remember { mutableStateOf(menu?.isAvailable ?: true) }
    var categoryId by remember { mutableStateOf(menu?.categoryId) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (menu == null) "Tambah Menu" else "Edit Menu") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nama") })
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Deskripsi") })
                OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Harga") })
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isAvailable, onCheckedChange = { isAvailable = it })
                    Text("Tersedia")
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
            Button(onClick = { onSave(name, description, price.toDoubleOrNull() ?: 0.0, isAvailable, categoryId) }) {
                Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}
