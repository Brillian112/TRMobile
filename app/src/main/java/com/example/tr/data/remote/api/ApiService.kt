package com.example.tr.data.remote.api

import com.example.tr.data.remote.model.*
import retrofit2.Response
import retrofit2.http.*
import okhttp3.MultipartBody
import okhttp3.RequestBody



interface ApiService {
    // Authentication
    @POST("api/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @DELETE("api/logout")
    suspend fun logout(): Response<Unit>

    @POST("api/register")
    suspend fun register(@Body request: RegisterRequest): Response<User>

    // Users
    @GET("api/users")
    suspend fun getUsers(): UserResponse

    @GET("api/users/{id}")
    suspend fun getUserById(@Path("id") id: Long): SingleUserResponse

    @POST("api/users")
    suspend fun createUser(@Body request: RegisterRequest): SingleUserResponse

    @PUT("api/users/{id}")
    suspend fun updateUser(@Path("id") id: Long, @Body request: RegisterRequest): SingleUserResponse

    @DELETE("api/users/{id}")
    suspend fun deleteUser(@Path("id") id: Long): Response<Unit>

    // Categories
    @GET("api/categories")
    suspend fun getCategories(): CategoryResponse

    @POST("api/categories")
    suspend fun createCategory(@Body request: CategoryRequest): SingleCategoryResponse

    @PUT("api/categories/{id}")
    suspend fun updateCategory(@Path("id") id: Long, @Body request: CategoryRequest): SingleCategoryResponse

    @DELETE("api/categories/{id}")
    suspend fun deleteCategory(@Path("id") id: Long): Response<Unit>

    // Menu
    @GET("api/menu")
    suspend fun getMenus(): MenuResponse

    @GET("api/menu/{id}")
    suspend fun getMenuById(@Path("id") id: Long): SingleMenuResponse

    @POST("api/menu")
    suspend fun createMenu(@Body request: MenuRequest): SingleMenuResponse

    @PUT("api/menu/{id}")
    suspend fun updateMenu(@Path("id") id: Long, @Body request: MenuRequest): SingleMenuResponse

    @DELETE("api/menu/{id}")
    suspend fun deleteMenu(@Path("id") id: Long): Response<Unit>

    @GET("api/menu/{id}/check-stock")
    suspend fun checkStock(@Path("id") id: Long): Response<Unit>

    // Ingredients
    @GET("api/ingredients")
    suspend fun getIngredients(): IngredientResponse

    @Multipart
    @POST("api/ingredients")
    suspend fun createIngredient(
        @Part name: MultipartBody.Part,
        @Part unit: MultipartBody.Part,
        @Part quantity: MultipartBody.Part,
        @Part gambar: MultipartBody.Part?
    ): SingleIngredientResponse

    @Multipart
    @PUT("api/ingredients/{id}")
    suspend fun updateIngredient(
        @Path("id") id: Long,
        @Part method: MultipartBody.Part,
        @Part name: MultipartBody.Part,
        @Part unit: MultipartBody.Part,
        @Part quantity: MultipartBody.Part,
        @Part gambar: MultipartBody.Part // 🔴 Selalu kirim part ini (tidak boleh null)
    ): SingleIngredientResponse

    @PUT("api/ingredients/{id}")
    suspend fun updateIngredientJson(
        @Path("id") id: Long,
        @Body request: UpdateIngredientTextRequest
    ): SingleIngredientResponse

    // Data class pembungkus payload JSON (Sesuaikan tipe data stok ke Double/Int sesuai backend)
    data class UpdateIngredientTextRequest(
        val nama: String,
        val unit: String,
        val stok: String
    )

    @DELETE("api/ingredients/{id}")
    suspend fun deleteIngredient(@Path("id") id: Long): Response<Unit>

    // Transactions
    @GET("api/transactions")
    suspend fun getTransactions(): TransactionResponse

    @GET("api/transactions/{id}")
    suspend fun getTransactionById(@Path("id") id: Long): SingleTransactionResponse

    @POST("api/transactions")
    suspend fun createTransaction(@Body request: TransactionRequest): TransactionResponse

    @PUT("api/transactions/{id}/status")
    suspend fun updateTransactionStatus(@Path("id") id: Long, @Query("status") status: String): SingleTransactionResponse

    // Attendances
    @GET("api/attendances")
    suspend fun getAttendances(): AttendanceResponse

    @POST("api/attendances/check-in")
    suspend fun checkIn(@Body body: Map<String, String>): Response<AttendanceResponse>

    @PUT("api/attendances/check-out")
    suspend fun checkOut(@Body body: Map<String, String>): Response<AttendanceResponse>

    @POST("api/attendances")
    suspend fun createAttendance(@Body request: AttendanceRequest): SingleAttendanceResponse

    // Manager
    @GET("api/manager/dashboard")
    suspend fun getManagerDashboard(): Map<String, Any>

    @GET("api/manager/employees")
    suspend fun getManagerEmployees(): UserResponse

    //rout katrawan


}
