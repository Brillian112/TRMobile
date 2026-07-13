package com.example.tr.data.remote.model

import com.google.gson.annotations.SerializedName

data class Category(
    val id: Long,
    val name: String,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?
)

data class CategoryRequest(
    val name: String
)

data class CategoryResponse(
    val success: Boolean,
    val message: String?,
    val data: List<Category>
)

data class SingleCategoryResponse(
    val success: Boolean,
    val message: String?,
    val data: Category
)
