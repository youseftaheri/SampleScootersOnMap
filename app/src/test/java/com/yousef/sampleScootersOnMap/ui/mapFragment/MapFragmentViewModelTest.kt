package com.yousef.sampleScootersOnMap.ui.mapFragment

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.gson.Gson
import com.yousef.sampleScootersOnMap.data.DataManager
import com.yousef.sampleScootersOnMap.data.model.ScootersPOJO
import com.yousef.sampleScootersOnMap.data.testData.TestScooterList
import com.yousef.sampleScootersOnMap.data.testData.TestWrongScooterList
import com.yousef.sampleScootersOnMap.utils.CoroutineTestRule
import com.yousef.sampleScootersOnMap.utils.TestCoroutineRule
import com.yousef.sampleScootersOnMap.utils.rx.TestSchedulerProvider
import io.reactivex.schedulers.TestScheduler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner


@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MapFragmentViewModelTest {

    @get:Rule
    val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @get:Rule
    var coroutinesTestRule = CoroutineTestRule()

    @Mock
    var mSchedulesCallback: MapNavigator? = null

    @Mock
    var mMockDataManager: DataManager? = null
    private var mSchedulesViewModel: MapViewModel? = null
    private var mTestScheduler: TestScheduler? = null

    @Before
    @Throws(java.lang.Exception::class)
    open fun setUp(): Unit {
        mTestScheduler = TestScheduler()
        val testSchedulerProvider = TestSchedulerProvider(mTestScheduler!!)
        mSchedulesViewModel = MapViewModel(mMockDataManager, testSchedulerProvider)
        mSchedulesViewModel!!.setNavigator(mSchedulesCallback)
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        mTestScheduler = null
        mSchedulesViewModel = null
        mSchedulesCallback = null
    }

    @Test
    fun testFindScooters(){
        testCoroutineRule.runBlockingTest {
            val vehicleListResponse = Gson().fromJson(TestScooterList().data, ScootersPOJO::class.java)
            lenient().doReturn(vehicleListResponse)
                .`when`(mMockDataManager)
                ?.requestScooterList(anyString(), anyString())
            mSchedulesViewModel?.findScooters()
            verify(mSchedulesCallback)?.setMarkers(vehicleListResponse.data)
        }
    }

    @Test
    fun testFindScootersWithError(){
        testCoroutineRule.runBlockingTest {
            val vehicleListResponse = Gson().fromJson(TestWrongScooterList().data, ScootersPOJO::class.java)
            lenient().doReturn(vehicleListResponse)
                .`when`(mMockDataManager)
                ?.requestScooterList(anyString(), anyString())
            mSchedulesViewModel?.findScooters()
            verify(mSchedulesCallback)?.handleError(ArgumentMatchers.any())
        }
    }
}