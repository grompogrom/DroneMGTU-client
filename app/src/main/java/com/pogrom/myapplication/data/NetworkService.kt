package com.pogrom.myapplication.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

object NetworkService {
    private var BASE_URL = "http://192.168.31.193:8000"
    private var retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    var serverDroneApi = retrofit.create<SeverDroneApi>()
    fun changeURL(newUrl: String){
        val url = "http://$newUrl"
        BASE_URL = url
        retrofit = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        serverDroneApi = retrofit.create<SeverDroneApi>()
    }
    fun getBaseUrl() = BASE_URL
}