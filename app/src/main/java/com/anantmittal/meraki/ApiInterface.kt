package com.anantmittal.meraki

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

//https://api.unsplash.com/photos?client_id=F_S8mDPxMVJX4n4ohON-yDbyXZt9pKYP8Re923IpvEE&page=1
const val BASE_URL = "https://api.unsplash.com/"
const val client_id = "F_S8mDPxMVJX4n4ohON-yDbyXZt9pKYP8Re923IpvEE"

interface ApiInterface {
    @Headers("Authorization: Client-ID $client_id")
    @GET("/photos")
    fun data(
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
        @Query("orientation") orientation: String
    ): Call<List<WallpaperDataItem>>

    @Headers("Authorization: Client-ID $client_id")
    @GET("/search/photos")
    fun searchData(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
        @Query("orientation") orientation: String
    ): Call<WallpaperData>
}