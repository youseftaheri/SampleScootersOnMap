package com.yousef.sampleScootersOnMap.ui.mapFragment

import androidx.lifecycle.viewModelScope
import com.yousef.sampleScootersOnMap.data.DataManager
import com.yousef.sampleScootersOnMap.ui.base.BaseViewModel
import com.yousef.sampleScootersOnMap.utils.rx.SchedulerProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel
@Inject
constructor(dataManager: DataManager?, schedulerProvider: SchedulerProvider?) : BaseViewModel<MapNavigator?>(dataManager!!, schedulerProvider!!) {
    companion object {
        const val TAG = "MapViewModel"
    }

    fun findScooters(){
        viewModelScope.launch (Dispatchers.Main) { // launches coroutine in main thread
            try {
                val scootersPOJO = dataManager.requestScooterList(
                    "9ec3a017-1c9d-47aa-8c38-ead2bfa9b339",
                    "c284fd9a-c94e-4bfa-8f26-3a04ddf15b47"
                )
                if (scootersPOJO?.data != null)
                    navigator!!.setMarkers(scootersPOJO.data)
                else
                    navigator!!.handleError("Invalid Data")
            } catch (e: Exception) {
                navigator!!.handleError(e.message)
            }
        }
    }
}