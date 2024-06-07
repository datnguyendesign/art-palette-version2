package com.example.artpaletteversion2.api

import com.example.artpaletteversion2.api.ApiUtilities.Companion.API_KEY
import com.example.artpaletteversion2.model.SearchModel
import com.example.artpaletteversion2.model.UrlImageModel
import retrofit2.http.GET
import retrofit2.Call
import retrofit2.http.Headers
import retrofit2.http.Query

interface ApiInterface {
    @Headers("Authorization: Client-ID $API_KEY")
    @GET("/photos")
    fun getImages(
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): Call<List<UrlImageModel>>

    @Headers("Authorization: Client-ID $API_KEY")
    @GET("/search/photos")
    fun searchImage(
        @Query("query") query: String
    ): Call<SearchModel>
}
