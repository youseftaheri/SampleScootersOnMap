package com.yousef.sampleScootersOnMap.data.remote

import com.yousef.sampleScootersOnMap.data.model.ScootersPOJO

interface ApiHelper {
    suspend fun requestScooterList(
        firstParam: String,
        secondParam: String): ScootersPOJO?
}