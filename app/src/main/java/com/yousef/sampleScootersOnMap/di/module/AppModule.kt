package com.yousef.sampleScootersOnMap.di.module

import android.app.Application
import android.content.Context
import com.yousef.sampleScootersOnMap.data.AppDataManager
import com.yousef.sampleScootersOnMap.data.DataManager
import com.yousef.sampleScootersOnMap.data.remote.ApiHelper
import com.yousef.sampleScootersOnMap.data.remote.Apis
import com.yousef.sampleScootersOnMap.data.remote.AppApiHelper
import com.yousef.sampleScootersOnMap.utils.CommonUtils
import com.yousef.sampleScootersOnMap.utils.rx.AppSchedulerProvider
import com.yousef.sampleScootersOnMap.utils.rx.SchedulerProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    @Singleton
    fun provideApiHelper(appApiHelper: AppApiHelper): ApiHelper {
        return appApiHelper
    }

    @Provides
    @Singleton
    fun provideApi(retrofit: Retrofit): Apis {
        return retrofit.create(Apis::class.java)
    }


    @Provides
    @Singleton
    fun provideContext(application: Application): Context {
        return application
    }

    @Provides
    @Singleton
    fun provideDataManager(appDataManager: AppDataManager): DataManager {
        return appDataManager
    }


    @Provides
    fun provideSchedulerProvider(): SchedulerProvider {
        return AppSchedulerProvider()
    }

    @Singleton
    @Provides
    fun provideUtils(context: Context?): CommonUtils {
        return CommonUtils
    }
}