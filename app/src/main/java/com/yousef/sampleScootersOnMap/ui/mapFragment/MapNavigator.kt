package com.yousef.sampleScootersOnMap.ui.mapFragment

import com.yousef.sampleScootersOnMap.data.model.ScootersPOJO

interface MapNavigator {
    fun handleError(exception: String?)
    fun setMarkers(scooters: List<ScootersPOJO.Scooter>?)
}