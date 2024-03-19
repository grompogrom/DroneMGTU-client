package com.pogrom.myapplication

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pogrom.myapplication.data.NetworkService
import com.pogrom.myapplication.models.DroneStatus
import com.pogrom.myapplication.models.ServerStatusResponse
import com.pogrom.myapplication.tools.getServerConnectionStatus
import com.pogrom.myapplication.tools.readIpFromDataStore
import com.pogrom.myapplication.tools.writeIpToDataStore
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch


class MainViewModel: ViewModel() {
    val plotLiveData = MutableLiveData<List<List<Double>>>()
    val isMapUpdating = MutableLiveData<Boolean>(false)
    val dronesStatus = MutableLiveData<DroneStatus>(DroneStatus.DISCONNECTED)
    val serverStatusConnected = MutableLiveData<Boolean>(false)
    private var mapUpdaterTask: Job? = null
    private var serverAvailabilityTask: Job? = null
    private var statusFlow: Flow<ServerStatusResponse> = getServerConnectionStatus()

    init {

        serverAvailabilityTask =
        viewModelScope.launch {
            statusFlow.onEach {
                if (it is ServerStatusResponse.ServerOffline){
                    serverStatusConnected.postValue(false)
                    dronesStatus.postValue(DroneStatus.DISCONNECTED)
                }
                else {
                    serverStatusConnected.postValue(true)
                    dronesStatus.postValue(
                        (it as ServerStatusResponse.ServerOnline).droneStatus
                    )
                }
            }.collect()
        }
    }


    fun changeIp(ipString: String, context: Context){

        serverAvailabilityTask?.cancel()
        NetworkService.changeURL(ipString.trim())
        statusFlow = getServerConnectionStatus()
        serverAvailabilityTask =
            viewModelScope.launch {
                writeIpToDataStore(ipString, context)
                statusFlow
                    .onEach {
                        if (it is ServerStatusResponse.ServerOffline){
                            serverStatusConnected.postValue(false)
                            dronesStatus.postValue(DroneStatus.DISCONNECTED)
                        }
                        else if (it is ServerStatusResponse.ServerOnline){
                            serverStatusConnected.postValue(true)
                            dronesStatus.postValue(
                            it.droneStatus
                            )
                        }
                    }
                    .collect()
            }
    }


    private fun loadMapData(){
        if (serverStatusConnected.value == true)
            mapUpdaterTask = viewModelScope.launch {
                while (true) {
                    plotLiveData.postValue(
                        NetworkService.serverDroneApi.getMap().data
                    )
                    delay(200)
                }
            }
    }

    fun onButtonGetMapClick(){
        if (mapUpdaterTask?.isActive == true){
            mapUpdaterTask?.cancel()
            isMapUpdating.postValue(false)
        }
        else {
            loadMapData()
            isMapUpdating.postValue(true)
        }
    }


    fun connectToDrones(){
        if (serverStatusConnected.value == true && dronesStatus.value == DroneStatus.DISCONNECTED)
        viewModelScope.launch {
            try {
                val connected = NetworkService.serverDroneApi.connectDrones()
            }
            catch (e: java.lang.Exception){}
            catch (e: java.net.ConnectException){}
        }
    }

    fun startScan(){
        if (serverStatusConnected.value == true && dronesStatus.value == DroneStatus.CONNECTED)
        viewModelScope.launch {
            val scaned = NetworkService.serverDroneApi.startScan()
        }
    }

    override fun onCleared() {
        super.onCleared()
        serverAvailabilityTask?.cancel()
        mapUpdaterTask?.cancel()
    }
}