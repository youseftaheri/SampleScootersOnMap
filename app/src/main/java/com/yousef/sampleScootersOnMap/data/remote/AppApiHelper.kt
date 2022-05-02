package com.yousef.sampleScootersOnMap.data.remote

import android.content.Context
import com.yousef.sampleScootersOnMap.Secrets
import com.yousef.sampleScootersOnMap.data.model.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppApiHelper
@Inject constructor(
    private val apis: Apis,
) : ApiHelper {

    override suspend fun requestScooterList(
        firstParam: String,
        secondParam: String): ScootersPOJO? {
        return apis.requestScooterList(firstParam, secondParam,
            Secrets().getApiKey("com.yousef.sampleScootersOnMap")
        )
    }
}