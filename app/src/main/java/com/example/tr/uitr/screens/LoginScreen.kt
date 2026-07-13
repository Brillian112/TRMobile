package com.example.tr.uitr.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tr.uitr.navigation.Screen
import androidx.navigation.NavController
import com.example.tr.uitr.viewmodel.AuthViewModel

@Composable
fun LoginScreen(navController: NavController, viewModel: AuthViewModel = viewModel()) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    //pemilihan role
    var isKaryawanSelected by remember { mutableStateOf(true) }

    //warna dasar
    // Definisikan warna dasar
    val warnaManajerBiru = Color(0xFF1A3E72) // Warna Biru untuk Manajer
    val warnaKaryawanHijau = Color(0xFF0F6E52) // Warna Hijau sesuai mockup gambar
    val warnaAbuBackgroundTab = Color(0xFFF4F4F6) // Warna background abu-abu untuk tab row

    var selectedRole by remember { mutableStateOf("Manager") }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "KasirKu",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        // Ubah Text subtitle lama kamu menjadi seperti ini:
        Text(
            text = if (isKaryawanSelected) "Masuk sebagai Karyawan" else "Masuk sebagai Manajer",
            modifier = Modifier.padding(bottom = 32.dp)
        )
        // Role Toggle (Manajer / Karyawan) - Sederhana menggunakan Row & Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(warnaAbuBackgroundTab, shape = RoundedCornerShape(12.dp))
                .padding(4.dp)
        ) {
            // TOMBOL MANAJER
            Button(
                onClick = { isKaryawanSelected = false },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    // JIKA MANAJER AKTIF (!isKaryawanSelected), WARNA JADI BIRU. JIKA TIDAK, TRANSPARAN
                    containerColor = if (!isKaryawanSelected) warnaManajerBiru else Color.Transparent,
                    contentColor = if (!isKaryawanSelected) Color.White else Color(0xFF333333)
                ),
                shape = RoundedCornerShape(8.dp),
                elevation = if (!isKaryawanSelected) ButtonDefaults.buttonElevation(defaultElevation = 2.dp) else null
            ) {
                Text("Manajer", fontWeight = if (!isKaryawanSelected) FontWeight.Bold else FontWeight.Normal)
            }

            // TOMBOL KARYAWAN
            Button(
                onClick = { isKaryawanSelected = true },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    // JIKA KARYAWAN AKTIF (isKaryawanSelected), WARNA JADI HIJAU. JIKA TIDAK, TRANSPARAN
                    containerColor = if (isKaryawanSelected) warnaKaryawanHijau else Color.Transparent,
                    contentColor = if (isKaryawanSelected) Color.White else Color(0xFF333333)
                ),
                shape = RoundedCornerShape(8.dp),
                elevation = if (isKaryawanSelected) ButtonDefaults.buttonElevation(defaultElevation = 2.dp) else null
            ) {
                Text("Karyawan", fontWeight = if (isKaryawanSelected) FontWeight.Bold else FontWeight.Normal)
        Text(text = "Masuk sebagai $selectedRole", modifier = Modifier.padding(bottom = 32.dp))

        // Role Toggle (Manajer / Karyawan)
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)) {
            if (selectedRole == "Manager") {
                Button(onClick = { selectedRole = "Manager" }, modifier = Modifier.weight(1f)) {
                    Text("Manajer")
                }
            } else {
                OutlinedButton(onClick = { selectedRole = "Manager" }, modifier = Modifier.weight(1f)) {
                    Text("Manajer")
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            if (selectedRole == "Employee") {
                Button(onClick = { selectedRole = "Employee" }, modifier = Modifier.weight(1f)) {
                    Text("Karyawan")
                }
            } else {
                OutlinedButton(onClick = { selectedRole = "Employee" }, modifier = Modifier.weight(1f)) {
                    Text("Karyawan")
                }
            }
        }

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Nama Pengguna") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            enabled = !viewModel.isLoading
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Kata Sandi") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            enabled = !viewModel.isLoading
        )

        viewModel.errorMessage?.let {
            Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(vertical = 8.dp))
        }

        TextButton(
            onClick = { /* Lupa sandi */ },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Lupa Kata Sandi?")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                // PERIKSA SIAPA YANG SEDANG LOGIN
                if (isKaryawanSelected) {
                    // JIKA KARYAWAN/KASIR AKTIF: Pergi ke Dashboard Kasir
                    navController.navigate(Screen.DashboardKasir.route) {
                        // Opsional: Hapus halaman login dari backstack agar ketika ditekan tombol back tidak kembali ke login
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                } else {
                    // JIKA MANAJER AKTIF: Pergi ke Dashboard Manager
                    navController.navigate(Screen.DashboardManager.route) {
                viewModel.login(username, password) {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isKaryawanSelected) warnaKaryawanHijau else warnaManajerBiru,
                contentColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Masuk ->", fontWeight = FontWeight.Bold)
            modifier = Modifier.fillMaxWidth().height(50.dp),
            enabled = !viewModel.isLoading && username.isNotBlank() && password.isNotBlank()
        ) {
            if (viewModel.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text("Masuk ->")
            }
        }
    }
}