package com.example.tr.data.remote.model

import com.google.gson.annotations.SerializedName

data class Menu(
    val id: Long,
    val name: String,
    val description: String?,
    val price: Double,
    @SerializedName("is_available") val isAvailable: Boolean?,
    @SerializedName("category_id") val categoryId: Long?,
    @SerializedName("image_url") val imageUrl: String?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?,
    val category: Category?
)

data class MenuRequest(
    val name: String,
    val description: String?,
    val price: Double,
    @SerializedName("is_available") val isAvailable: Boolean?,
    @SerializedName("category_id") val categoryId: Long?,
    @SerializedName("image_url") val imageUrl: String?
)

data class MenuResponse(
    val success: Boolean,
    val message: String?,
    val data: List<Menu>
)

data class SingleMenuResponse(
    val success: Boolean,
    val message: String?,
    val data: Menu
)
