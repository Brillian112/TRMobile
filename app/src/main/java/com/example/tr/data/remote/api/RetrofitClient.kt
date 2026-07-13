package com.example.tr.data.remote.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://kasirku-sand.vercel.app/"
    
    // Simpan token sementara di sini agar bisa diakses semua API
    var authToken: String? = null

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()
                .header("Accept", "application/json")
            
            // Jika ada token, masukkan ke Header
            authToken?.let {
                requestBuilder.header("Authorization", "Bearer $it")
            }

            val request = requestBuilder.method(original.method, original.body).build()
            chain.proceed(request)
        }
        .build()

    val instance: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

        retrofit.create(ApiService::class.java)
    }
}
