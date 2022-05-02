package com.yousef.sampleScootersOnMap.data.remote

import com.yousef.sampleScootersOnMap.data.model.*
import retrofit2.http.*

interface Apis {
    @GET("v1/json/{first_param}/{second_param}?")
    suspend fun requestScooterList(@Path("first_param") firstParam: String,
                                   @Path("second_param") secondParam: String,
                                   @Query("apiKey") apiKey: String): ScootersPOJO?
}