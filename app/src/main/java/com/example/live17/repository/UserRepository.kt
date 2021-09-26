package com.example.live17.repository

import com.example.live17.model.SearchResult
import com.example.live17.utils.api.HttpClientManager
import com.google.gson.Gson
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserRepository {

    interface OnUserListCallback {
        fun getUserList(result: SearchResult)
    }

    companion object {
        private val gson = Gson()
    }

    fun getUserList(
        name: String,
        page: Int,
        pre_page: Int,
        callback: OnUserListCallback) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = HttpClientManager.apiService.getUserList(name, page, pre_page)
            var searchResult = SearchResult(false, ArrayList(), 0)
            if(result.code() == 200){
                searchResult = SearchResult(false, ArrayList(), 0)
                searchResult = gson.fromJson(
                    result.body().toString(),
                    SearchResult::class.java
                )
                searchResult.isSuccess = true
            } else {
                kotlin.runCatching {
                    val response = JsonParser().parse(result.errorBody()!!.string()).asJsonObject
                    var errorMsg = ""
                    if (response.has("message")) {
                        errorMsg = response.get("message").asString
                    }
                    searchResult.errorMessage = "(${result.code()}) Message: $errorMsg"
                    searchResult.isSuccess = false
                }
            }

            withContext(Dispatchers.Main) {
                callback.getUserList(searchResult)
            }
        }
    }
}