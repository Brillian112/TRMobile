package com.example.tr.data.remote.model

import com.google.gson.annotations.SerializedName

data class Ingredient(
    val id: Long,
    val name: String,
    @SerializedName("stock_quantity") val stockQuantity: Double,
    val unit: String,
    @SerializedName("image_url") val imageUrl: String?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?
)

data class IngredientRequest(
    val name: String,
    @SerializedName("stock_quantity") val stockQuantity: Double,
    val unit: String,
    @SerializedName("image_url") val imageUrl: String?
)

data class IngredientResponse(
    val success: Boolean,
    val message: String?,
    val data: List<Ingredient>
)

data class SingleIngredientResponse(
    val success: Boolean,
    val message: String?,
    val data: Ingredient
)
