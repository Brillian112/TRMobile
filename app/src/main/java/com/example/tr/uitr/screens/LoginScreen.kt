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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tr.uitr.navigation.Screen
import androidx.navigation.NavController
import com.example.tr.uitr.viewmodel.AuthViewModel

@Composable
fun LoginScreen(navController: NavController, viewModel: AuthViewModel = viewModel()) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
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
                viewModel.login(username, password) {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            },
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