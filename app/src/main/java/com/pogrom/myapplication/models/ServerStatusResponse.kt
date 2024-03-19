package com.pogrom.myapplication.models

sealed class ServerStatusResponse {
    data class ServerOnline(
        val droneStatus: DroneStatus
    ) : ServerStatusResponse()
    object ServerOffline: ServerStatusResponse()
}