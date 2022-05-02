package com.yousef.sampleScootersOnMap.ui

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents.init
import androidx.test.espresso.intent.Intents.release
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.GrantPermissionRule
import com.yousef.sampleScootersOnMap.R
import com.yousef.sampleScootersOnMap.ui.mainActivity.MainActivity
import org.hamcrest.Matcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class MainActivityUITest {

    @Rule
    @JvmField
    val rule = ActivityScenarioRule(MainActivity::class.java)

    @Rule
    @JvmField
    var mGrantPermissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(
            "android.permission.INTERNET",
            "android.permission.ACCESS_WIFI_STATE",
            "android.permission.ACCESS_NETWORK_STATE"
        )

    @Test
    fun testMainActivity(){
        init()
        onView(withId(R.id.rlSplashContent)).check(matches(isDisplayed()))
        onView(withId(R.id.ivSplashLogo)).check(matches(isDisplayed()))
        onView(withId(R.id.tvWebsite)).check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(2000))
        onView(withId(R.id.progressDialog)).check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(6000))
        onView(withId(R.id.recyclerView)).check(matches(isDisplayed()))
        onView(ViewMatchers.withId(R.id.recyclerView)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>
                (0, ClickOnButtonView()))

        onView(isRoot()).perform(waitFor(1000))
        onView(withText("yes"))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))
            .perform(click())

        onView(withId(R.id.map)).check(matches(isDisplayed()))
        release()
    }

    inner class ClickOnButtonView : ViewAction {
        internal var click = ViewActions.click()

        override fun getConstraints(): Matcher<View> {
            return click.constraints
        }

        override fun getDescription(): String {
            return " click on custom button view"
        }

        override fun perform(uiController: UiController, view: View) {
            click.perform(uiController, view.findViewById(R.id.cvVehicleItem))
        }
    }

    /**
     * Perform action of waiting for a specific time.
     */
    private fun waitFor(millis: Long): ViewAction? {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return ViewMatchers.isRoot()
            }

            override fun getDescription(): String {
                return "Wait for $millis milliseconds."
            }

            override fun perform(uiController: UiController, view: View?) {
                uiController.loopMainThreadForAtLeast(millis)
            }
        }
    }
}
