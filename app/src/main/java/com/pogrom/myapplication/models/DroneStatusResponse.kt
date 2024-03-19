package com.pogrom.myapplication.models


    fun String.toDroneStatus() = when(this){
        "disconnected" -> DroneStatus.DISCONNECTED
        "connected" -> DroneStatus.CONNECTED
        "flying" -> DroneStatus.FLYING
        else -> DroneStatus.DISCONNECTED
    }