package com.yousef.sampleScootersOnMap.ui.splashFragment

import com.yousef.sampleScootersOnMap.data.DataManager
import com.yousef.sampleScootersOnMap.ui.base.BaseViewModel
import com.yousef.sampleScootersOnMap.utils.rx.SchedulerProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel
@Inject
constructor(dataManager: DataManager?, schedulerProvider: SchedulerProvider?) :
    BaseViewModel<SplashNavigator?>(dataManager!!, schedulerProvider!!) {}