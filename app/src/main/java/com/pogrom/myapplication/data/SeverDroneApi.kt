package com.pogrom.myapplication.data

import com.pogrom.myapplication.models.DroneStatus
import retrofit2.http.GET
import retrofit2.http.POST

interface SeverDroneApi {
    @GET("control/map")
    suspend fun getMap(): MapResponse

    @POST("control/connect_drones")
    suspend fun connectDrones(): Boolean

    @POST("control/start_scan")
    suspend fun startScan(): Boolean

    @GET("control/status/")
    suspend fun getStatus(): String
}