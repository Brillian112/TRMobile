package com.example.tr.uitr.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tr.uitr.navigation.Screen
import androidx.navigation.NavController

@Composable
fun LoginScreen(navController: NavController) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

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
        Text(text = "Masuk sebagai Manajer", modifier = Modifier.padding(bottom = 32.dp))

        // Role Toggle (Manajer / Karyawan) - Sederhana menggunakan Row & Buttons
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)) {
            Button(onClick = { /* TODO */ }, modifier = Modifier.weight(1f)) {
                Text("Manajer")
            }
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedButton(onClick = { /* TODO */ }, modifier = Modifier.weight(1f)) {
                Text("Karyawan")
            }
        }

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Nama Pengguna") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Kata Sandi") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )

        TextButton(
            onClick = { /* Lupa sandi */ },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Lupa Kata Sandi?")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { navController.navigate(Screen.Dashboard.route) },
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("Masuk ->")
        }
    }
}