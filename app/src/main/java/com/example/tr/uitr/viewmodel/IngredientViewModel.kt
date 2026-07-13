package com.example.tr.uitr.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tr.data.remote.api.RetrofitClient
import com.example.tr.data.remote.model.Ingredient
import com.example.tr.data.remote.model.IngredientRequest
import kotlinx.coroutines.launch

class IngredientViewModel : ViewModel() {
    var ingredients = mutableStateListOf<Ingredient>()
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        fetchIngredients()
    }

    fun fetchIngredients() {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = RetrofitClient.instance.getIngredients()
                if (response.success) {
                    ingredients.clear()
                    ingredients.addAll(response.data)
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

    fun createIngredient(request: IngredientRequest) {
        viewModelScope.launch {
            try {
                RetrofitClient.instance.createIngredient(request)
                fetchIngredients()
            } catch (e: Exception) {
                errorMessage = e.message
            }
        }
    }

    fun updateIngredient(id: Long, request: IngredientRequest) {
        viewModelScope.launch {
            try {
                RetrofitClient.instance.updateIngredient(id, request)
                fetchIngredients()
            } catch (e: Exception) {
                errorMessage = e.message
            }
        }
    }

    fun deleteIngredient(id: Long) {
        viewModelScope.launch {
            try {
                RetrofitClient.instance.deleteIngredient(id)
                fetchIngredients()
            } catch (e: Exception) {
                errorMessage = e.message
            }
        }
    }
}
