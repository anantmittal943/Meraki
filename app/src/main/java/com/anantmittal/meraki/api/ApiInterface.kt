package com.anantmittal.meraki.api

import com.anantmittal.meraki.BuildConfig
import com.anantmittal.meraki.api.api_data_modals.WallpaperData
import com.anantmittal.meraki.api.api_data_modals.WallpaperDataItem
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

//https://api.unsplash.com/photos?client_id=F_S8mDPxMVJX4n4ohON-yDbyXZt9pKYP8Re923IpvEE&page=1
const val BASE_URL = "https://api.unsplash.com/"

interface ApiInterface {
    @GET("/photos")
    fun data(
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
        @Query("orientation") orientation: String,
        @Header("Authorization") authorization: String = "Client-ID ${BuildConfig.client_id}"
    ): Call<List<WallpaperDataItem>>

    @GET("/search/photos")
    fun searchData(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
        @Query("orientation") orientation: String,
        @Header("Authorization") authorization: String = "Client-ID ${BuildConfig.client_id}"
    ): Call<WallpaperData>
}