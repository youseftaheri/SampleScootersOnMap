package com.yousef.sampleScootersOnMap.data

import com.yousef.sampleScootersOnMap.data.model.*
import com.yousef.sampleScootersOnMap.data.remote.ApiHelper
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppDataManager
@Inject constructor(
    private val mApiHelper: ApiHelper
) : DataManager {

    override suspend fun requestScooterList(
        firstParam: String,
        secondParam: String): ScootersPOJO? {
        return mApiHelper.requestScooterList(firstParam, secondParam)
    }
}