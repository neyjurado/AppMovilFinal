package com.example.appmovilfinal

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface RecetasApiService {
    @GET("recipes")
    suspend fun obtenerRecetasTop(): RespuestaRecetasApi
}

object RetrofitClient {
    val api: RecetasApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://dummyjson.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RecetasApiService::class.java)
    }
}
