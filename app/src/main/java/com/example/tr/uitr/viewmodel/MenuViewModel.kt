package com.example.tr.uitr.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tr.data.remote.api.RetrofitClient
import com.example.tr.data.remote.model.Category
import com.example.tr.data.remote.model.Menu
import com.example.tr.data.remote.model.MenuRequest
import kotlinx.coroutines.launch

class MenuViewModel : ViewModel() {
    var menus = mutableStateListOf<Menu>()
        private set

    var categories = mutableStateListOf<Category>()
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        fetchMenus()
        fetchCategories()
    }

    fun fetchMenus() {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = RetrofitClient.instance.getMenus()
                if (response.success) {
                    menus.clear()
                    menus.addAll(response.data)
                } else {
                    errorMessage = response.message
                }
            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }

    fun fetchCategories() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.getCategories()
                if (response.success) {
                    categories.clear()
                    categories.addAll(response.data)
                } else {
                    errorMessage = response.message
                }
            } catch (e: Exception) {
                errorMessage = e.message
            }
        }
    }

    fun createMenu(request: MenuRequest) {
        viewModelScope.launch {
            try {
                RetrofitClient.instance.createMenu(request)
                fetchMenus()
            } catch (e: Exception) {
                errorMessage = e.message
            }
        }
    }

    fun updateMenu(id: Long, request: MenuRequest) {
        viewModelScope.launch {
            try {
                RetrofitClient.instance.updateMenu(id, request)
                fetchMenus()
            } catch (e: Exception) {
                errorMessage = e.message
            }
        }
    }

    fun deleteMenu(id: Long) {
        viewModelScope.launch {
            try {
                RetrofitClient.instance.deleteMenu(id)
                fetchMenus()
            } catch (e: Exception) {
                errorMessage = e.message
            }
        }
    }
}
