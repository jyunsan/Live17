package com.example.live17.utils.api

import com.example.live17.utils.Constants
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET(Constants.SEARCH_USERS)
    suspend fun getUserList(
        @Query("q") name: String,
        @Query("page") page: Int,
        @Query("per_page") per_page: Int
    ): Response<JsonObject>
}