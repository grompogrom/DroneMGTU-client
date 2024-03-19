package com.pogrom.myapplication.tools

import android.util.Log
import com.pogrom.myapplication.data.NetworkService
import com.pogrom.myapplication.models.ServerStatusResponse
import com.pogrom.myapplication.models.toDroneStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

fun getServerConnectionStatus(): Flow<ServerStatusResponse> = flow {
    while (true) {
        kotlinx.coroutines.delay(1500)
        val status = statusUpdate()
        emit(status)
    }
}.flowOn(Dispatchers.IO)

suspend fun statusUpdate(): ServerStatusResponse{
    try {
        val response = NetworkService.serverDroneApi.getStatus()
        return ServerStatusResponse.ServerOnline(response.toDroneStatus())
    }
    catch (e: Exception){
        Log.e("response", "err", e)
        return ServerStatusResponse.ServerOffline
    }
}



