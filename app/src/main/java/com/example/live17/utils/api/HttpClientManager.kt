package com.example.live17.utils.api

import com.example.live17.utils.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class HttpClientManager private constructor() {
    private val retrofit: Retrofit
    private val logging = HttpLoggingInterceptor()
    private val okHttpClient: OkHttpClient

    init {
        logging.level = HttpLoggingInterceptor.Level.BODY

        okHttpClient = OkHttpClient().newBuilder()
            .connectTimeout(HTTP_CLIENT_TIMEOUT, TimeUnit.SECONDS)
            .addNetworkInterceptor(logging)
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(Constants.HOST)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    companion object {
        private const val HTTP_CLIENT_TIMEOUT = 30L
        private val manager = HttpClientManager()
        val apiService: ApiService
            get() = manager.retrofit.create(ApiService::class.java)
    }
}